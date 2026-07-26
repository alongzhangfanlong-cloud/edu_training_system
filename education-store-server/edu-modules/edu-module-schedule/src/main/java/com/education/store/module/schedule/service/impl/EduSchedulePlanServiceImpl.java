package com.education.store.module.schedule.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.exception.BusinessException;
import com.education.store.module.classinfo.entity.EduClass;
import com.education.store.module.classinfo.entity.EduClassScheduleRule;
import com.education.store.module.classinfo.mapper.EduClassMapper;
import com.education.store.module.classinfo.mapper.EduClassScheduleRuleMapper;
import com.education.store.module.course.entity.EduClassroom;
import com.education.store.module.course.entity.EduCourse;
import com.education.store.module.course.mapper.EduClassroomMapper;
import com.education.store.module.course.mapper.EduCourseMapper;
import com.education.store.module.schedule.dto.SchedulePlanCreateDTO;
import com.education.store.module.schedule.dto.SchedulePreviewVO;
import com.education.store.module.schedule.entity.EduSchedule;
import com.education.store.module.schedule.entity.EduSchedulePlan;
import com.education.store.module.schedule.mapper.EduScheduleMapper;
import com.education.store.module.schedule.mapper.EduSchedulePlanMapper;
import com.education.store.module.schedule.service.IEduSchedulePlanService;
import com.education.store.module.system.entity.system.SysUser;
import com.education.store.module.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EduSchedulePlanServiceImpl extends ServiceImpl<EduSchedulePlanMapper, EduSchedulePlan>
        implements IEduSchedulePlanService {

    private final EduSchedulePlanMapper planMapper;
    private final EduScheduleMapper scheduleMapper;
    private final EduClassMapper classMapper;
    private final EduCourseMapper courseMapper;
    private final SysUserMapper sysUserMapper;
    private final EduClassroomMapper classroomMapper;
    private final EduClassScheduleRuleMapper scheduleRuleMapper;

    @Override
    public IPage<EduSchedulePlan> selectPlanList(IPage<EduSchedulePlan> page, EduSchedulePlan plan, Long classId) {
        LambdaQueryWrapper<EduSchedulePlan> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(plan.getStatus())) {
            wrapper.eq(EduSchedulePlan::getStatus, plan.getStatus());
        }
        if (classId != null) {
            wrapper.eq(EduSchedulePlan::getClassId, classId);
        }
        wrapper.orderByDesc(EduSchedulePlan::getCreateTime);
        IPage<EduSchedulePlan> result = planMapper.selectPage(page, wrapper);
        fillNamesForList(result.getRecords());
        return result;
    }

    @Override
    public EduSchedulePlan selectPlanById(Long id) {
        EduSchedulePlan plan = planMapper.selectById(id);
        if (plan != null) {
            fillNames(plan);
            parsePlanConfig(plan);
        }
        return plan;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPlan(EduSchedulePlan plan) {
        plan.setStatus("UNGENERATED");
        return planMapper.insert(plan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePlan(EduSchedulePlan plan) {
        EduSchedulePlan exist = planMapper.selectById(plan.getId());
        if (exist == null) throw new BusinessException("排课计划不存在");
        if ("GENERATED".equals(exist.getStatus())) throw new BusinessException("已生成的排课计划不可修改");
        return planMapper.updateById(plan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePlanByIds(Long[] ids) {
        for (Long id : ids) {
            EduSchedulePlan plan = planMapper.selectById(id);
            if (plan != null && "GENERATED".equals(plan.getStatus())) {
                throw new BusinessException("已生成的排课计划不可删除");
            }
        }
        return planMapper.deleteBatchIds(Arrays.asList(ids));
    }

    // ==================== 预览 & 生成 & 重新生成 ====================

    @Override
    public SchedulePreviewVO previewPlan(SchedulePlanCreateDTO dto) {
        fillDefaults(dto);
        List<SchedulePreviewVO.ScheduleSlot> slots = buildSlots(dto);
        int conflictCount = 0;
        for (SchedulePreviewVO.ScheduleSlot slot : slots) {
            boolean hasConflict = checkSlotConflict(slot);
            slot.setConflict(hasConflict);
            if (hasConflict) conflictCount++;
        }
        return SchedulePreviewVO.builder()
                .slots(slots)
                .conflictCount(conflictCount)
                .totalCount(slots.size())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int generateSchedules(Long planId, SchedulePlanCreateDTO dto) {
        return generateWithOptions(planId, dto, false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int generateWithOptions(Long planId, SchedulePlanCreateDTO dto, boolean skipConflict) {
        EduSchedulePlan plan = planMapper.selectById(planId);
        if (plan == null) throw new BusinessException("排课计划不存在");
        if ("GENERATED".equals(plan.getStatus())) throw new BusinessException("排课计划已生成，不可重复生成");

        fillDefaults(dto);
        // 保存排课配置到planConfig
        Map<String, Object> config = new HashMap<>();
        config.put("weekDays", dto.getWeekDays());
        config.put("startTime", dto.getStartTime().toString());
        config.put("endTime", dto.getEndTime().toString());
        config.put("teacherId", dto.getTeacherId());
        plan.setPlanConfig(JSONUtil.toJsonStr(config));
        plan.setRoomId(dto.getRoomId());
        planMapper.updateById(plan);

        List<SchedulePreviewVO.ScheduleSlot> slots = buildSlots(dto);
        int count = 0;
        for (SchedulePreviewVO.ScheduleSlot slot : slots) {
            boolean hasConflict = checkSlotConflict(slot);
            if (hasConflict && !skipConflict) {
                throw new BusinessException("存在冲突槽位：" + slot.getConflictDetail()
                        + "（班级#" + slot.getClassId() + "，日期 " + slot.getScheduleDate() + "）");
            }
            if (hasConflict) {
                continue; // 跳过冲突
            }
            EduSchedule schedule = new EduSchedule();
            schedule.setPlanId(planId);
            schedule.setClassId(slot.getClassId());
            schedule.setCourseId(slot.getCourseId());
            schedule.setTeacherId(slot.getTeacherId());
            schedule.setRoomId(slot.getRoomId());
            schedule.setScheduleDate(slot.getScheduleDate());
            schedule.setStartTime(slot.getStartTime());
            schedule.setEndTime(slot.getEndTime());
            schedule.setScheduleType(dto.getScheduleType() != null ? dto.getScheduleType() : "NORMAL");
            schedule.setStatus("NORMAL");
            scheduleMapper.insert(schedule);
            count++;
        }
        plan.setStatus("GENERATED");
        planMapper.updateById(plan);
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int regeneratePlan(Long planId) {
        EduSchedulePlan plan = planMapper.selectById(planId);
        if (plan == null) throw new BusinessException("排课计划不存在");
        if (!"GENERATED".equals(plan.getStatus())) throw new BusinessException("仅已生成的排课计划可重新生成");

        LambdaQueryWrapper<EduSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduSchedule::getPlanId, planId);
        scheduleMapper.delete(wrapper);

        SchedulePlanCreateDTO dto = rebuildDTO(plan);
        plan.setStatus("UNGENERATED");
        planMapper.updateById(plan);
        return generateWithOptions(planId, dto, false);
    }

    @Override
    public List<EduClassScheduleRule> getPlanRules(Long planId) {
        EduSchedulePlan plan = planMapper.selectById(planId);
        if (plan == null || plan.getClassId() == null) return Collections.emptyList();
        LambdaQueryWrapper<EduClassScheduleRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduClassScheduleRule::getClassId, plan.getClassId());
        wrapper.orderByAsc(EduClassScheduleRule::getWeekDay, EduClassScheduleRule::getStartTime);
        return scheduleRuleMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePlanRules(Long planId, List<EduClassScheduleRule> rules) {
        EduSchedulePlan plan = planMapper.selectById(planId);
        if (plan == null) throw new BusinessException("排课计划不存在");
        if (plan.getClassId() == null) throw new BusinessException("排课计划未关联班级");

        Long classId = plan.getClassId();
        LambdaQueryWrapper<EduClassScheduleRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduClassScheduleRule::getClassId, classId);
        scheduleRuleMapper.delete(wrapper);

        for (EduClassScheduleRule rule : rules) {
            rule.setId(null);
            rule.setClassId(classId);
            if (StrUtil.isBlank(rule.getRuleType())) {
                rule.setRuleType(rule.getWeekDay() != null ? "WEEKLY" : "SPECIFIC");
            }
            scheduleRuleMapper.insert(rule);
        }
    }

    // ==================== 私有辅助方法 ====================

    /** 填充默认参数 */
    private void fillDefaults(SchedulePlanCreateDTO dto) {
        if (dto.getWeekDays() == null || dto.getWeekDays().isEmpty()) {
            dto.setWeekDays(Collections.singletonList(1));
        }
        if (dto.getTeacherId() == null || dto.getRoomId() == null) {
            EduClass clazz = classMapper.selectById(dto.getClassId());
            if (clazz != null) {
                if (dto.getTeacherId() == null) dto.setTeacherId(clazz.getTeacherId());
                if (dto.getRoomId() == null) dto.setRoomId(clazz.getClassroomId());
            }
        }
    }

    /** 根据 DTO 构建排课槽位列表 */
    private List<SchedulePreviewVO.ScheduleSlot> buildSlots(SchedulePlanCreateDTO dto) {
        List<SchedulePreviewVO.ScheduleSlot> slots = new ArrayList<>();
        Long classId = dto.getClassId();
        Long courseId = null;
        EduClass clazz = classMapper.selectById(classId);
        if (clazz != null) {
            courseId = clazz.getCourseId();
        }

        List<LocalDate> dates = generateDates(dto.getStartDate(), dto.getEndDate(), dto.getWeekDays());
        for (LocalDate date : dates) {
            slots.add(SchedulePreviewVO.ScheduleSlot.builder()
                    .classId(classId)
                    .courseId(courseId)
                    .className(getClassName(classId))
                    .teacherId(dto.getTeacherId())
                    .teacherName(getTeacherName(dto.getTeacherId()))
                    .roomId(dto.getRoomId())
                    .roomName(getRoomName(dto.getRoomId()))
                    .scheduleDate(date)
                    .startTime(dto.getStartTime())
                    .endTime(dto.getEndTime())
                    .conflict(false)
                    .conflictDetail(null)
                    .build());
        }
        return slots;
    }

    /** 生成日期范围内所有匹配星期几的日期 */
    private List<LocalDate> generateDates(LocalDate start, LocalDate end, List<Integer> weekDays) {
        List<LocalDate> dates = new ArrayList<>();
        Set<DayOfWeek> targetDays = new HashSet<>();
        for (int wd : weekDays) {
            targetDays.add(DayOfWeek.of(wd));
        }
        LocalDate cursor = start;
        while (!cursor.isAfter(end)) {
            if (targetDays.contains(cursor.getDayOfWeek())) {
                dates.add(cursor);
            }
            cursor = cursor.plusDays(1);
        }
        return dates;
    }

    /** 三维冲突检测：教师/教室/班级同时段 */
    private boolean checkSlotConflict(SchedulePreviewVO.ScheduleSlot slot) {
        StringBuilder detail = new StringBuilder();
        boolean hasConflict = false;

        if (slot.getTeacherId() != null) {
            int c = scheduleMapper.countTeacherConflict(slot.getTeacherId(), slot.getScheduleDate(),
                    slot.getStartTime(), slot.getEndTime());
            if (c > 0) { hasConflict = true; detail.append("教师冲突; "); }
        }
        if (slot.getRoomId() != null) {
            int c = scheduleMapper.countRoomConflict(slot.getRoomId(), slot.getScheduleDate(),
                    slot.getStartTime(), slot.getEndTime());
            if (c > 0) { hasConflict = true; detail.append("教室冲突; "); }
        }
        if (slot.getClassId() != null) {
            int c = scheduleMapper.countClassConflict(slot.getClassId(), slot.getScheduleDate(),
                    slot.getStartTime(), slot.getEndTime());
            if (c > 0) { hasConflict = true; detail.append("班级冲突; "); }
        }
        if (hasConflict) {
            slot.setConflictDetail(detail.toString().trim());
        }
        return hasConflict;
    }

    /** 从已生成计划重建 DTO（用于重新生成） */
    private SchedulePlanCreateDTO rebuildDTO(EduSchedulePlan plan) {
        SchedulePlanCreateDTO dto = new SchedulePlanCreateDTO();
        dto.setClassId(plan.getClassId());
        dto.setRoomId(plan.getRoomId());
        dto.setStartDate(plan.getStartDate());
        dto.setEndDate(plan.getEndDate());

        if (StrUtil.isNotBlank(plan.getPlanConfig())) {
            try {
                Map<String, Object> config = JSONUtil.toBean(plan.getPlanConfig(), Map.class);
                Object wd = config.get("weekDays");
                if (wd != null) {
                    dto.setWeekDays(JSONUtil.toList(JSONUtil.parseArray(wd.toString()), Integer.class));
                }
                Object st = config.get("startTime");
                if (st != null) dto.setStartTime(LocalTime.parse(st.toString()));
                Object et = config.get("endTime");
                if (et != null) dto.setEndTime(LocalTime.parse(et.toString()));
                Object tid = config.get("teacherId");
                if (tid != null) dto.setTeacherId(Long.valueOf(tid.toString()));
            } catch (Exception ignore) {}
        }

        fillDefaults(dto);
        return dto;
    }

    /** 解析planConfig到实体字段 */
    private void parsePlanConfig(EduSchedulePlan plan) {
        if (StrUtil.isBlank(plan.getPlanConfig())) return;
        try {
            Map<String, Object> config = JSONUtil.toBean(plan.getPlanConfig(), Map.class);
            Object wd = config.get("weekDays");
            if (wd != null) {
                plan.setWeekDays(JSONUtil.toList(JSONUtil.parseArray(wd.toString()), Integer.class));
            }
            Object st = config.get("startTime");
            if (st != null) plan.setStartTime(LocalTime.parse(st.toString()));
            Object et = config.get("endTime");
            if (et != null) plan.setEndTime(LocalTime.parse(et.toString()));
            Object tid = config.get("teacherId");
            if (tid != null) plan.setTeacherId(Long.valueOf(tid.toString()));
        } catch (Exception ignore) {}
    }

    // ========== 名称填充 ==========

    private void fillNamesForList(List<EduSchedulePlan> list) {
        if (list == null || list.isEmpty()) return;
        Set<Long> allClassIds = new HashSet<>();
        for (EduSchedulePlan plan : list) {
            if (plan.getClassId() != null) allClassIds.add(plan.getClassId());
        }
        if (allClassIds.isEmpty()) return;

        Map<Long, EduClass> classMap = classMapper.selectBatchIds(allClassIds).stream()
                .collect(Collectors.toMap(EduClass::getId, c -> c));
        Set<Long> courseIds = new HashSet<>();
        Set<Long> teacherIds = new HashSet<>();
        for (EduClass clazz : classMap.values()) {
            if (clazz.getCourseId() != null) courseIds.add(clazz.getCourseId());
            if (clazz.getTeacherId() != null) teacherIds.add(clazz.getTeacherId());
        }
        Map<Long, EduCourse> courseMap = courseIds.isEmpty() ? Collections.emptyMap()
                : courseMapper.selectBatchIds(courseIds).stream().collect(Collectors.toMap(EduCourse::getId, c -> c));
        Map<Long, SysUser> userMap = teacherIds.isEmpty() ? Collections.emptyMap()
                : sysUserMapper.selectBatchIds(teacherIds).stream().collect(Collectors.toMap(SysUser::getId, u -> u));
        Set<Long> allRoomIds = new HashSet<>();
        for (EduSchedulePlan plan : list) {
            Long rid = plan.getRoomId();
            if (rid != null) allRoomIds.add(rid);
            else {
                EduClass clazz = classMap.get(plan.getClassId());
                if (clazz != null && clazz.getClassroomId() != null) allRoomIds.add(clazz.getClassroomId());
            }
        }
        Map<Long, EduClassroom> roomMap = allRoomIds.isEmpty() ? Collections.emptyMap()
                : classroomMapper.selectBatchIds(allRoomIds).stream().collect(Collectors.toMap(EduClassroom::getId, r -> r));
        for (EduSchedulePlan plan : list) {
            fillPlanNames(plan, classMap, courseMap, userMap, roomMap);
            parsePlanConfig(plan);
            // 计算计划课次
            if (plan.getWeekDays() != null && plan.getStartDate() != null && plan.getEndDate() != null) {
                int planned = 0;
                LocalDate cursor = plan.getStartDate();
                Set<DayOfWeek> targetDays = new HashSet<>();
                for (int wd : plan.getWeekDays()) targetDays.add(DayOfWeek.of(wd));
                while (!cursor.isAfter(plan.getEndDate())) {
                    if (targetDays.contains(cursor.getDayOfWeek())) planned++;
                    cursor = cursor.plusDays(1);
                }
                plan.setPlannedLessons(planned);
            }
            // 统计已排课次
            Long actualCount = scheduleMapper.selectCount(new LambdaQueryWrapper<EduSchedule>()
                    .eq(EduSchedule::getPlanId, plan.getId()));
            plan.setScheduledLessons(actualCount != null ? actualCount.intValue() : 0);
        }
    }

    private void fillNames(EduSchedulePlan plan) {
        Long classId = plan.getClassId();
        if (classId == null) return;
        EduClass clazz = classMapper.selectById(classId);
        if (clazz == null) return;
        plan.setClassName(clazz.getName());
        plan.setName(clazz.getName());
        if (clazz.getCourseId() != null) {
            EduCourse course = courseMapper.selectById(clazz.getCourseId());
            if (course != null) plan.setCourseName(course.getName());
        }
        if (clazz.getTeacherId() != null) {
            SysUser user = sysUserMapper.selectById(clazz.getTeacherId());
            if (user != null) plan.setTeacherName(user.getRealName());
        }
        Long roomId = plan.getRoomId() != null ? plan.getRoomId() : clazz.getClassroomId();
        if (roomId != null) {
            EduClassroom room = classroomMapper.selectById(roomId);
            if (room != null) plan.setRoomName(room.getName());
        }
    }

    private void fillPlanNames(EduSchedulePlan plan,
                                Map<Long, EduClass> classMap,
                                Map<Long, EduCourse> courseMap,
                                Map<Long, SysUser> userMap,
                                Map<Long, EduClassroom> roomMap) {
        Long classId = plan.getClassId();
        if (classId == null) return;
        EduClass clazz = classMap.get(classId);
        if (clazz != null) {
            plan.setClassName(clazz.getName());
            plan.setName(clazz.getName());
            if (clazz.getCourseId() != null) {
                EduCourse course = courseMap.get(clazz.getCourseId());
                if (course != null) plan.setCourseName(course.getName());
            }
            if (clazz.getTeacherId() != null) {
                SysUser user = userMap.get(clazz.getTeacherId());
                if (user != null) plan.setTeacherName(user.getRealName());
            }
            Long roomId = plan.getRoomId() != null ? plan.getRoomId() : clazz.getClassroomId();
            if (roomId != null) {
                EduClassroom room = roomMap.get(roomId);
                if (room != null) plan.setRoomName(room.getName());
            }
        }
    }

    private String getClassName(Long classId) {
        if (classId == null) return null;
        EduClass clazz = classMapper.selectById(classId);
        return clazz != null ? clazz.getName() : null;
    }

    private String getTeacherName(Long teacherId) {
        if (teacherId == null) return null;
        SysUser user = sysUserMapper.selectById(teacherId);
        return user != null ? user.getRealName() : null;
    }

    private String getRoomName(Long roomId) {
        if (roomId == null) return null;
        EduClassroom room = classroomMapper.selectById(roomId);
        return room != null ? room.getName() : null;
    }
}
