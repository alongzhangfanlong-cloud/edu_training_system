package com.edu.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderTypeEnum {
    NEW(1, "新报"),
    RENEW(2, "续费"),
    EXPAND(3, "扩科"),
    SUPPLEMENT(4, "补费");

    private final int code;
    private final String desc;
}
