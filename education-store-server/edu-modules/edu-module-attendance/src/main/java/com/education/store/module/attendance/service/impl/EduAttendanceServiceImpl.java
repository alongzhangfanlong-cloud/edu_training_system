package com.education.store.module.attendance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.event.SignInEvent;
import com.education.store.common.exception.BusinessException;
import com.education.store.module.attendance.entity.EduAttendance;
import com.education.store.module.attendance.mapper.EduAttendanceMapper;
import com.education.store.module.attendance.service.IEduAttendanceService;
import com.education.store.module.course.entity.EduCourse;
import com.education.store.module.course.mapper.EduCourseMapper;
import com.education.store.module.schedule.entity.EduSchedule;
import com.education.store.module.schedule.mapper.EduScheduleMapper;
import com.education.store.module.student.entity.EduStudentHoursLog;
import com.education.store.module.student.mapper.EduStudentHoursLogMapper;
import com.education.store.module.student.mapper.EduStudentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.education.store.common.utils.SecurityUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EduAttendanceServiceImpl extends ServiceImpl<EduAttendanceMapper, EduAttendance>
        implements IEduAttendanceService {

    private final EduAttendanceMapper attendanceMapper;
    private final EduStudentHoursLogMapper hoursLogMapper;
    private final EduStudentMapper studentMapper;
    private final EduScheduleMapper scheduleMapper;
    private final EduCourseMapper courseMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public IPage<EduAttendance> selectAttendanceList(IPage<EduAttendance> page, Long scheduleId, Long classId, Long studentId) {
        LambdaQueryWrapper<EduAttendance> wrapper = new LambdaQueryWrapper<>();
        if (scheduleId != null) wrapper.eq(EduAttendance::getScheduleId, scheduleId);
        if (classId != null) wrapper.eq(EduAttendance::getClassId, classId);
        if (studentId != null) wrapper.eq(EduAttendance::getStudentId, studentId);
        wrapper.orderByDesc(EduAttendance::getCreateTime);
        return attendanceMapper.selectPage(page, wrapper);
    }

    @Override
    public List<EduAttendance> selectByScheduleId(Long scheduleId) {
        LambdaQueryWrapper<EduAttendance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduAttendance::getScheduleId, scheduleId);
        return attendanceMapper.selectList(wrapper);
    }

    @Override
    public Map<String, Long> getStatsBySchedule(Long scheduleId) {
        List<EduAttendance> records = selectByScheduleId(scheduleId);
        long present = records.stream().filter(r -> r.getStatus() == EduAttendance.STATUS_PRESENT).count();
        long late = records.stream().filter(r -> r.getStatus() == EduAttendance.STATUS_LATE).count();
        long absent = records.stream().filter(r -> r.getStatus() == EduAttendance.STATUS_ABSENT).count();
        long leave = records.stream().filter(r -> r.getStatus() == EduAttendance.STATUS_LEAVE || r.getStatus() == 4).count();
        return Map.of("present", present, "late", late, "absent", absent, "leave", leave, "total", (long) records.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchSignin(Long scheduleId, Long classId, List<EduAttendance> records, Long operatorId, String operatorType) {
        // 检查排课类型：试听课不消课
        EduSchedule schedule = scheduleMapper.selectById(scheduleId);
        boolean isTrial = schedule != null && "TRIAL".equals(schedule.getScheduleType());

        int count = 0;
        for (EduAttendance record : records) {
            record.setScheduleId(scheduleId);
            record.setClassId(classId);
            record.setSigninTime(LocalDateTime.now());
            record.setOperatorId(operatorId);
            record.setOperatorType(operatorType);
            // 消课：到校或迟到 → 扣1课时（试听课除外）
            if (!isTrial && (record.getStatus() == EduAttendance.STATUS_PRESENT || record.getStatus() == EduAttendance.STATUS_LATE)) {
                consumeHours(record.getStudentId(), scheduleId);
            }
            attendanceMapper.insert(record);
            count++;
        }
        // 发布签到事件，异步通知家长和积分发放
        String courseName = "";
        if (schedule != null && schedule.getCourseId() != null) {
            try {
                EduCourse course = courseMapper.selectById(schedule.getCourseId());
                if (course != null) {
                    courseName = course.getName();
                }
            } catch (Exception e) {
                log.warn("查询课程名称失败: courseId={}", schedule.getCourseId(), e);
            }
        }
        for (EduAttendance record : records) {
            eventPublisher.publishEvent(new SignInEvent(
                this, record.getStudentId(), scheduleId, courseName, LocalDateTime.now()));
        }
        // 自动消课检查：班级所有学员都完成考勤后，自动标记排课已消课
        autoConsumeScheduleIfAllSignedIn(scheduleId);
        return count;
    }

    /**
     * 自动消课检查：班级所有在学学员都已完成考勤时，自动标记排课为已消课
     */
    private void autoConsumeScheduleIfAllSignedIn(Long scheduleId) {
        try {
            int totalStudents = scheduleMapper.countClassEnrolledStudents(scheduleId);
            int signedStudents = scheduleMapper.countAttendedStudents(scheduleId);
            if (totalStudents > 0 && signedStudents >= totalStudents) {
                String consumeBy = SecurityUtils.getUserId() != null ? SecurityUtils.getUserId().toString() : "system";
                scheduleMapper.markConsumed(scheduleId, consumeBy);
            }
        } catch (Exception e) {
            // 自动消课失败不影响主流程
            log.warn("自动消课检查失败：scheduleId={}", scheduleId, e);
        }
    }

    private void consumeHours(Long studentId, Long scheduleId) {
        BigDecimal hoursToDeduct = BigDecimal.ONE;
        // 原子扣减课时，防止并发超扣
        int updated = studentMapper.deductHoursAtomic(studentId, hoursToDeduct);
        if (updated == 0) {
            // 扣减失败，查询当前余额用于错误提示
            BigDecimal current = hoursLogMapper.sumBalanceByStudent(studentId);
            if (current == null) current = BigDecimal.ZERO;
            throw new BusinessException("学员课时余额不足，无法消课：studentId=" + studentId + ", 当前余额=" + current);
        }
        // 获取扣减后的余额
        BigDecimal balanceAfter = hoursLogMapper.sumBalanceByStudent(studentId);
        if (balanceAfter == null) balanceAfter = BigDecimal.ZERO;

        EduStudentHoursLog log = new EduStudentHoursLog();
        log.setStudentId(studentId);
        log.setChangeType("CONSUME");
        log.setChangeAmount(hoursToDeduct.negate());
        log.setSourceType("ATTENDANCE");
        log.setSourceId(scheduleId);
        log.setBalanceAfter(balanceAfter);
        hoursLogMapper.insert(log);
    }
}
