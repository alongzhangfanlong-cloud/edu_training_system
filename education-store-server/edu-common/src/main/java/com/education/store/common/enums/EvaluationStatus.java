package com.education.store.common.enums;

/**
 * 评价状态枚举
 */
public enum EvaluationStatus {
    DRAFT("草稿"),
    SUBMITTED("已提交");

    private final String label;

    EvaluationStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
