package com.edu.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TeacherStatusEnum {
    RESIGNED(0, "离职"),
    ACTIVE(1, "在职"),
    VACATION(2, "休假");

    private final int code;
    private final String desc;
}
