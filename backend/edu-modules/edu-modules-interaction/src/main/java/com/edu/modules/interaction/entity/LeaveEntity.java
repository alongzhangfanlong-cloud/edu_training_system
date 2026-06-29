package com.edu.modules.interaction.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("leaves")
public class LeaveEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private Long scheduleId;
    private Integer leaveType; // 1-病假 2-事假 3-其他
    private String reason;
    private Integer status; // 0-待审批 1-已通过 2-已拒绝
    private String approveRemark;
    private Long campusId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
