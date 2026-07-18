package com.education.store.module.teaching.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.store.module.teaching.entity.EduHomeworkAttachment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EduHomeworkAttachmentMapper extends BaseMapper<EduHomeworkAttachment> {

    @Select("SELECT * FROM edu_homework_attachment WHERE homework_id = #{homeworkId} ORDER BY sort_order")
    List<EduHomeworkAttachment> selectByHomeworkId(@Param("homeworkId") Long homeworkId);
}
