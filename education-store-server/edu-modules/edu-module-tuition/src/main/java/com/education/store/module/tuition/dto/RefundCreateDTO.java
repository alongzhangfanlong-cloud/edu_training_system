package com.education.store.module.tuition.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 退费新增 DTO — 仅暴露允许客户端提交的字段
 */
@Data
public class RefundCreateDTO {

    @NotNull(message = "学员ID不能为空")
    private Long studentId;

    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    private Long feeId;

    @NotNull(message = "退费金额不能为空")
    @DecimalMin(value = "0.01", message = "退费金额必须大于0")
    private BigDecimal amount;

    private String reason;

    private String reasonCustom;

    private String refundMethod;

    @Min(value = 0, message = "扣回课时不能为负数")
    private Integer hoursDeducted;

    private Long applicantId;

    private String remark;
}
