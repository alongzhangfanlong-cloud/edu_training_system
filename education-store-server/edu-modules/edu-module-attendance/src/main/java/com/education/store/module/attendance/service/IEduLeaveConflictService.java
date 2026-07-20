package com.education.store.module.attendance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.attendance.entity.EduLeaveConflict;

import java.util.List;
import java.util.Map;

public interface IEduLeaveConflictService extends IService<EduLeaveConflict> {
    List<EduLeaveConflict> selectByLeaveId(Long leaveId);
    List<Map<String, Object>> selectDetailsByLeaveId(Long leaveId);
    int resolveConflict(Long id, EduLeaveConflict resolution);
}
