package com.education.store.module.tuition.dto;

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
    private BigDecimal amountReceivable;

    private BigDecimal amountPaid;

    private String paymentMethod;

    private String status;

    private LocalDateTime payTime;

    private String remark;
}
