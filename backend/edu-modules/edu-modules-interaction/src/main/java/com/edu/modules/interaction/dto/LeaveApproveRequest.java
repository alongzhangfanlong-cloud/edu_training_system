package com.edu.modules.interaction.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LeaveApproveRequest {
    @NotNull(message = "审批结果不能为空")
    private Boolean approved;
    private String remark;
}
