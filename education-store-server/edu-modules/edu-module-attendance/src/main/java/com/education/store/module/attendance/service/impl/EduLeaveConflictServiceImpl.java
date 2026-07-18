package com.education.store.module.attendance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.exception.BusinessException;
import com.education.store.module.attendance.entity.EduLeaveConflict;
import com.education.store.module.attendance.mapper.EduLeaveConflictMapper;
import com.education.store.module.attendance.service.IEduLeaveConflictService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EduLeaveConflictServiceImpl extends ServiceImpl<EduLeaveConflictMapper, EduLeaveConflict>
        implements IEduLeaveConflictService {

    private final EduLeaveConflictMapper conflictMapper;

    @Override
    public List<EduLeaveConflict> selectByLeaveId(Long leaveId) {
        LambdaQueryWrapper<EduLeaveConflict> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduLeaveConflict::getLeaveId, leaveId);
        return conflictMapper.selectList(wrapper);
    }

    @Override @Transactional(rollbackFor = Exception.class)
    public int resolveConflict(Long id, EduLeaveConflict resolution) {
        EduLeaveConflict conflict = conflictMapper.selectById(id);
        if (conflict == null) throw new BusinessException("冲突记录不存在");
        conflict.setResolveType(resolution.getResolveType());
        conflict.setSubstituteTeacherId(resolution.getSubstituteTeacherId());
        conflict.setNewScheduleDate(resolution.getNewScheduleDate());
        conflict.setNewTimeSlot(resolution.getNewTimeSlot());
        conflict.setNewClassroomId(resolution.getNewClassroomId());
        conflict.setStatus(1);
        return conflictMapper.updateById(conflict);
    }
}
