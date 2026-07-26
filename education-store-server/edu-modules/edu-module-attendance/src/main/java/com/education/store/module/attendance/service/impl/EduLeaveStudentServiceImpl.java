package com.education.store.module.attendance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.event.LeaveApprovedEvent;
import com.education.store.common.exception.BusinessException;
import com.education.store.module.attendance.entity.EduAttendance;
import com.education.store.module.attendance.entity.EduLeaveStudent;
import com.education.store.module.attendance.mapper.EduAttendanceMapper;
import com.education.store.module.attendance.mapper.EduLeaveStudentMapper;
import com.education.store.module.attendance.service.IEduLeaveStudentService;
import com.education.store.module.student.entity.EduStudent;
import com.education.store.module.student.mapper.EduStudentMapper;
import com.education.store.module.system.entity.system.SysUser;
import com.education.store.module.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EduLeaveStudentServiceImpl extends ServiceImpl<EduLeaveStudentMapper, EduLeaveStudent>
        implements IEduLeaveStudentService {

    private final EduLeaveStudentMapper leaveMapper;
    private final EduAttendanceMapper attendanceMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final EduStudentMapper studentMapper;
    private final SysUserMapper sysUserMapper;

    @Override
    public IPage<EduLeaveStudent> selectLeaveList(IPage<EduLeaveStudent> page, EduLeaveStudent leave) {
        LambdaQueryWrapper<EduLeaveStudent> wrapper = new LambdaQueryWrapper<>();
        if (leave.getStudentId() != null) wrapper.eq(EduLeaveStudent::getStudentId, leave.getStudentId());
        if (leave.getStatus() != null) wrapper.eq(EduLeaveStudent::getStatus, leave.getStatus());
        wrapper.orderByDesc(EduLeaveStudent::getCreateTime);
        IPage<EduLeaveStudent> result = leaveMapper.selectPage(page, wrapper);
        fillNamesForList(result.getRecords());
        return result;
    }

    @Override public EduLeaveStudent selectLeaveById(Long id) { return leaveMapper.selectById(id); }

    @Override @Transactional(rollbackFor = Exception.class)
    public int insertLeave(EduLeaveStudent leave) {
        leave.setStatus(0); // 待确认
        if ("TEACHER".equals(leave.getApplicantType())) {
            leave.setStatus(1); // 教师代请假直接通过
            leave.setConfirmTime(LocalDateTime.now());
            writeAttendanceRecord(leave.getStudentId(), leave.getLeaveDate());
        }
        return leaveMapper.insert(leave);
    }

    @Override @Transactional(rollbackFor = Exception.class)
    public int approveLeave(Long id, Long teacherId) {
        EduLeaveStudent leave = leaveMapper.selectById(id);
        if (leave == null) throw new BusinessException("请假记录不存在");
        leave.setStatus(1);
        leave.setConfirmTeacherId(teacherId);
        leave.setConfirmTime(LocalDateTime.now());
        writeAttendanceRecord(leave.getStudentId(), leave.getLeaveDate());
        // 发布请假审批通过事件（通知目标为家长）
        EduStudent student = studentMapper.selectById(leave.getStudentId());
        Long parentUserId = student != null ? student.getParentId() : null;
        String studentName = student != null ? student.getName() : "";
        eventPublisher.publishEvent(new LeaveApprovedEvent(
            this, id, "STUDENT", parentUserId, studentName, "通过", leave.getReason()));
        return leaveMapper.updateById(leave);
    }

    @Override @Transactional(rollbackFor = Exception.class)
    public int rejectLeave(Long id, Long teacherId, String reason) {
        EduLeaveStudent leave = leaveMapper.selectById(id);
        if (leave == null) throw new BusinessException("请假记录不存在");
        leave.setStatus(2);
        leave.setConfirmTeacherId(teacherId);
        leave.setConfirmTime(LocalDateTime.now());
        leave.setRejectReason(reason);
        // 发布请假审批驳回事件（通知目标为家长）
        EduStudent student = studentMapper.selectById(leave.getStudentId());
        Long parentUserId = student != null ? student.getParentId() : null;
        String studentName = student != null ? student.getName() : "";
        eventPublisher.publishEvent(new LeaveApprovedEvent(
            this, id, "STUDENT", parentUserId, studentName, "驳回", reason));
        return leaveMapper.updateById(leave);
    }

    @Override @Transactional(rollbackFor = Exception.class)
    public int deleteLeaveByIds(Long[] ids) { return leaveMapper.deleteBatchIds(Arrays.asList(ids)); }

    /**
     * 请假通过后自动向签到表写入请假记录 (status=3)
     * 查询该学生在请假日期有排课的班级，为每个排课写入一条签到记录
     */
    private void writeAttendanceRecord(Long studentId, LocalDate leaveDate) {
        List<Map<String, Object>> schedules = attendanceMapper.findSchedulesForStudent(studentId, leaveDate);
        for (Map<String, Object> row : schedules) {
            Long scheduleId = ((Number) row.get("scheduleId")).longValue();
            Long classId = ((Number) row.get("classId")).longValue();
            // 检查是否已有签到记录（避免重复写入）
            LambdaQueryWrapper<EduAttendance> check = new LambdaQueryWrapper<>();
            check.eq(EduAttendance::getScheduleId, scheduleId)
                .eq(EduAttendance::getStudentId, studentId);
            if (attendanceMapper.selectCount(check) > 0) continue;
            // 写入签到记录：状态=请假
            EduAttendance att = new EduAttendance();
            att.setScheduleId(scheduleId);
            att.setClassId(classId);
            att.setStudentId(studentId);
            att.setStatus(EduAttendance.STATUS_LEAVE);
            att.setSigninTime(LocalDateTime.now());
            att.setOperatorType("SYSTEM");
            attendanceMapper.insert(att);
        }
    }

    private void fillNamesForList(List<EduLeaveStudent> list) {
        if (list == null || list.isEmpty()) return;
        Set<Long> studentIds = new HashSet<>();
        Set<Long> teacherIds = new HashSet<>();
        for (EduLeaveStudent leave : list) {
            if (leave.getStudentId() != null) studentIds.add(leave.getStudentId());
            if (leave.getConfirmTeacherId() != null) teacherIds.add(leave.getConfirmTeacherId());
        }
        Map<Long, EduStudent> studentMap = studentIds.isEmpty() ? Collections.emptyMap()
                : studentMapper.selectBatchIds(studentIds).stream().collect(Collectors.toMap(EduStudent::getId, s -> s));
        Map<Long, SysUser> userMap = teacherIds.isEmpty() ? Collections.emptyMap()
                : sysUserMapper.selectBatchIds(teacherIds).stream().collect(Collectors.toMap(SysUser::getId, u -> u));
        for (EduLeaveStudent leave : list) {
            if (leave.getStudentId() != null) {
                EduStudent student = studentMap.get(leave.getStudentId());
                if (student != null) leave.setStudentName(student.getName());
            }
            if (leave.getConfirmTeacherId() != null) {
                SysUser user = userMap.get(leave.getConfirmTeacherId());
                if (user != null) leave.setConfirmTeacherName(user.getRealName());
            }
        }
    }
}
