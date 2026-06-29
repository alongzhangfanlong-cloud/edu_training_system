package com.edu.modules.interaction.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("point_transactions")
public class PointTransactionEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private Integer changeAmount;
    private String reason;
    private Long campusId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
