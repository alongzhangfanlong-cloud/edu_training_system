package com.education.store.common.enums;

/**
 * 性别枚举
 */
public enum Gender {
    M("男"),
    F("女"),
    U("未知");

    private final String label;

    Gender(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
