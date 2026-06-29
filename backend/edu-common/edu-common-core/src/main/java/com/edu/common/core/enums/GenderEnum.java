package com.edu.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 性别枚举
 */
@Getter
@AllArgsConstructor
public enum GenderEnum {
    UNKNOWN(0, "未知"),
    MALE(1, "男"),
    FEMALE(2, "女");

    private final int code;
    private final String desc;

    public static String getDesc(Integer code) {
        if (code == null) return UNKNOWN.desc;
        for (GenderEnum e : values()) {
            if (e.code == code) return e.desc;
        }
        return UNKNOWN.desc;
    }
}
