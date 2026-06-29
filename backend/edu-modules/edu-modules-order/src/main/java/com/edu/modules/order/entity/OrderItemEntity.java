package com.edu.modules.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("order_items")
public class OrderItemEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private Long courseId;
    private Long classId;
    private Integer itemType;
    private BigDecimal hours;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private BigDecimal giftHours;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
