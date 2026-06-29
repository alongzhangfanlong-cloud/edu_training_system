package com.edu.modules.interaction.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GrowthRecordRequest {
    @NotNull(message = "学员ID不能为空")
    private Long studentId;
    private Long teacherId;
    @NotNull(message = "评语不能为空")
    private String content;
    @Min(1) @Max(5)
    private Integer score;
}
