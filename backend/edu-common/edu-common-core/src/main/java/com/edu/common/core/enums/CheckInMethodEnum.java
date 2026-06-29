package com.edu.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CheckInMethodEnum {
    QR_SCAN(1, "扫码签到"),
    TEACHER_CALL(2, "教师点名"),
    FACE(3, "人脸识别"),
    BLUETOOTH(4, "蓝牙签到"),
    MANUAL(5, "手动补签");

    private final int code;
    private final String desc;
}
