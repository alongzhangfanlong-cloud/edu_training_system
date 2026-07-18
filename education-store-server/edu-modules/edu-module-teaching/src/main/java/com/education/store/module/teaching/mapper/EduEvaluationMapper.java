package com.education.store.module.teaching.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.store.module.teaching.entity.EduEvaluation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EduEvaluationMapper extends BaseMapper<EduEvaluation> {

    @Select("SELECT * FROM edu_evaluation WHERE schedule_id = #{scheduleId} AND is_deleted = 0")
    EduEvaluation selectByScheduleId(@Param("scheduleId") Long scheduleId);
}
