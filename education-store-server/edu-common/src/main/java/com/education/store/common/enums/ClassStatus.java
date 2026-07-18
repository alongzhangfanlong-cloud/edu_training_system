package com.education.store.common.enums;

/**
 * 班级状态枚举
 */
public enum ClassStatus {
    RECRUITING("招生中"),
    FULL("已满"),
    SUSPENDED("停招"),
    FINISHED("结课"),
    CANCELLED("已取消");

    private final String label;

    ClassStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
