package com.education.store.module.attendance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.exception.BusinessException;
import com.education.store.module.attendance.entity.EduAttendance;
import com.education.store.module.attendance.mapper.EduAttendanceMapper;
import com.education.store.module.attendance.service.IEduAttendanceService;
import com.education.store.module.schedule.entity.EduSchedule;
import com.education.store.module.schedule.mapper.EduScheduleMapper;
import com.education.store.module.student.entity.EduStudentHoursLog;
import com.education.store.module.student.mapper.EduStudentHoursLogMapper;
import com.education.store.module.student.mapper.EduStudentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EduAttendanceServiceImpl extends ServiceImpl<EduAttendanceMapper, EduAttendance>
        implements IEduAttendanceService {

    private final EduAttendanceMapper attendanceMapper;
    private final EduStudentHoursLogMapper hoursLogMapper;
    private final EduStudentMapper studentMapper;
    private final EduScheduleMapper scheduleMapper;

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
        return count;
    }

    private void consumeHours(Long studentId, Long scheduleId) {
        // 使用数据库原子更新：余额不足时拒绝消课
        BigDecimal current = hoursLogMapper.sumBalanceByStudent(studentId);
        if (current == null) current = BigDecimal.ZERO;
        if (current.compareTo(BigDecimal.ONE) < 0) {
            throw new BusinessException("学员课时余额不足，无法消课：studentId=" + studentId + ", 当前余额=" + current);
        }
        BigDecimal balanceAfter = current.subtract(BigDecimal.ONE);

        EduStudentHoursLog log = new EduStudentHoursLog();
        log.setStudentId(studentId);
        log.setChangeType("CONSUME");
        log.setChangeAmount(new BigDecimal("-1"));
        log.setSourceType("ATTENDANCE");
        log.setSourceId(scheduleId);
        log.setBalanceAfter(balanceAfter);
        hoursLogMapper.insert(log);

        var student = studentMapper.selectById(studentId);
        if (student != null) {
            student.setRemainingHours(balanceAfter);
            student.setLastVisitTime(LocalDateTime.now());
            studentMapper.updateById(student);
        }
    }
}
