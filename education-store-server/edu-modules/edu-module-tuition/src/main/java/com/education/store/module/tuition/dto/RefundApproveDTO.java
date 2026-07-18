package com.education.store.module.tuition.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 退费审批通过 DTO
 */
@Data
public class RefundApproveDTO {

    @NotNull(message = "审批人ID不能为空")
    private Long approverId;
}
