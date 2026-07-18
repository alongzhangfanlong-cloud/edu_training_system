package com.education.store.module.teaching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.exception.BusinessException;
import com.education.store.module.teaching.entity.EduHomework;
import com.education.store.module.teaching.mapper.EduHomeworkAttachmentMapper;
import com.education.store.module.teaching.mapper.EduHomeworkMapper;
import com.education.store.module.teaching.service.IEduHomeworkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EduHomeworkServiceImpl extends ServiceImpl<EduHomeworkMapper, EduHomework>
        implements IEduHomeworkService {

    private final EduHomeworkMapper homeworkMapper;
    private final EduHomeworkAttachmentMapper attachmentMapper;

    @Override
    public IPage<EduHomework> selectHomeworkList(IPage<EduHomework> page, String keyword, Long classId, Long teacherId, String startDate, String endDate) {
        LambdaQueryWrapper<EduHomework> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(EduHomework::getTitle, keyword);
        }
        if (classId != null) {
            wrapper.eq(EduHomework::getClassId, classId);
        }
        if (teacherId != null) {
            wrapper.eq(EduHomework::getTeacherId, teacherId);
        }
        if (startDate != null && !startDate.isEmpty()) {
            wrapper.ge(EduHomework::getCreateTime, startDate + " 00:00:00");
        }
        if (endDate != null && !endDate.isEmpty()) {
            wrapper.le(EduHomework::getCreateTime, endDate + " 23:59:59");
        }
        wrapper.orderByDesc(EduHomework::getCreateTime);
        return homeworkMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EduHomework addHomework(EduHomework homework, List<Long> attachmentIds) {
        homeworkMapper.insert(homework);
        return homework;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateHomework(EduHomework homework) {
        EduHomework existing = homeworkMapper.selectById(homework.getId());
        if (existing == null) {
            throw new BusinessException("404", "作业不存在");
        }
        homework.setIsUpdated(1);
        return updateById(homework);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteHomework(Long id, Long currentUserId) {
        EduHomework existing = homeworkMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("404", "作业不存在");
        }
        return removeById(id);
    }
}
