package com.education.store.module.attendance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.exception.BusinessException;
import com.education.store.module.attendance.entity.EduLeaveConflict;
import com.education.store.module.attendance.mapper.EduLeaveConflictMapper;
import com.education.store.module.attendance.service.IEduLeaveConflictService;
import com.education.store.module.schedule.entity.EduSchedule;
import com.education.store.module.schedule.mapper.EduScheduleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EduLeaveConflictServiceImpl extends ServiceImpl<EduLeaveConflictMapper, EduLeaveConflict>
        implements IEduLeaveConflictService {

    private final EduLeaveConflictMapper conflictMapper;
    private final EduScheduleMapper scheduleMapper;

    @Override
    public List<EduLeaveConflict> selectByLeaveId(Long leaveId) {
        LambdaQueryWrapper<EduLeaveConflict> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduLeaveConflict::getLeaveId, leaveId);
        return conflictMapper.selectList(wrapper);
    }

    @Override
    public List<Map<String, Object>> selectDetailsByLeaveId(Long leaveId) {
        return conflictMapper.selectDetailsByLeaveId(leaveId);
    }

    @Override @Transactional(rollbackFor = Exception.class)
    public int resolveConflict(Long id, EduLeaveConflict resolution) {
        EduLeaveConflict conflict = conflictMapper.selectById(id);
        if (conflict == null) throw new BusinessException("冲突记录不存在");

        // 1. 更新冲突记录
        conflict.setResolveType(resolution.getResolveType());
        conflict.setSubstituteTeacherId(resolution.getSubstituteTeacherId());
        conflict.setNewScheduleDate(resolution.getNewScheduleDate());
        conflict.setNewTimeSlot(resolution.getNewTimeSlot());
        conflict.setNewClassroomId(resolution.getNewClassroomId());
        conflict.setStatus(1);
        int rows = conflictMapper.updateById(conflict);

        // 2. 操作排课表
        EduSchedule schedule = scheduleMapper.selectById(conflict.getScheduleId());
        if (schedule == null) return rows;

        int type = resolution.getResolveType() != null ? resolution.getResolveType() : 0;
        switch (type) {
            case 0: // 代课：替换排课教师
                if (resolution.getSubstituteTeacherId() != null) {
                    schedule.setTeacherId(resolution.getSubstituteTeacherId());
                    schedule.setStatus("SUBSTITUTE");
                }
                break;
            case 1: // 调课：更新排课日期/时间/教室
                if (resolution.getNewScheduleDate() != null) {
                    schedule.setScheduleDate(resolution.getNewScheduleDate());
                }
                if (resolution.getNewTimeSlot() != null && !resolution.getNewTimeSlot().isEmpty()) {
                    LocalTime[] times = parseTimeSlot(resolution.getNewTimeSlot());
                    if (times != null) {
                        schedule.setStartTime(times[0]);
                        schedule.setEndTime(times[1]);
                    }
                }
                if (resolution.getNewClassroomId() != null) {
                    schedule.setRoomId(resolution.getNewClassroomId());
                }
                schedule.setStatus("RESCHEDULED");
                break;
            case 2: // 停课：取消本次排课
                schedule.setStatus("SUSPENDED");
                schedule.setCancelReason("教师请假停课");
                schedule.setCancelTime(LocalDateTime.now());
                break;
            default: break;
        }
        scheduleMapper.updateById(schedule);
        return rows;
    }

    /** 解析时段字符串 "14:00-16:00" 为 [startTime, endTime] */
    private LocalTime[] parseTimeSlot(String timeSlot) {
        try {
            String[] parts = timeSlot.split("-");
            if (parts.length == 2) {
                return new LocalTime[] {
                    LocalTime.parse(parts[0].trim()),
                    LocalTime.parse(parts[1].trim())
                };
            }
        } catch (Exception e) { /* 解析失败则跳过时间更新 */ }
        return null;
    }
}
