package com.education.store.module.attendance.service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.attendance.entity.EduLeaveTeacher;

public interface IEduLeaveTeacherService extends IService<EduLeaveTeacher> {
    IPage<EduLeaveTeacher> selectLeaveList(IPage<EduLeaveTeacher> page, EduLeaveTeacher leave);
    EduLeaveTeacher selectLeaveById(Long id);
    int insertLeave(EduLeaveTeacher leave);
    int approveLeave(Long id, Long approverId);
    int rejectLeave(Long id, Long approverId, String reason);
    int deleteLeaveByIds(Long[] ids);
}
