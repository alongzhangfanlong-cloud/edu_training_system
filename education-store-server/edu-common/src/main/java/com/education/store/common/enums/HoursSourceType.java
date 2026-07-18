package com.education.store.common.enums;

/**
 * 课时变动来源类型枚举
 */
public enum HoursSourceType {
    ENROLLMENT("报名"),
    TUITION("缴费"),
    ATTENDANCE("考勤"),
    LEAVE("请假"),
    GIFT("赠送"),
    MANUAL("手动");

    private final String label;

    HoursSourceType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
