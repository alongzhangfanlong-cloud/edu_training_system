package com.education.store.module.teaching.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.module.teaching.entity.EduHomeworkSubmission;
import com.education.store.module.teaching.mapper.EduHomeworkSubmissionMapper;
import com.education.store.module.teaching.service.IEduHomeworkSubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 作业提交 Service 实现
 */
@Service
@RequiredArgsConstructor
public class EduHomeworkSubmissionServiceImpl extends ServiceImpl<EduHomeworkSubmissionMapper, EduHomeworkSubmission>
        implements IEduHomeworkSubmissionService {

    private final EduHomeworkSubmissionMapper submissionMapper;

    @Override
    public int countPendingGrading(Long teacherId) {
        return submissionMapper.countPendingGrading(teacherId);
    }
}
