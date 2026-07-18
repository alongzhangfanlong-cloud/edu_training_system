package com.education.store.common.enums;

/**
 * 学员状态枚举
 */
public enum StudentStatus {
    ENROLLED("在读"),
    SUSPENDED("停课"),
    COMPLETED("结业"),
    LOST("流失");

    private final String label;

    StudentStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
