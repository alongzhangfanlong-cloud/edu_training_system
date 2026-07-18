package com.education.store.module.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.store.module.course.entity.EduCourse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 课程 Mapper
 */
@Mapper
public interface EduCourseMapper extends BaseMapper<EduCourse> {

    /** 检查课程是否被班级引用（删除保护） */
    @Select("SELECT COUNT(1) FROM edu_class WHERE course_id = #{courseId} AND deleted = 0")
    int countReferencedByClasses(@Param("courseId") Long courseId);

    /** 统计某课程下非终态班级数（ONE_ON_ONE 类型变更时校验） */
    @Select("SELECT COUNT(1) FROM edu_class WHERE course_id = #{courseId} AND status NOT IN ('FINISHED','CANCELLED') AND deleted = 0")
    int countActiveClasses(@Param("courseId") Long courseId);
}
