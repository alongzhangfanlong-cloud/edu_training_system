package com.edu.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PayMethodEnum {
    WECHAT(1, "微信支付"),
    ALIPAY(2, "支付宝"),
    OFFLINE(3, "线下"),
    BALANCE(4, "余额");

    private final int code;
    private final String desc;
}
