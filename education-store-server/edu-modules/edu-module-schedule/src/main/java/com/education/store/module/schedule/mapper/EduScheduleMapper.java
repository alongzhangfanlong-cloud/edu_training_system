package com.education.store.module.schedule.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.store.module.schedule.entity.EduSchedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface EduScheduleMapper extends BaseMapper<EduSchedule> {

    /** 三维冲突检测：查同教师同时段冲突 */
    @Select("SELECT COUNT(1) FROM edu_schedule WHERE teacher_id = #{teacherId} AND schedule_date = #{date} AND status IN ('NORMAL','MAKEUP') AND deleted = 0 AND start_time < #{endTime} AND end_time > #{startTime}")
    int countTeacherConflict(@Param("teacherId") Long teacherId, @Param("date") LocalDate date, @Param("startTime") LocalTime startTime, @Param("endTime") LocalTime endTime);

    /** 三维冲突检测：查同教室同时段冲突 */
    @Select("SELECT COUNT(1) FROM edu_schedule WHERE room_id = #{roomId} AND schedule_date = #{date} AND status IN ('NORMAL','MAKEUP') AND deleted = 0 AND start_time < #{endTime} AND end_time > #{startTime}")
    int countRoomConflict(@Param("roomId") Long roomId, @Param("date") LocalDate date, @Param("startTime") LocalTime startTime, @Param("endTime") LocalTime endTime);

    /** 三维冲突检测：查同班级同时段冲突 */
    @Select("SELECT COUNT(1) FROM edu_schedule WHERE class_id = #{classId} AND schedule_date = #{date} AND status IN ('NORMAL','MAKEUP') AND deleted = 0 AND start_time < #{endTime} AND end_time > #{startTime}")
    int countClassConflict(@Param("classId") Long classId, @Param("date") LocalDate date, @Param("startTime") LocalTime startTime, @Param("endTime") LocalTime endTime);

    /** 三维冲突检测（排除自身）：查同教师同时段冲突 */
    @Select("SELECT COUNT(1) FROM edu_schedule WHERE teacher_id = #{teacherId} AND schedule_date = #{date} AND status IN ('NORMAL','MAKEUP') AND deleted = 0 AND start_time < #{endTime} AND end_time > #{startTime} AND id != #{excludeId}")
    int countTeacherConflictExclude(@Param("teacherId") Long teacherId, @Param("date") LocalDate date, @Param("startTime") LocalTime startTime, @Param("endTime") LocalTime endTime, @Param("excludeId") Long excludeId);

    /** 三维冲突检测（排除自身）：查同教室同时段冲突 */
    @Select("SELECT COUNT(1) FROM edu_schedule WHERE room_id = #{roomId} AND schedule_date = #{date} AND status IN ('NORMAL','MAKEUP') AND deleted = 0 AND start_time < #{endTime} AND end_time > #{startTime} AND id != #{excludeId}")
    int countRoomConflictExclude(@Param("roomId") Long roomId, @Param("date") LocalDate date, @Param("startTime") LocalTime startTime, @Param("endTime") LocalTime endTime, @Param("excludeId") Long excludeId);

    /** 三维冲突检测（排除自身）：查同班级同时段冲突 */
    @Select("SELECT COUNT(1) FROM edu_schedule WHERE class_id = #{classId} AND schedule_date = #{date} AND status IN ('NORMAL','MAKEUP') AND deleted = 0 AND start_time < #{endTime} AND end_time > #{startTime} AND id != #{excludeId}")
    int countClassConflictExclude(@Param("classId") Long classId, @Param("date") LocalDate date, @Param("startTime") LocalTime startTime, @Param("endTime") LocalTime endTime, @Param("excludeId") Long excludeId);

    /** 查询某排课对应的班级总在学人数 */
    @Select("SELECT COUNT(1) FROM edu_class_student WHERE class_id = (SELECT class_id FROM edu_schedule WHERE id = #{scheduleId}) AND status = 'ENROLLED' AND deleted = 0")
    int countClassEnrolledStudents(@Param("scheduleId") Long scheduleId);

    /** 查询某排课已完成考勤的人数（非null状态） */
    @Select("SELECT COUNT(1) FROM edu_attendance WHERE schedule_id = #{scheduleId} AND status IS NOT NULL AND deleted = 0")
    int countAttendedStudents(@Param("scheduleId") Long scheduleId);

    /** 原子更新消课状态 */
    @Update("UPDATE edu_schedule SET consumed = 1, consume_time = NOW(), consume_by = #{consumeBy} WHERE id = #{scheduleId} AND consumed = 0 AND deleted = 0")
    int markConsumed(@Param("scheduleId") Long scheduleId, @Param("consumeBy") String consumeBy);

    /**
     * 按月统计教师已消课排课数（按课程分组）
     * 返回字段：teacher_id, course_id, lesson_count
     */
    @Select("SELECT teacher_id AS teacherId, course_id AS courseId, COUNT(1) AS lessonCount " +
            "FROM edu_schedule " +
            "WHERE teacher_id = #{teacherId} " +
            "AND DATE_FORMAT(schedule_date, '%Y-%m') = #{month} " +
            "AND consumed = 1 " +
            "AND deleted = 0 " +
            "GROUP BY teacher_id, course_id")
    List<Map<String, Object>> countConsumedLessonsByTeacherAndMonth(@Param("teacherId") Long teacherId, @Param("month") String month);

    /**
     * 按月统计所有教师已消课排课数（按教师+课程分组）
     */
    @Select("SELECT teacher_id AS teacherId, course_id AS courseId, COUNT(1) AS lessonCount " +
            "FROM edu_schedule " +
            "WHERE DATE_FORMAT(schedule_date, '%Y-%m') = #{month} " +
            "AND consumed = 1 " +
            "AND deleted = 0 " +
            "GROUP BY teacher_id, course_id")
    List<Map<String, Object>> countAllConsumedLessonsByMonth(@Param("month") String month);
}
