package com.education.store.module.classinfo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 班级更新 DTO
 */
@Data
public class ClassUpdateDTO {

    @NotNull(message = "班级ID不能为空")
    private Long id;

    private String name;

    private Long courseId;

    private Long teacherId;

    private Long classroomId;

    private Integer maxStudents;

    private String status;

    private LocalDate startDate;

    private LocalDate endDate;

    private String description;
}
