package com.education.store.module.teaching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.exception.BusinessException;
import com.education.store.module.teaching.entity.EduHomework;
import com.education.store.module.teaching.entity.EduHomeworkAttachment;
import com.education.store.module.teaching.entity.EduHomeworkSubmission;
import com.education.store.module.teaching.mapper.EduHomeworkAttachmentMapper;
import com.education.store.module.teaching.mapper.EduHomeworkMapper;
import com.education.store.module.teaching.mapper.EduHomeworkSubmissionMapper;
import com.education.store.module.teaching.service.IEduHomeworkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class EduHomeworkServiceImpl extends ServiceImpl<EduHomeworkMapper, EduHomework>
        implements IEduHomeworkService {

    private final EduHomeworkMapper homeworkMapper;
    private final EduHomeworkAttachmentMapper attachmentMapper;
    private final EduHomeworkSubmissionMapper submissionMapper;

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
        if (attachmentIds != null && !attachmentIds.isEmpty()) {
            linkAttachments(homework.getId(), attachmentIds);
        }
        return homework;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateHomework(EduHomework homework, List<Long> attachmentIds) {
        EduHomework existing = homeworkMapper.selectById(homework.getId());
        if (existing == null) {
            throw new BusinessException("404", "作业不存在");
        }
        homework.setIsUpdated(1);
        if (attachmentIds != null && !attachmentIds.isEmpty()) {
            linkAttachments(homework.getId(), attachmentIds);
        }
        return updateById(homework);
    }

    @Override
    public Map<String, Object> getDetail(Long id) {
        EduHomework homework = homeworkMapper.selectById(id);
        if (homework == null) return Collections.emptyMap();

        Map<String, Object> result = new HashMap<>();
        result.put("id", homework.getId());
        result.put("classId", homework.getClassId());
        result.put("courseId", homework.getCourseId());
        result.put("scheduleId", homework.getScheduleId());
        result.put("title", homework.getTitle());
        result.put("content", homework.getContent());
        result.put("teacherId", homework.getTeacherId());
        result.put("isUpdated", homework.getIsUpdated());
        result.put("createTime", homework.getCreateTime());
        result.put("updateTime", homework.getUpdateTime());

        // 附件
        List<EduHomeworkAttachment> attachments = attachmentMapper.selectByHomeworkId(id);
        result.put("attachments", attachments);

        // 提交记录
        LambdaQueryWrapper<EduHomeworkSubmission> subWrapper = new LambdaQueryWrapper<>();
        subWrapper.eq(EduHomeworkSubmission::getHomeworkId, id);
        subWrapper.orderByDesc(EduHomeworkSubmission::getCreateTime);
        List<EduHomeworkSubmission> submissions = submissionMapper.selectList(subWrapper);
        result.put("submissions", submissions);

        return result;
    }

    private void linkAttachments(Long homeworkId, List<Long> attachmentIds) {
        // 删除旧附件关联
        LambdaQueryWrapper<EduHomeworkAttachment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduHomeworkAttachment::getHomeworkId, homeworkId);
        attachmentMapper.delete(wrapper);
        // 重新绑定
        for (int i = 0; i < attachmentIds.size(); i++) {
            EduHomeworkAttachment attachment = new EduHomeworkAttachment();
            attachment.setHomeworkId(homeworkId);
            attachment.setFileUrl(String.valueOf(attachmentIds.get(i)));
            attachment.setSortOrder(i);
            attachmentMapper.insert(attachment);
        }
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
