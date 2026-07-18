package com.education.store.module.teaching.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.teaching.entity.EduTeacherEvaluation;

import java.util.Map;

public interface IEduTeacherEvaluationService extends IService<EduTeacherEvaluation> {

    IPage<EduTeacherEvaluation> selectList(IPage<EduTeacherEvaluation> page, Long teacherId, Long studentId, String status, String startDate, String endDate);

    EduTeacherEvaluation submit(EduTeacherEvaluation evaluation);

    boolean hideEvaluation(Long id);

    boolean restoreEvaluation(Long id);

    Map<String, Object> getTeacherStats(Long teacherId);
}
