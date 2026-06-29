package com.edu.modules.schedule.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.modules.schedule.entity.ScheduleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ScheduleMapper extends BaseMapper<ScheduleEntity> {

    @Select("SELECT DISTINCT s.* FROM schedules s " +
            "INNER JOIN student_classes sc ON s.class_id = sc.class_id " +
            "WHERE sc.student_id = #{studentId} " +
            "AND s.date >= #{startDate} AND s.date <= #{endDate} " +
            "ORDER BY s.date, s.start_time")
    List<ScheduleEntity> findByStudentId(@Param("studentId") Long studentId,
                                          @Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);
}
