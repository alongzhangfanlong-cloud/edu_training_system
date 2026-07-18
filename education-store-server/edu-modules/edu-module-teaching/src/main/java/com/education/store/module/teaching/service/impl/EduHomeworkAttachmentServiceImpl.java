package com.education.store.module.teaching.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.module.teaching.entity.EduHomeworkAttachment;
import com.education.store.module.teaching.mapper.EduHomeworkAttachmentMapper;
import com.education.store.module.teaching.service.IEduHomeworkAttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
        if (!attachments.isEmpty()) {
            attachmentMapper.deleteBatchIds(attachments.stream()
                    .map(EduHomeworkAttachment::getId).collect(Collectors.toList()));
        }
    }
}
