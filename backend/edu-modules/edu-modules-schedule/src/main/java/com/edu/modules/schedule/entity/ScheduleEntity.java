package com.edu.modules.schedule.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@TableName("schedules")
public class ScheduleEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long classId;
    private Long courseId;
    private Long teacherId;
    private Long classroomId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer lessonNo;
    private Integer status;
    private Long originalScheduleId;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
