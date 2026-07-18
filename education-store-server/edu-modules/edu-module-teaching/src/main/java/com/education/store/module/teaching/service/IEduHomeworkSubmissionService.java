package com.education.store.module.teaching.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.teaching.entity.EduHomeworkSubmission;

/**
 * 作业提交 Service
 */
public interface IEduHomeworkSubmissionService extends IService<EduHomeworkSubmission> {

    /**
     * 按教师ID统计待批改作业数
     */
    int countPendingGrading(Long teacherId);
}
