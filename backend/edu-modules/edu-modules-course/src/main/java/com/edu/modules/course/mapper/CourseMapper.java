package com.edu.modules.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.modules.course.entity.CourseEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CourseMapper extends BaseMapper<CourseEntity> {
}
