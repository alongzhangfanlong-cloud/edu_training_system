package com.education.store.module.teaching.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.teaching.entity.EduEvaluationStudent;

import java.util.List;

public interface IEduEvaluationStudentService extends IService<EduEvaluationStudent> {

    List<EduEvaluationStudent> selectByEvaluationId(Long evaluationId);

    List<EduEvaluationStudent> selectByStudentId(Long studentId);

    void deleteByEvaluationId(Long evaluationId);
}
