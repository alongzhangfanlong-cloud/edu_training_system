package com.education.store.module.schedule.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.store.module.schedule.entity.EduSchedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EduScheduleMapper extends BaseMapper<EduSchedule> {

    /** 三维冲突检测：查同教师同时段冲突 */
    @Select("SELECT COUNT(1) FROM edu_schedule WHERE teacher_id = #{teacherId} AND schedule_date = #{date} AND status IN ('NORMAL','MAKEUP') AND deleted = 0 AND start_time < #{endTime} AND end_time > #{startTime}")
    int countTeacherConflict(@Param("teacherId") Long teacherId, @Param("date") java.time.LocalDate date, @Param("startTime") java.time.LocalTime startTime, @Param("endTime") java.time.LocalTime endTime);

    /** 三维冲突检测：查同教室同时段冲突 */
    @Select("SELECT COUNT(1) FROM edu_schedule WHERE room_id = #{roomId} AND schedule_date = #{date} AND status IN ('NORMAL','MAKEUP') AND deleted = 0 AND start_time < #{endTime} AND end_time > #{startTime}")
    int countRoomConflict(@Param("roomId") Long roomId, @Param("date") java.time.LocalDate date, @Param("startTime") java.time.LocalTime startTime, @Param("endTime") java.time.LocalTime endTime);

    /** 三维冲突检测：查同班级同时段冲突 */
    @Select("SELECT COUNT(1) FROM edu_schedule WHERE class_id = #{classId} AND schedule_date = #{date} AND status IN ('NORMAL','MAKEUP') AND deleted = 0 AND start_time < #{endTime} AND end_time > #{startTime}")
    int countClassConflict(@Param("classId") Long classId, @Param("date") java.time.LocalDate date, @Param("startTime") java.time.LocalTime startTime, @Param("endTime") java.time.LocalTime endTime);
}
