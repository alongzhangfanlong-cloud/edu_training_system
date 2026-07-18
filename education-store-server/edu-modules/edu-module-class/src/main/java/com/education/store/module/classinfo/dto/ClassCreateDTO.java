package com.education.store.module.classinfo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 班级新增 DTO
 */
@Data
public class ClassCreateDTO {

    @NotBlank(message = "班级名称不能为空")
    private String name;

    @NotNull(message = "关联课程不能为空")
    private Long courseId;

    private Long teacherId;

    private Long classroomId;

    private Integer maxStudents;

    private String status;

    private LocalDate startDate;

    private LocalDate endDate;

    private String description;
}
