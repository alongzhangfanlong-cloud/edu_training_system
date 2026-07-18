package com.education.store.module.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.store.module.student.entity.EduStudentHoursLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

/**
 * 课时变动日志 Mapper（只追加，不可修改/删除）
 */
@Mapper
public interface EduStudentHoursLogMapper extends BaseMapper<EduStudentHoursLog> {

    /** 查询学员当前剩余总课时（全部课程汇总） */
    @Select("SELECT COALESCE(SUM(change_amount), 0) FROM edu_student_hours_log WHERE student_id = #{studentId}")
    BigDecimal sumBalanceByStudent(@Param("studentId") Long studentId);

    /** 查询学员当前已获得总课时（仅正数汇总） */
    @Select("SELECT COALESCE(SUM(change_amount), 0) FROM edu_student_hours_log WHERE student_id = #{studentId} AND change_amount > 0")
    BigDecimal sumTotalGainedByStudent(@Param("studentId") Long studentId);

    /** 查询学员某课程的当前剩余课时（FOR UPDATE 行锁） */
    @Select("SELECT COALESCE(SUM(change_amount), 0) FROM edu_student_hours_log WHERE student_id = #{studentId} AND course_id = #{courseId}")
    BigDecimal sumBalanceByStudentAndCourse(@Param("studentId") Long studentId, @Param("courseId") Long courseId);
}
