package com.edu.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LeaveTypeEnum {
    SICK(1, "病假"),
    PERSONAL(2, "事假"),
    OTHER(3, "其他");

    private final int code;
    private final String desc;
}
