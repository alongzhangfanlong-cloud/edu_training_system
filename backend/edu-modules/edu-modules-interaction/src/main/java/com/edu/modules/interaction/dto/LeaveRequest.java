package com.edu.modules.interaction.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LeaveRequest {
    @NotNull(message = "学员ID不能为空")
    private Long studentId;
    private Long scheduleId;
    @NotNull(message = "请假类型不能为空")
    private Integer leaveType; // 1-病假 2-事假 3-其他
    private String reason;
}
