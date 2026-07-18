package com.education.store.module.student.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.exception.BusinessException;
import com.education.store.module.student.entity.EduStudentParent;
import com.education.store.module.student.mapper.EduStudentParentMapper;
import com.education.store.module.student.service.IEduStudentParentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

/**
 * 家长关联 Service 实现
 */
@Service
@RequiredArgsConstructor
public class EduStudentParentServiceImpl extends ServiceImpl<EduStudentParentMapper, EduStudentParent>
        implements IEduStudentParentService {

    private final EduStudentParentMapper parentMapper;

    @Override
    public IPage<EduStudentParent> selectParentList(IPage<EduStudentParent> page, Long studentId) {
        LambdaQueryWrapper<EduStudentParent> wrapper = new LambdaQueryWrapper<>();
        if (studentId != null) {
            wrapper.eq(EduStudentParent::getStudentId, studentId);
        }
        wrapper.orderByAsc(EduStudentParent::getSort);
        return parentMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertParent(EduStudentParent parent) {
        return parentMapper.insert(parent);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateParent(EduStudentParent parent) {
        EduStudentParent exist = parentMapper.selectById(parent.getId());
        if (exist == null) {
            throw new BusinessException("家长关联不存在");
        }
        return parentMapper.updateById(parent);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteParentByIds(Long[] ids) {
        // 仅删除关联，不删除 sys_user 记录
        return parentMapper.deleteBatchIds(Arrays.asList(ids));
    }

    @Override
    public boolean checkMobileUnique(EduStudentParent parent) {
        Long id = parent.getId() != null ? parent.getId() : -1L;
        EduStudentParent exist = lambdaQuery()
                .eq(EduStudentParent::getStudentId, parent.getStudentId())
                .eq(EduStudentParent::getMobile, parent.getMobile())
                .one();
        return exist == null || exist.getId().equals(id);
    }
}
