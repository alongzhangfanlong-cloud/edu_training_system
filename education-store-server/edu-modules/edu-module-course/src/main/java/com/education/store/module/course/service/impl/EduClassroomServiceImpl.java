package com.education.store.module.course.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.exception.BusinessException;
import com.education.store.module.course.entity.EduClassroom;
import com.education.store.module.course.mapper.EduClassroomMapper;
import com.education.store.module.course.service.IEduClassroomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

/**
 * 教室 Service 实现
 */
@Service
@RequiredArgsConstructor
public class EduClassroomServiceImpl extends ServiceImpl<EduClassroomMapper, EduClassroom>
        implements IEduClassroomService {

    private final EduClassroomMapper classroomMapper;

    @Override
    public IPage<EduClassroom> selectClassroomList(IPage<EduClassroom> page, EduClassroom classroom) {
        LambdaQueryWrapper<EduClassroom> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(classroom.getName())) {
            wrapper.like(EduClassroom::getName, classroom.getName());
        }
        if (classroom.getStatus() != null) {
            wrapper.eq(EduClassroom::getStatus, classroom.getStatus());
        }
        if (StrUtil.isNotBlank(classroom.getLocation())) {
            wrapper.like(EduClassroom::getLocation, classroom.getLocation());
        }
        wrapper.orderByAsc(EduClassroom::getCreateTime);
        return classroomMapper.selectPage(page, wrapper);
    }

    @Override
    public EduClassroom selectClassroomById(Long id) {
        return classroomMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertClassroom(EduClassroom classroom) {
        validateCapacity(classroom.getCapacity());
        return classroomMapper.insert(classroom);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateClassroom(EduClassroom classroom) {
        EduClassroom exist = classroomMapper.selectById(classroom.getId());
        if (exist == null) {
            throw new BusinessException("教室不存在");
        }
        validateCapacity(classroom.getCapacity());
        return classroomMapper.updateById(classroom);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteClassroomByIds(Long[] ids) {
        for (Long id : ids) {
            int refCount = classroomMapper.countReferencedBySchedules(id);
            if (refCount > 0) {
                EduClassroom classroom = classroomMapper.selectById(id);
                throw new BusinessException(
                        String.format("教室「%s」已被 %d 条排课记录引用，不可删除，仅可停用",
                                classroom != null ? classroom.getName() : "未知", refCount));
            }
        }
        return classroomMapper.deleteBatchIds(Arrays.asList(ids));
    }

    @Override
    public boolean checkNameUnique(EduClassroom classroom) {
        Long id = classroom.getId() != null ? classroom.getId() : -1L;
        EduClassroom exist = lambdaQuery()
                .eq(EduClassroom::getName, classroom.getName())
                .one();
        return exist == null || exist.getId().equals(id);
    }

    @Override
    public java.util.List<EduClassroom> selectAllEnabled() {
        return lambdaQuery()
                .eq(EduClassroom::getStatus, 1) // 1=启用
                .orderByAsc(EduClassroom::getCreateTime)
                .list();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateClassroomStatus(Long id, Integer status) {
        EduClassroom classroom = classroomMapper.selectById(id);
        if (classroom == null) {
            throw new BusinessException("教室不存在");
        }
        classroom.setStatus(status);
        return classroomMapper.updateById(classroom);
    }

    /** 容量范围校验：0=不限, 1-999=有效 */
    private void validateCapacity(Integer capacity) {
        if (capacity != null && capacity < 0) {
            throw new BusinessException("容量不能为负数");
        }
        if (capacity != null && capacity > 999) {
            throw new BusinessException("容量不能超过 999");
        }
    }
}
