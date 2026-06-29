package com.edu.modules.interaction.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LeaveDTO {
    private Long id;
    private Long studentId;
    private Long scheduleId;
    private Integer leaveType;
    private String reason;
    private Integer status;
    private String approveRemark;
    private LocalDateTime createdAt;
    // joined
    private String studentName;
    private String courseName;
}
