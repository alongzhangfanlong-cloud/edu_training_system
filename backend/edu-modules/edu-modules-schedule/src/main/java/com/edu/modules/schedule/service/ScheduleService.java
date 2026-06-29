package com.edu.modules.schedule.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.edu.common.core.exception.BusinessException;
import com.edu.common.core.exception.ErrorCode;
import com.edu.common.core.utils.RedisDistributedLock;
import com.edu.modules.course.entity.ClassEntity;
import com.edu.modules.course.entity.CourseEntity;
import com.edu.modules.course.entity.TeacherEntity;
import com.edu.modules.course.entity.ClassroomEntity;
import com.edu.modules.course.mapper.ClassMapper;
import com.edu.modules.course.mapper.CourseMapper;
import com.edu.modules.course.mapper.TeacherMapper;
import com.edu.modules.course.mapper.ClassroomMapper;
import com.edu.modules.schedule.dto.*;
import com.edu.modules.schedule.entity.ScheduleEntity;
import com.edu.modules.schedule.mapper.ScheduleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleMapper scheduleMapper;
    private final ClassMapper classMapper;
    private final CourseMapper courseMapper;
    private final TeacherMapper teacherMapper;
    private final ClassroomMapper classroomMapper;
    private final RedisDistributedLock distributedLock;

    /**
     * 查询课表
     */
    public List<ScheduleDTO> query(LocalDate startDate, LocalDate endDate,
                                    Long teacherId, Long classroomId,
                                    Long classId, Long studentId) {
        // 按学员ID过滤：通过student_classes关联表查询
        List<ScheduleEntity> list;
        if (studentId != null && startDate != null && endDate != null) {
            list = scheduleMapper.findByStudentId(studentId, startDate, endDate);
            // 如果还指定了其他过滤条件，在内存中过滤
            list = list.stream()
                .filter(s -> teacherId == null || teacherId.equals(s.getTeacherId()))
                .filter(s -> classroomId == null || classroomId.equals(s.getClassroomId()))
                .filter(s -> classId == null || classId.equals(s.getClassId()))
                .toList();
        } else {
            LambdaQueryWrapper<ScheduleEntity> wrapper = new LambdaQueryWrapper<>();
            if (startDate != null) wrapper.ge(ScheduleEntity::getDate, startDate);
            if (endDate != null) wrapper.le(ScheduleEntity::getDate, endDate);
            if (teacherId != null) wrapper.eq(ScheduleEntity::getTeacherId, teacherId);
            if (classroomId != null) wrapper.eq(ScheduleEntity::getClassroomId, classroomId);
            if (classId != null) wrapper.eq(ScheduleEntity::getClassId, classId);
            wrapper.orderByAsc(ScheduleEntity::getDate, ScheduleEntity::getStartTime);
            list = scheduleMapper.selectList(wrapper);
        }

        List<ScheduleDTO> dtos = list.stream().map(this::toDTO).toList();
        fillNames(dtos);
        return dtos;
    }

    /**
     * 新增单条排课 — 带分布式锁
     */
    @Transactional
    public ScheduleDTO create(CreateScheduleRequest request) {
        // 校验班级存在并获取courseId
        ClassEntity classEntity = classMapper.selectById(request.getClassId());
        if (classEntity == null) throw new BusinessException(ErrorCode.CLASS_NOT_FOUND);

        // 分布式锁防并发冲突
        String lockKey = RedisDistributedLock.scheduleConflictLock(
                request.getTeacherId(), request.getDate().toString());

        return distributedLock.executeWithLock(lockKey, () -> {
            // 冲突检测
            List<Map<String, Object>> conflicts = detectConflicts(
                    request.getTeacherId(), request.getClassroomId(),
                    request.getDate(), request.getStartTime(), request.getEndTime(), null);
            if (!conflicts.isEmpty()) {
                throw new BusinessException(ErrorCode.SCHEDULE_CONFLICT_TEACHER,
                        conflicts.get(0).get("message").toString());
            }

            ScheduleEntity entity = new ScheduleEntity();
            entity.setClassId(request.getClassId());
            entity.setCourseId(classEntity.getCourseId());
            entity.setTeacherId(request.getTeacherId());
            entity.setClassroomId(request.getClassroomId());
            entity.setDate(request.getDate());
            entity.setStartTime(request.getStartTime());
            entity.setEndTime(request.getEndTime());
            entity.setLessonNo(request.getLessonNo());
            entity.setRemark(request.getRemark());
            entity.setStatus(1);

            try {
                scheduleMapper.insert(entity);
            } catch (DuplicateKeyException e) {
                throw new BusinessException(ErrorCode.SCHEDULE_CONFLICT_TEACHER,
                        "该时段教师或教室已被占用");
            }

            log.info("创建排课成功: id={}, date={}, time={}-{}",
                    entity.getId(), request.getDate(), request.getStartTime(), request.getEndTime());
            return toDTO(entity);
        });
    }

    /**
     * 批量排课 — 完整实现
     *
     * 流程:
     * 1. 校验班级/教师/教室有效性
     * 2. 预检所有日期的冲突 (先不改库)
     * 3. 逐日创建排课 (带分布式锁+唯一约束兜底)
     * 4. 返回成功数/跳过数/冲突详情
     */
    @Transactional
    public Map<String, Object> batchCreate(BatchScheduleRequest request) {
        // 1. 校验
        ClassEntity classEntity = classMapper.selectById(request.getClassId());
        if (classEntity == null) throw new BusinessException(ErrorCode.CLASS_NOT_FOUND);

        if (request.getWeekdays() == null || request.getWeekdays().isEmpty()) {
            throw new BusinessException("请选择至少一个上课日");
        }
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new BusinessException("开始日期不能晚于结束日期");
        }
        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new BusinessException("开始时间不能晚于结束时间");
        }

        Long courseId = classEntity.getCourseId();

        // 2. 预检冲突 — 遍历所有日期，收集冲突
        List<Map<String, Object>> conflictList = new ArrayList<>();
        Set<String> skipDates = new HashSet<>();
        LocalDate current = request.getStartDate();

        while (!current.isAfter(request.getEndDate())) {
            int weekday = current.getDayOfWeek().getValue();
            if (request.getWeekdays().contains(weekday)) {
                List<Map<String, Object>> dayConflicts = detectConflicts(
                        request.getTeacherId(), request.getClassroomId(),
                        current, request.getStartTime(), request.getEndTime(), null);
                if (!dayConflicts.isEmpty()) {
                    Map<String, Object> info = new LinkedHashMap<>();
                    info.put("date", current.toString());
                    info.put("weekday", weekday);
                    info.put("conflicts", dayConflicts);
                    conflictList.add(info);
                    skipDates.add(current.toString());
                }
            }
            current = current.plusDays(1);
        }

        // 3. 逐日创建排课 (跳过冲突的日期)
        AtomicInteger createdCount = new AtomicInteger(0);
        AtomicInteger skippedCount = new AtomicInteger(conflictList.size());
        current = request.getStartDate();

        while (!current.isAfter(request.getEndDate())) {
            int weekday = current.getDayOfWeek().getValue();
            final LocalDate processDate = current;
            if (request.getWeekdays().contains(weekday) && !skipDates.contains(processDate.toString())) {
                // 分布式锁
                String lockKey = RedisDistributedLock.scheduleConflictLock(
                        request.getTeacherId(), processDate.toString());

                try {
                    distributedLock.executeWithLock(lockKey, () -> {
                        // 二次检测 (锁内)
                        List<Map<String, Object>> recheck = detectConflicts(
                                request.getTeacherId(), request.getClassroomId(),
                                processDate, request.getStartTime(), request.getEndTime(), null);
                        if (!recheck.isEmpty()) {
                            Map<String, Object> info = new LinkedHashMap<>();
                            info.put("date", processDate.toString());
                            info.put("weekday", weekday);
                            info.put("conflicts", recheck);
                            conflictList.add(info);
                            skippedCount.incrementAndGet();
                            return null;
                        }

                        ScheduleEntity entity = new ScheduleEntity();
                        entity.setClassId(request.getClassId());
                        entity.setCourseId(courseId);
                        entity.setTeacherId(request.getTeacherId());
                        entity.setClassroomId(request.getClassroomId());
                        entity.setDate(processDate);
                        entity.setStartTime(request.getStartTime());
                        entity.setEndTime(request.getEndTime());
                        entity.setStatus(1);

                        try {
                            scheduleMapper.insert(entity);
                        } catch (DuplicateKeyException e) {
                            Map<String, Object> info = new LinkedHashMap<>();
                            info.put("date", processDate.toString());
                            info.put("reason", "数据库唯一约束冲突");
                            conflictList.add(info);
                            skippedCount.incrementAndGet();
                            return null;
                        }
                        return null;
                    });
                    createdCount.incrementAndGet();
                } catch (Exception e) {
                    log.warn("批量排课锁获取失败: date={}, msg={}", processDate, e.getMessage());
                    skippedCount.incrementAndGet();
                }
            }
            current = current.plusDays(1);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalDays", countWeekdays(request.getStartDate(), request.getEndDate(), request.getWeekdays()));
        result.put("createdCount", createdCount);
        result.put("skippedCount", skippedCount);
        result.put("conflictCount", conflictList.size());
        result.put("conflicts", conflictList);
        result.put("classId", request.getClassId());
        result.put("className", classEntity.getName());
        result.put("startDate", request.getStartDate().toString());
        result.put("endDate", request.getEndDate().toString());

        log.info("批量排课完成: total={}, created={}, skipped={}",
                result.get("totalDays"), createdCount, skippedCount);
        return result;
    }

    private int countWeekdays(LocalDate start, LocalDate end, List<Integer> weekdays) {
        int count = 0;
        LocalDate d = start;
        while (!d.isAfter(end)) {
            if (weekdays.contains(d.getDayOfWeek().getValue())) count++;
            d = d.plusDays(1);
        }
        return count;
    }

    /**
     * 修改排课
     */
    @Transactional
    public ScheduleDTO update(Long id, CreateScheduleRequest request) {
        ScheduleEntity entity = scheduleMapper.selectById(id);
        if (entity == null) throw new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND);

        if (request.getTeacherId() != null) entity.setTeacherId(request.getTeacherId());
        if (request.getClassroomId() != null) entity.setClassroomId(request.getClassroomId());
        if (request.getDate() != null) entity.setDate(request.getDate());
        if (request.getStartTime() != null) entity.setStartTime(request.getStartTime());
        if (request.getEndTime() != null) entity.setEndTime(request.getEndTime());
        if (request.getRemark() != null) entity.setRemark(request.getRemark());
        try {
            scheduleMapper.updateById(entity);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(ErrorCode.SCHEDULE_CONFLICT_TEACHER, "修改后排课时段冲突");
        }

        return toDTO(entity);
    }

    /**
     * 删除排课
     */
    public void delete(Long id) {
        if (scheduleMapper.selectById(id) == null) throw new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND);
        scheduleMapper.deleteById(id);
    }

    /**
     * 冲突检测
     */
    public Map<String, Object> checkConflicts(ConflictCheckRequest request) {
        List<Map<String, Object>> conflicts = detectConflicts(
                request.getTeacherId(), request.getClassroomId(),
                request.getDate(), request.getStartTime(), request.getEndTime(),
                request.getExcludeScheduleId());
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("hasConflict", !conflicts.isEmpty());
        result.put("conflicts", conflicts);
        return result;
    }

    /**
     * 冲突检测核心逻辑
     * 检测: 教师时间冲突 + 教室时间冲突
     */
    private List<Map<String, Object>> detectConflicts(Long teacherId, Long classroomId,
                                                       LocalDate date, java.time.LocalTime startTime,
                                                       java.time.LocalTime endTime,
                                                       Long excludeScheduleId) {
        List<Map<String, Object>> conflicts = new ArrayList<>();

        // 教师时间冲突
        if (teacherId != null) {
            LambdaQueryWrapper<ScheduleEntity> w = new LambdaQueryWrapper<>();
            w.eq(ScheduleEntity::getTeacherId, teacherId)
             .eq(ScheduleEntity::getDate, date)
             .lt(ScheduleEntity::getStartTime, endTime)
             .gt(ScheduleEntity::getEndTime, startTime)
             .ne(ScheduleEntity::getStatus, 0);
            if (excludeScheduleId != null) w.ne(ScheduleEntity::getId, excludeScheduleId);

            List<ScheduleEntity> list = scheduleMapper.selectList(w);
            for (ScheduleEntity s : list) {
                conflicts.add(Map.of(
                    "type", "teacher",
                    "message", String.format("教师在该时段已有排课(id=%d, %s-%s)", s.getId(), s.getStartTime(), s.getEndTime()),
                    "relatedScheduleId", s.getId().toString(),
                    "relatedDate", s.getDate().toString()
                ));
            }
        }

        // 教室时间冲突
        if (classroomId != null) {
            LambdaQueryWrapper<ScheduleEntity> w = new LambdaQueryWrapper<>();
            w.eq(ScheduleEntity::getClassroomId, classroomId)
             .eq(ScheduleEntity::getDate, date)
             .lt(ScheduleEntity::getStartTime, endTime)
             .gt(ScheduleEntity::getEndTime, startTime)
             .ne(ScheduleEntity::getStatus, 0);
            if (excludeScheduleId != null) w.ne(ScheduleEntity::getId, excludeScheduleId);

            List<ScheduleEntity> list = scheduleMapper.selectList(w);
            for (ScheduleEntity s : list) {
                conflicts.add(Map.of(
                    "type", "classroom",
                    "message", String.format("教室在该时段已被占用(id=%d, %s-%s)", s.getId(), s.getStartTime(), s.getEndTime()),
                    "relatedScheduleId", s.getId().toString(),
                    "relatedDate", s.getDate().toString()
                ));
            }
        }

        return conflicts;
    }

    /**
     * 批量填充課程/班級/教師/教室名稱
     */
    private void fillNames(List<ScheduleDTO> dtos) {
        if (dtos.isEmpty()) return;

        // 收集所有ID
        Set<Long> classIds = new HashSet<>();
        Set<Long> courseIds = new HashSet<>();
        Set<Long> teacherIds = new HashSet<>();
        Set<Long> classroomIds = new HashSet<>();
        for (ScheduleDTO d : dtos) {
            if (d.getClassId() != null) classIds.add(d.getClassId());
            if (d.getCourseId() != null) courseIds.add(d.getCourseId());
            if (d.getTeacherId() != null) teacherIds.add(d.getTeacherId());
            if (d.getClassroomId() != null) classroomIds.add(d.getClassroomId());
        }

        // 批量查询
        Map<Long, String> classNameMap = new HashMap<>();
        Map<Long, String> courseNameMap = new HashMap<>();
        Map<Long, String> teacherNameMap = new HashMap<>();
        Map<Long, String> classroomNameMap = new HashMap<>();

        if (!classIds.isEmpty()) {
            classMapper.selectBatchIds(classIds).forEach(c -> {
                classNameMap.put(c.getId(), c.getName());
                if (c.getCourseId() != null) courseIds.add(c.getCourseId());
            });
        }
        if (!courseIds.isEmpty()) {
            courseMapper.selectBatchIds(courseIds).forEach(c -> courseNameMap.put(c.getId(), c.getName()));
        }
        if (!teacherIds.isEmpty()) {
            teacherMapper.selectBatchIds(teacherIds).forEach(t -> teacherNameMap.put(t.getId(), t.getName()));
        }
        if (!classroomIds.isEmpty()) {
            classroomMapper.selectBatchIds(classroomIds).forEach(r -> classroomNameMap.put(r.getId(), r.getName()));
        }

        // 填充名稱
        for (ScheduleDTO d : dtos) {
            d.setClassName(classNameMap.get(d.getClassId()));
            d.setCourseName(courseNameMap.get(d.getCourseId()));
            d.setTeacherName(teacherNameMap.get(d.getTeacherId()));
            d.setClassroomName(classroomNameMap.get(d.getClassroomId()));
        }
    }

    private ScheduleDTO toDTO(ScheduleEntity e) {
        ScheduleDTO dto = new ScheduleDTO();
        dto.setId(e.getId());
        dto.setClassId(e.getClassId());
        dto.setCourseId(e.getCourseId());
        dto.setTeacherId(e.getTeacherId());
        dto.setClassroomId(e.getClassroomId());
        dto.setDate(e.getDate());
        dto.setStartTime(e.getStartTime());
        dto.setEndTime(e.getEndTime());
        dto.setLessonNo(e.getLessonNo());
        dto.setStatus(e.getStatus());
        dto.setRemark(e.getRemark());
        return dto;
    }
}
