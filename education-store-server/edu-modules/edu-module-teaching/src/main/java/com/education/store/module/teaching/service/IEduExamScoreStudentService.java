package com.education.store.module.teaching.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.teaching.entity.EduExamScoreStudent;

import java.util.List;

public interface IEduExamScoreStudentService extends IService<EduExamScoreStudent> {

    List<EduExamScoreStudent> selectByExamScoreId(Long examScoreId);

    List<EduExamScoreStudent> selectByStudentId(Long studentId);

    void deleteByExamScoreId(Long examScoreId);
}
