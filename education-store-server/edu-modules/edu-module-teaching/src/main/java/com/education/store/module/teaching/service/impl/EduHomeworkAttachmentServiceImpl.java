package com.education.store.module.teaching.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.module.teaching.entity.EduHomeworkAttachment;
import com.education.store.module.teaching.mapper.EduHomeworkAttachmentMapper;
import com.education.store.module.teaching.service.IEduHomeworkAttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EduHomeworkAttachmentServiceImpl extends ServiceImpl<EduHomeworkAttachmentMapper, EduHomeworkAttachment>
        implements IEduHomeworkAttachmentService {

    private final EduHomeworkAttachmentMapper attachmentMapper;

    @Override
    public List<EduHomeworkAttachment> selectByHomeworkId(Long homeworkId) {
        return attachmentMapper.selectByHomeworkId(homeworkId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByHomeworkId(Long homeworkId) {
        List<EduHomeworkAttachment> attachments = attachmentMapper.selectByHomeworkId(homeworkId);
        for (EduHomeworkAttachment attachment : attachments) {
            attachmentMapper.deleteById(attachment.getId());
        }
    }
}
