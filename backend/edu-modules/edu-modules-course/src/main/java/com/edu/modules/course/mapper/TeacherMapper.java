package com.edu.modules.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.modules.course.entity.TeacherEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TeacherMapper extends BaseMapper<TeacherEntity> {

    @Select("SELECT MAX(teacher_no) FROM teachers WHERE teacher_no REGEXP CONCAT('^TCH', #{year}, '[0-9]{5}$')")
    String selectMaxTeacherNo(String year);
}
