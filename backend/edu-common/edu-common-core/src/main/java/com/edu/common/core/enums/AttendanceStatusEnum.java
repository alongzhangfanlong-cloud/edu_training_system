package com.edu.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 考勤状态枚举
 */
@Getter
@AllArgsConstructor
public enum AttendanceStatusEnum {
    PRESENT(1, "出勤"),
    LEAVE(2, "请假"),
    ABSENT(3, "旷课"),
    MAKEUP(4, "补课");

    private final int code;
    private final String desc;

    public static String getDesc(Integer code) {
        if (code == null) return "";
        for (AttendanceStatusEnum e : values()) {
            if (e.code == code) return e.desc;
        }
        return "";
    }
}
