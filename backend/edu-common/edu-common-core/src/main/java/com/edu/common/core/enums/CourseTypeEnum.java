package com.edu.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 课程类型枚举
 */
@Getter
@AllArgsConstructor
public enum CourseTypeEnum {
    GROUP(1, "班课"),
    ONE_ON_ONE(2, "一对一"),
    SMALL_GROUP(3, "小班");

    private final int code;
    private final String desc;

    public static String getDesc(Integer code) {
        if (code == null) return "";
        for (CourseTypeEnum e : values()) {
            if (e.code == code) return e.desc;
        }
        return "";
    }
}
