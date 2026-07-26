package com.education.store.module.tuition.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 收费新增 DTO
 */
@Data
public class FeeCreateDTO {

    @NotNull(message = "学员ID不能为空")
    private Long studentId;

    private Long enrollmentId;

    private Long courseId;

    private Long classId;

    private String feeType;

    @NotNull(message = "应收金额不能为空")
    @DecimalMin(value = "0.01", message = "应收金额必须大于0")
    private BigDecimal amountReceivable;

    @NotNull(message = "实缴金额不能为空")
    @DecimalMin(value = "0.00", message = "实缴金额不能为负数")
    private BigDecimal amountPaid;

    private String paymentMethod;

    private LocalDateTime payTime;

    private String remark;

    private Long operatorId;
}
