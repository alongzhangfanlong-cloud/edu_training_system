package com.edu.modules.course.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CourseDTO {
    private Long id;
    @NotBlank(message = "课程名称不能为空")
    private String name;
    private String code;
    @NotNull(message = "课程类型不能为空")
    private Integer type;
    private String subject;
    private String gradeRange;
    private Integer duration;
    private BigDecimal unitPrice;
    private String description;
    private String coverImage;
    private Integer status;
}
