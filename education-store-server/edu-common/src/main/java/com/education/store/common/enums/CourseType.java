package com.education.store.common.enums;

/**
 * 课程类型枚举
 */
public enum CourseType {
    BATCH("班课"),
    ONE_ON_ONE("一对一"),
    SMALL_GROUP("小班(2-6人)");

    private final String label;

    CourseType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
