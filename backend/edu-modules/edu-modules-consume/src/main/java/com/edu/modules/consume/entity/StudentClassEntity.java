package com.edu.modules.consume.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("student_classes")
public class StudentClassEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private Long classId;
    private Long orderId;
    private BigDecimal totalHours;
    private BigDecimal usedHours;
    private BigDecimal remainHours;
    private BigDecimal giftHours;
    private LocalDate expireDate;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
