package com.edu.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 课表状态枚举
 */
@Getter
@AllArgsConstructor
public enum ScheduleStatusEnum {
    CANCELLED(0, "取消"),
    NORMAL(1, "正常"),
    ADJUSTED(2, "调课"),
    MAKEUP(3, "补课"),
    SUSPENDED(4, "停课");

    private final int code;
    private final String desc;

    public static String getDesc(Integer code) {
        if (code == null) return "";
        for (ScheduleStatusEnum e : values()) {
            if (e.code == code) return e.desc;
        }
        return "";
    }
}
