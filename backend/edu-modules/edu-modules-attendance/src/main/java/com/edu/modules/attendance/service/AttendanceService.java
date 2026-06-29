package com.edu.modules.attendance.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.edu.common.core.enums.AttendanceStatusEnum;
import com.edu.common.core.enums.CheckInMethodEnum;
import com.edu.common.core.exception.BusinessException;
import com.edu.common.core.exception.ErrorCode;
import com.edu.modules.attendance.dto.BatchCheckInRequest;
import com.edu.modules.attendance.entity.AttendanceEntity;
import com.edu.modules.attendance.mapper.AttendanceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceMapper attendanceMapper;

    /**
     * 教师批量点名
     */
    @Transactional
    public Map<String, Object> batchCheckIn(BatchCheckInRequest request) {
        int processed = 0;
        List<Map<String, Object>> results = new ArrayList<>();

        for (BatchCheckInRequest.Attendee attendee : request.getAttendances()) {
            // 检查是否已存在考勤记录
            LambdaQueryWrapper<AttendanceEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AttendanceEntity::getScheduleId, request.getScheduleId())
                    .eq(AttendanceEntity::getStudentId, attendee.getStudentId());
            AttendanceEntity existing = attendanceMapper.selectOne(wrapper);

            if (existing != null) {
                // 更新已有记录
                existing.setStatus(attendee.getStatus());
                existing.setCheckInMethod(CheckInMethodEnum.TEACHER_CALL.getCode());
                existing.setCheckInTime(LocalDateTime.now());
                existing.setRemark(attendee.getRemark());
                attendanceMapper.updateById(existing);
            } else {
                // 创建新记录
                AttendanceEntity entity = new AttendanceEntity();
                entity.setScheduleId(request.getScheduleId());
                entity.setStudentId(attendee.getStudentId());
                entity.setStatus(attendee.getStatus());
                entity.setCheckInMethod(CheckInMethodEnum.TEACHER_CALL.getCode());
                entity.setCheckInTime(LocalDateTime.now());
                entity.setRemark(attendee.getRemark());
                entity.setIsMakeup(0);
                attendanceMapper.insert(entity);
            }

            Map<String, Object> r = new HashMap<>();
            r.put("studentId", attendee.getStudentId());
            r.put("status", attendee.getStatus());
            r.put("statusName", AttendanceStatusEnum.getDesc(attendee.getStatus()));
            results.add(r);
            processed++;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("processedCount", processed);
        result.put("results", results);
        log.info("批量点名完成: scheduleId={}, processed={}", request.getScheduleId(), processed);
        return result;
    }

    /**
     * 查询某次排课的考勤列表
     */
    public List<AttendanceEntity> getByScheduleId(Long scheduleId) {
        LambdaQueryWrapper<AttendanceEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttendanceEntity::getScheduleId, scheduleId);
        return attendanceMapper.selectList(wrapper);
    }

    /**
     * 按学员ID查询考勤列表
     */
    public List<AttendanceEntity> getByStudentId(Long studentId) {
        LambdaQueryWrapper<AttendanceEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttendanceEntity::getStudentId, studentId)
               .orderByDesc(AttendanceEntity::getCreatedAt);
        return attendanceMapper.selectList(wrapper);
    }

    /**
     * 获取学员的考勤统计
     */
    public Map<String, Object> getStudentStats(Long studentId) {
        LambdaQueryWrapper<AttendanceEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttendanceEntity::getStudentId, studentId);
        List<AttendanceEntity> list = attendanceMapper.selectList(wrapper);

        long total = list.size();
        long present = list.stream().filter(a -> a.getStatus() == 1).count();
        long leave = list.stream().filter(a -> a.getStatus() == 2).count();
        long absent = list.stream().filter(a -> a.getStatus() == 3).count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", (int) total);
        stats.put("present", (int) present);
        stats.put("leave", (int) leave);
        stats.put("absent", (int) absent);
        stats.put("presentRate", total > 0 ? Math.round(present * 10000.0 / total) / 100.0 : 0);
        return stats;
    }

    /**
     * 获取或创建考勤记录
     */
    public AttendanceEntity getOrCreate(Long scheduleId, Long studentId, Long classId) {
        LambdaQueryWrapper<AttendanceEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttendanceEntity::getScheduleId, scheduleId)
                .eq(AttendanceEntity::getStudentId, studentId);
        AttendanceEntity existing = attendanceMapper.selectOne(wrapper);
        if (existing != null) return existing;

        AttendanceEntity entity = new AttendanceEntity();
        entity.setScheduleId(scheduleId);
        entity.setStudentId(studentId);
        entity.setClassId(classId);
        entity.setStatus(1); // 默认出勤
        entity.setIsMakeup(0);
        attendanceMapper.insert(entity);
        return entity;
    }
}
