package com.education.store.common.enums;

/**
 * 意向等级枚举
 */
public enum InterestLevel {
    HIGH("高"),
    MEDIUM("中"),
    LOW("低");

    private final String label;

    InterestLevel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
