package com.education.store.module.attendance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.exception.BusinessException;
import com.education.store.module.attendance.entity.EduAttendance;
import com.education.store.module.attendance.entity.EduLeaveStudent;
import com.education.store.module.attendance.mapper.EduAttendanceMapper;
import com.education.store.module.attendance.mapper.EduLeaveStudentMapper;
import com.education.store.module.attendance.service.IEduLeaveStudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class EduLeaveStudentServiceImpl extends ServiceImpl<EduLeaveStudentMapper, EduLeaveStudent>
        implements IEduLeaveStudentService {

    private final EduLeaveStudentMapper leaveMapper;
    private final EduAttendanceMapper attendanceMapper;

    @Override
    public IPage<EduLeaveStudent> selectLeaveList(IPage<EduLeaveStudent> page, EduLeaveStudent leave) {
        LambdaQueryWrapper<EduLeaveStudent> wrapper = new LambdaQueryWrapper<>();
        if (leave.getStudentId() != null) wrapper.eq(EduLeaveStudent::getStudentId, leave.getStudentId());
        if (leave.getStatus() != null) wrapper.eq(EduLeaveStudent::getStatus, leave.getStatus());
        wrapper.orderByDesc(EduLeaveStudent::getCreateTime);
        return leaveMapper.selectPage(page, wrapper);
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
        return leaveMapper.updateById(leave);
    }

    @Override @Transactional(rollbackFor = Exception.class)
    public int deleteLeaveByIds(Long[] ids) { return leaveMapper.deleteBatchIds(Arrays.asList(ids)); }

    // 请假通过后自动向签到表写入请假记录(状态=3)
    private void writeAttendanceRecord(Long studentId, java.time.LocalDate leaveDate) {
        // 查该学生请假日期有排课的排课ID，暂无排课关联则跳过
        // 此处简化：实际应由job/schedule触发
    }
}
