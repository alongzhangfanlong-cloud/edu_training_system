package com.education.store.common.enums;

/**
 * 年级枚举
 */
public enum Grade {
    PRESCHOOL("学前"),
    GRADE_1("小学一年级"),
    GRADE_2("小学二年级"),
    GRADE_3("小学三年级"),
    GRADE_4("小学四年级"),
    GRADE_5("小学五年级"),
    GRADE_6("小学六年级"),
    GRADE_7("初中一年级"),
    GRADE_8("初中二年级"),
    GRADE_9("初中三年级"),
    GRADE_10("高中一年级"),
    GRADE_11("高中二年级"),
    GRADE_12("高中三年级"),
    OTHER("其他");

    private final String label;

    Grade(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
