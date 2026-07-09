package com.edu.modules.interaction.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HomeworkRequest {
    private Long scheduleId;
    private Long classId;
    private Long teacherId;
    @NotBlank(message = "作业内容不能为空")
    private String content;
    private String images;
}
