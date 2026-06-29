package com.edu.modules.consume.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("hour_transactions")
public class HourTransactionEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private Long studentClassId;
    private Integer type;
    private BigDecimal changeHours;
    private BigDecimal beforeHours;
    private BigDecimal afterHours;
    private Long scheduleId;
    private Long attendanceId;
    private Long orderId;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
