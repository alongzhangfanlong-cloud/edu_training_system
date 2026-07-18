package com.education.store.module.tuition.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 退费驳回 DTO
 */
@Data
public class RefundRejectDTO {

    @NotNull(message = "审批人ID不能为空")
    private Long approverId;

    @NotBlank(message = "驳回原因不能为空")
    private String reason;
}
