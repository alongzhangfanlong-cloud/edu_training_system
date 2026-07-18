package com.education.store.module.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.store.module.course.entity.EduClassroom;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 教室 Mapper
 */
@Mapper
public interface EduClassroomMapper extends BaseMapper<EduClassroom> {

    /** 统计教室被排课引用的数量（用于删除前引用保护） */
    @Select("SELECT COUNT(1) FROM edu_schedule WHERE room_id = #{classroomId} AND deleted = 0")
    int countReferencedBySchedules(@Param("classroomId") Long classroomId);
}
