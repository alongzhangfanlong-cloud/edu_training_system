package com.education.store.module.schedule.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.exception.BusinessException;
import com.education.store.module.classinfo.entity.EduClass;
import com.education.store.module.classinfo.mapper.EduClassMapper;
import com.education.store.module.course.entity.EduClassroom;
import com.education.store.module.course.entity.EduCourse;
import com.education.store.module.course.mapper.EduClassroomMapper;
import com.education.store.module.course.mapper.EduCourseMapper;
import com.education.store.module.schedule.entity.EduSchedule;
import com.education.store.module.schedule.mapper.EduScheduleMapper;
import com.education.store.module.schedule.service.IEduScheduleService;
import com.education.store.module.system.entity.system.SysUser;
import com.education.store.module.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EduScheduleServiceImpl extends ServiceImpl<EduScheduleMapper, EduSchedule>
        implements IEduScheduleService {

    private final EduScheduleMapper scheduleMapper;
    private final EduClassMapper classMapper;
    private final EduCourseMapper courseMapper;
    private final EduClassroomMapper classroomMapper;
    private final SysUserMapper userMapper;

    @Override
    public IPage<EduSchedule> selectScheduleList(IPage<EduSchedule> page, EduSchedule schedule,
                                                   String startDate, String endDate) {
        LambdaQueryWrapper<EduSchedule> wrapper = new LambdaQueryWrapper<>();
        if (schedule.getClassId() != null) wrapper.eq(EduSchedule::getClassId, schedule.getClassId());
        if (schedule.getTeacherId() != null) wrapper.eq(EduSchedule::getTeacherId, schedule.getTeacherId());
        if (schedule.getCourseId() != null) wrapper.eq(EduSchedule::getCourseId, schedule.getCourseId());
        if (schedule.getRoomId() != null) wrapper.eq(EduSchedule::getRoomId, schedule.getRoomId());
        if (StrUtil.isNotBlank(schedule.getStatus())) wrapper.eq(EduSchedule::getStatus, schedule.getStatus());
        if (StrUtil.isNotBlank(startDate)) wrapper.ge(EduSchedule::getScheduleDate, startDate);
        if (StrUtil.isNotBlank(endDate)) wrapper.le(EduSchedule::getScheduleDate, endDate);
        wrapper.orderByDesc(EduSchedule::getScheduleDate);
        IPage<EduSchedule> result = scheduleMapper.selectPage(page, wrapper);
        fillScheduleNames(result.getRecords());
        return result;
    }

    @Override public EduSchedule selectScheduleById(Long id) {
        EduSchedule schedule = scheduleMapper.selectById(id);
        if (schedule != null) {
            fillScheduleNames(Collections.singletonList(schedule));
        }
        return schedule;
    }

    private void fillScheduleNames(List<EduSchedule> schedules) {
        if (CollUtil.isEmpty(schedules)) {
            return;
        }

        Set<Long> classIds = schedules.stream()
                .map(EduSchedule::getClassId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Set<Long> courseIds = schedules.stream()
                .map(EduSchedule::getCourseId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Set<Long> teacherIds = schedules.stream()
                .map(EduSchedule::getTeacherId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Set<Long> roomIds = schedules.stream()
                .map(EduSchedule::getRoomId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, String> classNameMap = new HashMap<>();
        if (CollUtil.isNotEmpty(classIds)) {
            List<EduClass> classes = classMapper.selectBatchIds(classIds);
            classNameMap = classes.stream()
                    .collect(Collectors.toMap(EduClass::getId, EduClass::getName, (v1, v2) -> v1));
        }

        Map<Long, String> courseNameMap = new HashMap<>();
        if (CollUtil.isNotEmpty(courseIds)) {
            List<EduCourse> courses = courseMapper.selectBatchIds(courseIds);
            courseNameMap = courses.stream()
                    .collect(Collectors.toMap(EduCourse::getId, EduCourse::getName, (v1, v2) -> v1));
        }

        Map<Long, String> teacherNameMap = new HashMap<>();
        if (CollUtil.isNotEmpty(teacherIds)) {
            List<SysUser> users = userMapper.selectBatchIds(teacherIds);
            teacherNameMap = users.stream()
                    .collect(Collectors.toMap(SysUser::getId,
                            u -> StrUtil.isNotBlank(u.getNickName()) ? u.getNickName() :
                                    (StrUtil.isNotBlank(u.getRealName()) ? u.getRealName() : u.getUsername()),
                            (v1, v2) -> v1));
        }

        Map<Long, String> roomNameMap = new HashMap<>();
        if (CollUtil.isNotEmpty(roomIds)) {
            List<EduClassroom> classrooms = classroomMapper.selectBatchIds(roomIds);
            roomNameMap = classrooms.stream()
                    .collect(Collectors.toMap(EduClassroom::getId, EduClassroom::getName, (v1, v2) -> v1));
        }

        for (EduSchedule schedule : schedules) {
            if (schedule.getClassId() != null) {
                schedule.setClassName(classNameMap.get(schedule.getClassId()));
            }
            if (schedule.getCourseId() != null) {
                schedule.setCourseName(courseNameMap.get(schedule.getCourseId()));
            }
            if (schedule.getTeacherId() != null) {
                schedule.setTeacherName(teacherNameMap.get(schedule.getTeacherId()));
            }
            if (schedule.getRoomId() != null) {
                schedule.setRoomName(roomNameMap.get(schedule.getRoomId()));
            }
        }
    }

    @Override @Transactional(rollbackFor = Exception.class)
    public int insertSchedule(EduSchedule schedule) {
        if (!checkConflict(schedule)) throw new BusinessException("排课冲突，请检查教师/教室/班级时间安排");
        schedule.setStatus("NORMAL");
        if (StrUtil.isBlank(schedule.getScheduleType())) schedule.setScheduleType("NORMAL");
        return scheduleMapper.insert(schedule);
    }

    @Override @Transactional(rollbackFor = Exception.class)
    public int updateSchedule(EduSchedule schedule) {
        EduSchedule exist = scheduleMapper.selectById(schedule.getId());
        if (exist == null) throw new BusinessException("排课记录不存在");
        if (!checkConflict(schedule, schedule.getId())) throw new BusinessException("排课冲突，请检查教师/教室/班级时间安排");
        return scheduleMapper.updateById(schedule);
    }

    @Override @Transactional(rollbackFor = Exception.class)
    public int deleteScheduleByIds(Long[] ids) {
        return scheduleMapper.deleteBatchIds(Arrays.asList(ids));
    }

    @Override @Transactional(rollbackFor = Exception.class)
    public int cancelSchedule(Long id, String reason) {
        EduSchedule schedule = scheduleMapper.selectById(id);
        if (schedule == null) throw new BusinessException("排课记录不存在");
        if (!"NORMAL".equals(schedule.getStatus()) && !"MAKEUP".equals(schedule.getStatus()))
            throw new BusinessException("仅正常/补课状态的排课可停课");
        // 停课状态为 SUSPENDED，CANCELLED 用于取消操作（见 PRD §9.8）
        schedule.setStatus("SUSPENDED");
        schedule.setCancelReason(reason);
        schedule.setCancelTime(LocalDateTime.now());
        return scheduleMapper.updateById(schedule);
    }

    @Override @Transactional(rollbackFor = Exception.class)
    public int reschedule(EduSchedule updated) {
        EduSchedule exist = scheduleMapper.selectById(updated.getId());
        if (exist == null) throw new BusinessException("排课记录不存在");
        exist.setStatus("RESCHEDULED");
        scheduleMapper.updateById(exist);

        EduSchedule newSchedule = new EduSchedule();
        newSchedule.setPlanId(exist.getPlanId());
        newSchedule.setClassId(updated.getClassId() != null ? updated.getClassId() : exist.getClassId());
        newSchedule.setCourseId(exist.getCourseId());
        newSchedule.setTeacherId(updated.getTeacherId() != null ? updated.getTeacherId() : exist.getTeacherId());
        newSchedule.setRoomId(updated.getRoomId() != null ? updated.getRoomId() : exist.getRoomId());
        newSchedule.setScheduleDate(updated.getScheduleDate());
        newSchedule.setStartTime(updated.getStartTime());
        newSchedule.setEndTime(updated.getEndTime());
        newSchedule.setStatus("NORMAL");
        newSchedule.setScheduleType(exist.getScheduleType());
        newSchedule.setRescheduledFromId(exist.getId());
        if (!checkConflict(newSchedule)) throw new BusinessException("排课冲突，请检查教师/教室/班级时间安排");
        int rows = scheduleMapper.insert(newSchedule);

        // 回写原记录的 rescheduledToId，修复双向关联断裂（P2 修复）
        exist.setRescheduledToId(newSchedule.getId());
        scheduleMapper.updateById(exist);

        return rows;
    }

    @Override @Transactional(rollbackFor = Exception.class)
    public int consumeSchedule(Long id) {
        EduSchedule schedule = scheduleMapper.selectById(id);
        if (schedule == null) throw new BusinessException("排课记录不存在");
        // 仅允许消课已过日期的排课（PRD §3.1.5）
        if (schedule.getScheduleDate() != null
                && schedule.getScheduleDate().isAfter(java.time.LocalDate.now())) {
            throw new BusinessException("仅可对已过上课日期的排课执行消课操作");
        }
        schedule.setConsumed(1);
        schedule.setConsumeTime(LocalDateTime.now());
        return scheduleMapper.updateById(schedule);
    }

    @Override
    public boolean checkConflict(EduSchedule schedule) {
        return checkConflict(schedule, null);
    }

    /** 冲突检测（排除指定排课ID，用于更新场景） */
    private boolean checkConflict(EduSchedule schedule, Long excludeId) {
        if (schedule.getTeacherId() != null) {
            int c;
            if (excludeId != null) {
                c = scheduleMapper.countTeacherConflictExclude(schedule.getTeacherId(), schedule.getScheduleDate(), schedule.getStartTime(), schedule.getEndTime(), excludeId);
            } else {
                c = scheduleMapper.countTeacherConflict(schedule.getTeacherId(), schedule.getScheduleDate(), schedule.getStartTime(), schedule.getEndTime());
            }
            if (c > 0) return false;
        }
        if (schedule.getRoomId() != null) {
            int c;
            if (excludeId != null) {
                c = scheduleMapper.countRoomConflictExclude(schedule.getRoomId(), schedule.getScheduleDate(), schedule.getStartTime(), schedule.getEndTime(), excludeId);
            } else {
                c = scheduleMapper.countRoomConflict(schedule.getRoomId(), schedule.getScheduleDate(), schedule.getStartTime(), schedule.getEndTime());
            }
            if (c > 0) return false;
        }
        if (schedule.getClassId() != null) {
            int c;
            if (excludeId != null) {
                c = scheduleMapper.countClassConflictExclude(schedule.getClassId(), schedule.getScheduleDate(), schedule.getStartTime(), schedule.getEndTime(), excludeId);
            } else {
                c = scheduleMapper.countClassConflict(schedule.getClassId(), schedule.getScheduleDate(), schedule.getStartTime(), schedule.getEndTime());
            }
            if (c > 0) return false;
        }
        return true;
    }
}
