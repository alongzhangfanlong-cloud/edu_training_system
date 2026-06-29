package com.edu.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 班级状态枚举
 */
@Getter
@AllArgsConstructor
public enum ClassStatusEnum {
    NOT_STARTED(0, "未开班"),
    IN_PROGRESS(1, "进行中"),
    FINISHED(2, "已结课");

    private final int code;
    private final String desc;

    public static String getDesc(Integer code) {
        if (code == null) return "";
        for (ClassStatusEnum e : values()) {
            if (e.code == code) return e.desc;
        }
        return "";
    }
}
