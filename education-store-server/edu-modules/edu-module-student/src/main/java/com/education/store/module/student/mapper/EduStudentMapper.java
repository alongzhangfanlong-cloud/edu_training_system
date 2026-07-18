package com.education.store.module.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.store.module.student.entity.EduStudent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 在学学员 Mapper
 */
@Mapper
public interface EduStudentMapper extends BaseMapper<EduStudent> {

    /** 检查学员是否有活跃的班级关联（删除保护） */
    @Select("SELECT COUNT(1) FROM edu_class_student WHERE student_id = #{studentId} AND status = 'ENROLLED' AND deleted = 0")
    int countActiveClassEnrollments(@Param("studentId") Long studentId);

    /** 检查学员是否所有班级关联均已结束（COMPLETED 条件） */
    @Select("SELECT COUNT(1) FROM edu_class_student WHERE student_id = #{studentId} AND status != 'EXITED' AND deleted = 0")
    int countNonExitedEnrollments(@Param("studentId") Long studentId);
}
