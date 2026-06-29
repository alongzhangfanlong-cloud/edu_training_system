package com.edu.modules.consume.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.modules.consume.entity.StudentClassEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface StudentClassMapper extends BaseMapper<StudentClassEntity> {

    @Select("SELECT * FROM student_classes WHERE student_id = #{studentId} AND class_id = #{classId} AND status = 1 FOR UPDATE")
    StudentClassEntity selectForUpdate(@Param("studentId") Long studentId, @Param("classId") Long classId);
}
