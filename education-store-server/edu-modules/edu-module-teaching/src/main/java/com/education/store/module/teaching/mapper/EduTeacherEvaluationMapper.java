package com.education.store.module.teaching.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.store.module.teaching.entity.EduTeacherEvaluation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

@Mapper
public interface EduTeacherEvaluationMapper extends BaseMapper<EduTeacherEvaluation> {

    @Select("SELECT AVG(rating_score) FROM edu_teacher_evaluation WHERE teacher_id = #{teacherId} AND status = 'ACTIVE' AND deleted = 0")
    BigDecimal getAvgScoreByTeacherId(@Param("teacherId") Long teacherId);

    @Select("SELECT COUNT(*) FROM edu_teacher_evaluation WHERE teacher_id = #{teacherId} AND status = 'ACTIVE' AND deleted = 0")
    int countByTeacherId(@Param("teacherId") Long teacherId);
}
