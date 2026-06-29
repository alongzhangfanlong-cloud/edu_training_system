package com.edu.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HourTransactionTypeEnum {
    ENROLLMENT(1, "报名增加"),
    CONSUME(2, "上课消耗"),
    LEAVE_RETURN(3, "请假返还"),
    MAKEUP_CONSUME(4, "补课消耗"),
    GIFT(5, "赠课"),
    REFUND(6, "退课");

    private final int code;
    private final String desc;

    public static String getDesc(Integer code) {
        if (code == null) return "";
        for (HourTransactionTypeEnum e : values()) {
            if (e.code == code) return e.desc;
        }
        return "";
    }
}
