package com.education.store.common.enums;

/**
 * 家长查阅记录业务类型枚举
 */
public enum BizType {
    EVALUATION("课堂评价"),
    EXAM("考试成绩"),
    HOMEWORK("课后作业");

    private final String label;

    BizType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
