package com.edu.modules.interaction.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PointChangeRequest {
    @NotNull(message = "学员ID不能为空")
    private Long studentId;
    @NotNull(message = "变动数量不能为空")
    private Integer changeAmount;
    @NotBlank(message = "变动原因不能为空")
    private String reason;
}
