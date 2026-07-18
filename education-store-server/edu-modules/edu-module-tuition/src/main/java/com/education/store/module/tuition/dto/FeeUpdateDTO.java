package com.education.store.module.tuition.dto;

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

    private BigDecimal amountReceivable;

    private BigDecimal amountPaid;

    private String paymentMethod;

    private String status;

    private LocalDateTime payTime;

    private String remark;
}
