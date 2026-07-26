package com.education.store.module.tuition.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 收费更新 DTO
 */
@Data
public class FeeUpdateDTO {

    @NotNull(message = "收费记录ID不能为空")
    private Long id;

    private Long enrollmentId;

    private Long studentId;

    private Long courseId;

    private Long classId;

    private String feeType;

    @DecimalMin(value = "0.01", message = "应收金额必须大于0")
    private BigDecimal amountReceivable;

    @DecimalMin(value = "0.00", message = "实缴金额不能为负数")
    private BigDecimal amountPaid;

    private String paymentMethod;

    private LocalDateTime payTime;

    private String remark;
}
