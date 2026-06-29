package com.edu.modules.course.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ClassDTO {
    private Long id;
    @NotNull(message = "课程ID不能为空")
    private Long courseId;
    private String courseName;
    @NotNull(message = "教师ID不能为空")
    private Long teacherId;
    private String teacherName;
    private Long assistantId;
    private Long classroomId;
    private String classroomName;
    @NotBlank(message = "班级名称不能为空")
    private String name;
    private Integer maxStudents;
    private Integer currentStudents;
    private String scheduleRule;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer status;
}
