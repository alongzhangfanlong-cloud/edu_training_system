package com.edu.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ItemTypeEnum {
    HOUR_PACKAGE(1, "课时包"),
    BY_TERM(2, "按期"),
    BY_YEAR(3, "按年"),
    CARD(4, "次卡");

    private final int code;
    private final String desc;
}
