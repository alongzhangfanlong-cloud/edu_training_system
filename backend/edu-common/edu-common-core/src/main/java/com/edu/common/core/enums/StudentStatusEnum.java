package com.edu.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 学员状态枚举
 */
@Getter
@AllArgsConstructor
public enum StudentStatusEnum {
    INTENT(0, "意向"),
    ACTIVE(1, "在读"),
    SUSPENDED(2, "停课"),
    COMPLETED(3, "结课"),
    LOST(4, "流失");

    private final int code;
    private final String desc;

    public static String getDesc(Integer code) {
        if (code == null) return "";
        for (StudentStatusEnum e : values()) {
            if (e.code == code) return e.desc;
        }
        return "";
    }
}
