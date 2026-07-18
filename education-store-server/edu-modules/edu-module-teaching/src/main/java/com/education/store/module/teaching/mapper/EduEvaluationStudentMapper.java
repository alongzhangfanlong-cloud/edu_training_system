package com.education.store.module.teaching.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.store.module.teaching.entity.EduEvaluationStudent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EduEvaluationStudentMapper extends BaseMapper<EduEvaluationStudent> {

    @Select("SELECT * FROM edu_evaluation_student WHERE evaluation_id = #{evaluationId}")
    List<EduEvaluationStudent> selectByEvaluationId(@Param("evaluationId") Long evaluationId);

    @Select("SELECT * FROM edu_evaluation_student WHERE student_id = #{studentId} ORDER BY create_time DESC")
    List<EduEvaluationStudent> selectByStudentId(@Param("studentId") Long studentId);
}
