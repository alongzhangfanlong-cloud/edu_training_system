package com.education.store.module.teaching.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.store.module.teaching.entity.EduExamScoreStudent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EduExamScoreStudentMapper extends BaseMapper<EduExamScoreStudent> {

    @Select("SELECT * FROM edu_exam_score_student WHERE exam_score_id = #{examScoreId}")
    List<EduExamScoreStudent> selectByExamScoreId(@Param("examScoreId") Long examScoreId);

    @Select("SELECT * FROM edu_exam_score_student WHERE student_id = #{studentId} ORDER BY create_time DESC")
    List<EduExamScoreStudent> selectByStudentId(@Param("studentId") Long studentId);
}
