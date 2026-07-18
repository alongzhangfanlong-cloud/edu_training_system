package com.education.store.common.enums;

/**
 * 班级学员状态枚举
 */
public enum ClassStudentStatus {
    ENROLLED("在读"),
    SUSPENDED("停课"),
    EXITED("已退出");

    private final String label;

    ClassStudentStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
