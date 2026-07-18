package com.education.store.module.teaching.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.teaching.entity.EduExamScore;
import com.education.store.module.teaching.entity.EduExamScoreStudent;

import java.util.List;

public interface IEduExamScoreService extends IService<EduExamScore> {

    IPage<EduExamScore> selectExamList(IPage<EduExamScore> page, Long classId, Long subjectId, String examType, String startDate, String endDate);

    EduExamScore addExam(EduExamScore exam, List<EduExamScoreStudent> students);

    boolean updateExam(EduExamScore exam, List<EduExamScoreStudent> students);

    boolean deleteExam(Long id, Long currentUserId);

    List<EduExamScoreStudent> getStudentsByExamId(Long examScoreId);
}
