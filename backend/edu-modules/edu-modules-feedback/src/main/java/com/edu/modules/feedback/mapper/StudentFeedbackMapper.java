package com.edu.modules.feedback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.modules.feedback.entity.StudentFeedbackEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StudentFeedbackMapper extends BaseMapper<StudentFeedbackEntity> {
}
