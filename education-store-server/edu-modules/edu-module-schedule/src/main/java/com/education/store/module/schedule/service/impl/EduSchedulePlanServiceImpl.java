package com.education.store.module.schedule.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.exception.BusinessException;
import com.education.store.module.schedule.dto.SchedulePlanCreateDTO;
import com.education.store.module.schedule.dto.SchedulePreviewVO;
import com.education.store.module.schedule.entity.EduSchedule;
import com.education.store.module.schedule.entity.EduSchedulePlan;
import com.education.store.module.schedule.mapper.EduScheduleMapper;
import com.education.store.module.schedule.mapper.EduSchedulePlanMapper;
import com.education.store.module.schedule.service.IEduSchedulePlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
@RequiredArgsConstructor
public class EduSchedulePlanServiceImpl extends ServiceImpl<EduSchedulePlanMapper, EduSchedulePlan>
        implements IEduSchedulePlanService {

    private final EduSchedulePlanMapper planMapper;
    private final EduScheduleMapper scheduleMapper;

    @Override
    public IPage<EduSchedulePlan> selectPlanList(IPage<EduSchedulePlan> page, EduSchedulePlan plan, Long classId) {
        LambdaQueryWrapper<EduSchedulePlan> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(plan.getStatus())) {
            wrapper.eq(EduSchedulePlan::getStatus, plan.getStatus());
        }
        if (classId != null) {
            wrapper.like(EduSchedulePlan::getClassIds, String.valueOf(classId));
        }
        wrapper.orderByDesc(EduSchedulePlan::getCreateTime);
        return planMapper.selectPage(page, wrapper);
    }

    @Override
    public EduSchedulePlan selectPlanById(Long id) {
        return planMapper.selectById(id);
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
        List<SchedulePreviewVO.ScheduleSlot> slots = buildSlots(dto);
        // 三维冲突检测
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
        EduSchedulePlan plan = planMapper.selectById(planId);
        if (plan == null) throw new BusinessException("排课计划不存在");
        if ("GENERATED".equals(plan.getStatus())) throw new BusinessException("排课计划已生成，不可重复生成");

        List<SchedulePreviewVO.ScheduleSlot> slots = buildSlots(dto);
        int count = 0;
        for (SchedulePreviewVO.ScheduleSlot slot : slots) {
            if (slot.isConflict()) {
                throw new BusinessException("存在冲突槽位：" + slot.getConflictDetail()
                        + "（班级#" + slot.getClassId() + "，日期 " + slot.getScheduleDate() + "）");
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
        // 更新计划状态
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

        // 删除旧的排课记录
        LambdaQueryWrapper<EduSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduSchedule::getPlanId, planId);
        scheduleMapper.delete(wrapper);

        // 解析原计划参数并重新生成
        SchedulePlanCreateDTO dto = rebuildDTO(plan);
        return generateSchedules(planId, dto);
    }

    // ==================== 私有辅助方法 ====================

    /** 根据 DTO 构建排课槽位列表 */
    private List<SchedulePreviewVO.ScheduleSlot> buildSlots(SchedulePlanCreateDTO dto) {
        List<SchedulePreviewVO.ScheduleSlot> slots = new ArrayList<>();
        List<Integer> weekDays = dto.getWeekDays();
        if (weekDays == null || weekDays.isEmpty()) {
            weekDays = Collections.singletonList(1); // 默认周一
        }

        for (Long classId : dto.getClassIds()) {
            // 生成日期范围内所有匹配星期几的日期
            List<LocalDate> dates = generateDates(dto.getStartDate(), dto.getEndDate(), weekDays);
            for (LocalDate date : dates) {
                slots.add(SchedulePreviewVO.ScheduleSlot.builder()
                        .classId(classId)
                        .className("班级#" + classId)
                        .teacherId(dto.getTeacherId())
                        .teacherName(dto.getTeacherId() != null ? "教师#" + dto.getTeacherId() : null)
                        .roomId(dto.getRoomId())
                        .roomName(dto.getRoomId() != null ? "教室#" + dto.getRoomId() : null)
                        .scheduleDate(date)
                        .startTime(dto.getStartTime())
                        .endTime(dto.getEndTime())
                        .conflict(false)
                        .conflictDetail(null)
                        .build());
            }
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
        // 解析 classIds JSON 数组
        String classIdsStr = plan.getClassIds();
        List<Long> classIds = new ArrayList<>();
        if (StrUtil.isNotBlank(classIdsStr)) {
            String trimmed = classIdsStr.replace("[", "").replace("]", "").replace("\"", "");
            for (String s : trimmed.split(",")) {
                try { classIds.add(Long.parseLong(s.trim())); } catch (NumberFormatException ignored) {}
            }
        }
        dto.setClassIds(classIds);
        dto.setStartDate(plan.getStartDate());
        dto.setEndDate(plan.getEndDate());
        return dto;
    }
}
