package com.education.store.module.attendance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.store.module.attendance.entity.EduAttendance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface EduAttendanceMapper extends BaseMapper<EduAttendance> {

    /** 查询某学生在某天有排课的排课ID和班级ID（JOIN班级学员表） */
    @Select("SELECT DISTINCT s.id AS scheduleId, s.class_id AS classId FROM edu_schedule s " +
            "INNER JOIN edu_class_student cs ON s.class_id = cs.class_id AND cs.deleted = 0 " +
            "WHERE cs.student_id = #{studentId} AND s.schedule_date = #{date} " +
            "AND s.status IN ('NORMAL','MAKEUP') AND s.deleted = 0")
    List<Map<String, Object>> findSchedulesForStudent(@Param("studentId") Long studentId, @Param("date") LocalDate date);
}
