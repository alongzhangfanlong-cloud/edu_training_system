package com.education.store.module.teaching.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.teaching.entity.EduHomeworkAttachment;

import java.util.List;

public interface IEduHomeworkAttachmentService extends IService<EduHomeworkAttachment> {

    List<EduHomeworkAttachment> selectByHomeworkId(Long homeworkId);

    void deleteByHomeworkId(Long homeworkId);
}
