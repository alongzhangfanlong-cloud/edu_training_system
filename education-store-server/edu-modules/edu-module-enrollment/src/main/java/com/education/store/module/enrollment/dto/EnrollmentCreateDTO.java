package com.education.store.module.enrollment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 报名新增 DTO
 */
@Data
public class EnrollmentCreateDTO {

    @NotNull(message = "学员ID不能为空")
    private Long studentId;

    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    private Long classId;

    @NotNull(message = "报名金额不能为空")
    @DecimalMin(value = "0.01", message = "报名金额必须大于0")
    private BigDecimal amount;

    private Long handlerId;

    private LocalDate enrollmentDate;

    private String remark;
}
