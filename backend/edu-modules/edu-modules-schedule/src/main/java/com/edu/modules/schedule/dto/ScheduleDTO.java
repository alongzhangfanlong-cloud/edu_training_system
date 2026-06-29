package com.edu.modules.schedule.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ScheduleDTO {
    private Long id;
    private Long classId;
    private String className;
    private Long courseId;
    private String courseName;
    private Long teacherId;
    private String teacherName;
    private Long classroomId;
    private String classroomName;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer lessonNo;
    private Integer status;
    private Integer studentCount;
    private Integer presentCount;
    private String color;
    private String remark;
}
