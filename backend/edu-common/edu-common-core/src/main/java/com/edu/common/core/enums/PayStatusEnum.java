package com.edu.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PayStatusEnum {
    UNPAID(0, "未支付"),
    PAID(1, "已支付"),
    PARTIAL_REFUND(2, "部分退款"),
    FULL_REFUND(3, "全额退款");

    private final int code;
    private final String desc;
}
