package com.edu.modules.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateOrderRequest {
    @NotNull private Long studentId;
    private Integer type; // 1-新报 2-续费 3-扩科
    @NotNull private List<OrderItem> items;
    private String discountCode;
    private String remark;

    @Data
    public static class OrderItem {
        @NotNull private Long courseId;
        private Long classId;
        private Integer itemType;
        private BigDecimal hours;
        private BigDecimal unitPrice;
        private BigDecimal giftHours;
    }
}
