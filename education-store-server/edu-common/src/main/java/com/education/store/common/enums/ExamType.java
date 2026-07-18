package com.education.store.common.enums;

/**
 * 考试类型枚举
 */
public enum ExamType {
    MONTHLY("月考"),
    MIDTERM("期中"),
    FINAL("期末"),
    QUIZ("随堂测验"),
    CUSTOM("自定义");

    private final String label;

    ExamType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
