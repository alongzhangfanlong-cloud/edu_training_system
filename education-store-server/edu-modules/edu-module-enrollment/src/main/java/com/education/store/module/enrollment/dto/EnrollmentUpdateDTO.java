package com.education.store.module.enrollment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 报名更新 DTO — 仅暴露允许客户端修改的字段，防止字段注入
 */
@Data
public class EnrollmentUpdateDTO {

    @NotNull(message = "ID不能为空")
    private Long id;

    private Long classId;

    @DecimalMin(value = "0.01", message = "报名金额必须大于0")
    private BigDecimal amount;

    private Long handlerId;

    private LocalDate enrollmentDate;

    private String remark;
}
