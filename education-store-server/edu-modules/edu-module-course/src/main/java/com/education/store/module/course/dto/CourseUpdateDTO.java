package com.education.store.module.course.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 课程更新 DTO
 */
@Data
public class CourseUpdateDTO {

    @NotNull(message = "课程ID不能为空")
    private Long id;

    private String name;

    private String code;

    private Long subjectId;

    private String courseType;

    private BigDecimal price;

    private String applicableGrade;

    private String coverImage;

    private Integer status;

    private Integer sortOrder;

    private String description;
}
