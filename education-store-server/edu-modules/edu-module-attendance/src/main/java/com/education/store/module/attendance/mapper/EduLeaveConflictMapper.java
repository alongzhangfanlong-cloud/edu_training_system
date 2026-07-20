package com.education.store.module.attendance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.store.module.attendance.entity.EduLeaveConflict;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface EduLeaveConflictMapper extends BaseMapper<EduLeaveConflict> {

    /** 查询冲突详情（JOIN 排课表 + 课程表 + 班级表），字段使用驼峰命名供前端直接使用 */
    @Select("SELECT c.id, c.leave_id AS leaveId, c.schedule_id AS scheduleId, c.resolve_type AS resolveType, " +
            "c.substitute_teacher_id AS substituteTeacherId, c.new_schedule_date AS newScheduleDate, " +
            "c.new_time_slot AS newTimeSlot, c.new_classroom_id AS newClassroomId, c.status, " +
            "s.schedule_date AS scheduleDate, s.start_time AS startTime, s.end_time AS endTime, " +
            "CONCAT(s.start_time, '-', s.end_time) AS scheduleTime, " +
            "s.course_id AS courseId, s.class_id AS classId, s.teacher_id AS teacherId, " +
            "s.schedule_type AS scheduleType, s.status AS scheduleStatus, " +
            "co.name AS courseName, cl.name AS className, " +
            "(SELECT COUNT(1) FROM edu_class_student cs WHERE cs.class_id = s.class_id AND cs.deleted = 0) AS studentCount " +
            "FROM edu_leave_conflict c " +
            "LEFT JOIN edu_schedule s ON c.schedule_id = s.id AND s.deleted = 0 " +
            "LEFT JOIN edu_course co ON s.course_id = co.id " +
            "LEFT JOIN edu_class cl ON s.class_id = cl.id " +
            "WHERE c.leave_id = #{leaveId} AND c.deleted = 0 " +
            "ORDER BY s.start_time")
    List<Map<String, Object>> selectDetailsByLeaveId(@Param("leaveId") Long leaveId);
}
