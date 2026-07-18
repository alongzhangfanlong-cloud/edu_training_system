package com.education.store.module.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.store.module.course.entity.EduSubject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 科目 Mapper
 */
@Mapper
public interface EduSubjectMapper extends BaseMapper<EduSubject> {

    /** 检查科目是否被课程引用（删除保护） */
    @Select("SELECT COUNT(1) FROM edu_course WHERE subject_id = #{subjectId} AND deleted = 0")
    int countReferencedByCourses(@Param("subjectId") Long subjectId);
}
