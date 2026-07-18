package com.education.store.module.course.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.exception.BusinessException;
import com.education.store.module.course.entity.EduSubject;
import com.education.store.module.course.mapper.EduSubjectMapper;
import com.education.store.module.course.service.IEduSubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

/**
 * 科目 Service 实现
 */
@Service
@RequiredArgsConstructor
public class EduSubjectServiceImpl extends ServiceImpl<EduSubjectMapper, EduSubject>
        implements IEduSubjectService {

    private final EduSubjectMapper subjectMapper;

    @Override
    public IPage<EduSubject> selectSubjectList(IPage<EduSubject> page, EduSubject subject) {
        LambdaQueryWrapper<EduSubject> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(subject.getName())) {
            wrapper.like(EduSubject::getName, subject.getName());
        }
        if (subject.getStatus() != null) {
            wrapper.eq(EduSubject::getStatus, subject.getStatus());
        }
        wrapper.orderByAsc(EduSubject::getSortOrder);
        return subjectMapper.selectPage(page, wrapper);
    }

    @Override
    public EduSubject selectSubjectById(Long id) {
        return subjectMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSubject(EduSubject subject) {
        return subjectMapper.insert(subject);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSubject(EduSubject subject) {
        EduSubject exist = subjectMapper.selectById(subject.getId());
        if (exist == null) {
            throw new BusinessException("科目不存在");
        }
        return subjectMapper.updateById(subject);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSubjectByIds(Long[] ids) {
        for (Long id : ids) {
            int refCount = subjectMapper.countReferencedByCourses(id);
            if (refCount > 0) {
                EduSubject subject = subjectMapper.selectById(id);
                throw new BusinessException(
                        String.format("科目「%s」下存在 %d 门课程，无法删除", subject.getName(), refCount));
            }
        }
        return subjectMapper.deleteBatchIds(Arrays.asList(ids));
    }

    @Override
    public boolean checkNameUnique(EduSubject subject) {
        Long id = subject.getId() != null ? subject.getId() : -1L;
        EduSubject exist = lambdaQuery()
                .eq(EduSubject::getName, subject.getName())
                .one();
        return exist == null || exist.getId().equals(id);
    }

    @Override
    public java.util.List<EduSubject> selectAllSubjects() {
        return lambdaQuery()
                .orderByAsc(EduSubject::getSortOrder)
                .list();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSubjectStatus(Long id, Integer status) {
        EduSubject subject = subjectMapper.selectById(id);
        if (subject == null) {
            throw new BusinessException("科目不存在");
        }
        subject.setStatus(status);
        return subjectMapper.updateById(subject);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSortBatch(java.util.List<com.education.store.module.course.dto.SubjectUpdateDTO> list) {
        int rows = 0;
        for (com.education.store.module.course.dto.SubjectUpdateDTO dto : list) {
            if (dto.getId() != null && dto.getSortOrder() != null) {
                EduSubject subject = subjectMapper.selectById(dto.getId());
                if (subject != null) {
                    subject.setSortOrder(dto.getSortOrder());
                    rows += subjectMapper.updateById(subject);
                }
            }
        }
        return rows;
    }
}
