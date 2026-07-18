package com.education.store.module.teaching.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.teaching.entity.EduEvaluation;
import com.education.store.module.teaching.entity.EduEvaluationStudent;

import java.util.List;

public interface IEduEvaluationService extends IService<EduEvaluation> {

    IPage<EduEvaluation> selectEvaluationList(IPage<EduEvaluation> page, Long scheduleId, Long teacherId, Long studentId, String startDate, String endDate, Integer status);

    EduEvaluation getDetail(Long id);

    EduEvaluation saveDraft(Long scheduleId, Long classId, Long teacherId, String summary, List<EduEvaluationStudent> students);

    EduEvaluation submitEvaluation(Long id, String summary, List<EduEvaluationStudent> students, Long teacherId);

    boolean deleteEvaluation(Long id, Long currentUserId);
}
