package com.education.store.module.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.store.module.student.entity.EduStudent;
import java.util.List;
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

    /** 根据班级ID查询学员列表（用于作业通知等场景） */
    @Select("SELECT es.* FROM edu_student es INNER JOIN edu_class_student ecs ON es.id = ecs.student_id WHERE ecs.class_id = #{classId} AND ecs.status = 'ENROLLED' AND es.deleted = 0 AND ecs.deleted = 0")
    List<EduStudent> selectByClassId(@Param("classId") Long classId);

    /** 根据顾问ID查询顾问姓名（跨 sys_user 表，避免模块循环依赖） */
    @Select("SELECT real_name FROM sys_user WHERE id = #{counselorId}")
    String selectCounselorName(@Param("counselorId") Long counselorId);

    /** 根据课程ID查询课程名称（跨 edu_course 表，避免模块循环依赖） */
    @Select("SELECT name FROM edu_course WHERE id = #{courseId}")
    String selectCourseName(@Param("courseId") Long courseId);
}
