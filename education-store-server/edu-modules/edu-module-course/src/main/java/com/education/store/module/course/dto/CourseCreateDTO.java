package com.education.store.module.course.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 课程新增 DTO
 */
@Data
public class CourseCreateDTO {

    @NotBlank(message = "课程名称不能为空")
    private String name;

    @NotBlank(message = "课程编号不能为空")
    private String code;

    @NotNull(message = "所属科目不能为空")
    private Long subjectId;

    private String courseType;

    private BigDecimal price;

    private String applicableGrade;

    private String coverImage;

    private Integer status;

    private Integer sortOrder;

    private String description;
}
