package com.education.store.module.attendance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.exception.BusinessException;
import com.education.store.module.attendance.entity.EduLeaveConflict;
import com.education.store.module.attendance.entity.EduLeaveTeacher;
import com.education.store.module.attendance.mapper.EduLeaveConflictMapper;
import com.education.store.module.attendance.mapper.EduLeaveTeacherMapper;
import com.education.store.module.attendance.service.IEduLeaveTeacherService;
import com.education.store.module.schedule.entity.EduSchedule;
import com.education.store.module.schedule.mapper.EduScheduleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EduLeaveTeacherServiceImpl extends ServiceImpl<EduLeaveTeacherMapper, EduLeaveTeacher>
        implements IEduLeaveTeacherService {

    private final EduLeaveTeacherMapper leaveMapper;
    private final EduScheduleMapper scheduleMapper;
    private final EduLeaveConflictMapper conflictMapper;

    @Override
    public IPage<EduLeaveTeacher> selectLeaveList(IPage<EduLeaveTeacher> page, EduLeaveTeacher leave) {
        LambdaQueryWrapper<EduLeaveTeacher> wrapper = new LambdaQueryWrapper<>();
        if (leave.getTeacherId() != null) wrapper.eq(EduLeaveTeacher::getTeacherId, leave.getTeacherId());
        if (leave.getStatus() != null) wrapper.eq(EduLeaveTeacher::getStatus, leave.getStatus());
        wrapper.orderByDesc(EduLeaveTeacher::getCreateTime);
        return leaveMapper.selectPage(page, wrapper);
    }

    @Override public EduLeaveTeacher selectLeaveById(Long id) { return leaveMapper.selectById(id); }

    @Override @Transactional(rollbackFor = Exception.class)
    public int insertLeave(EduLeaveTeacher leave) {
        leave.setStatus(0);
        return leaveMapper.insert(leave);
    }

    @Override @Transactional(rollbackFor = Exception.class)
    public int approveLeave(Long id, Long approverId) {
        EduLeaveTeacher leave = leaveMapper.selectById(id);
        if (leave == null) throw new BusinessException("请假记录不存在");
        leave.setStatus(1);
        leave.setApproverId(approverId);
        leave.setApproveTime(LocalDateTime.now());
        int rows = leaveMapper.updateById(leave);

        // 冲突检测：查询该教师在请假日期范围内的所有正常/补课排课
        LambdaQueryWrapper<EduSchedule> sw = new LambdaQueryWrapper<>();
        sw.eq(EduSchedule::getTeacherId, leave.getTeacherId())
          .ge(EduSchedule::getScheduleDate, leave.getLeaveStartDate())
          .le(EduSchedule::getScheduleDate, leave.getLeaveEndDate())
          .in(EduSchedule::getStatus, "NORMAL", "MAKEUP");
        List<EduSchedule> schedules = scheduleMapper.selectList(sw);
        for (EduSchedule s : schedules) {
            // 检查是否已存在冲突记录（避免重复生成）
            LambdaQueryWrapper<EduLeaveConflict> cw = new LambdaQueryWrapper<>();
            cw.eq(EduLeaveConflict::getLeaveId, leave.getId())
              .eq(EduLeaveConflict::getScheduleId, s.getId());
            if (conflictMapper.selectCount(cw) > 0) continue;
            EduLeaveConflict c = new EduLeaveConflict();
            c.setLeaveId(leave.getId());
            c.setScheduleId(s.getId());
            c.setStatus(0); // 待处理
            conflictMapper.insert(c);
        }
        return rows;
    }

    @Override @Transactional(rollbackFor = Exception.class)
    public int rejectLeave(Long id, Long approverId, String reason) {
        EduLeaveTeacher leave = leaveMapper.selectById(id);
        if (leave == null) throw new BusinessException("请假记录不存在");
        leave.setStatus(2);
        leave.setApproverId(approverId);
        leave.setRejectReason(reason);
        leave.setApproveTime(LocalDateTime.now());
        return leaveMapper.updateById(leave);
    }

    @Override @Transactional(rollbackFor = Exception.class)
    public int deleteLeaveByIds(Long[] ids) { return leaveMapper.deleteBatchIds(Arrays.asList(ids)); }
}
