package com.education.store.module.attendance.service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.attendance.entity.EduLeaveStudent;

public interface IEduLeaveStudentService extends IService<EduLeaveStudent> {
    IPage<EduLeaveStudent> selectLeaveList(IPage<EduLeaveStudent> page, EduLeaveStudent leave);
    EduLeaveStudent selectLeaveById(Long id);
    int insertLeave(EduLeaveStudent leave);
    int approveLeave(Long id, Long teacherId);
    int rejectLeave(Long id, Long teacherId, String reason);
    int deleteLeaveByIds(Long[] ids);
}
