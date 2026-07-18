package com.education.store.common.enums;

/**
 * 课件资源类型枚举
 */
public enum ResourceType {
    DOCUMENT("课件文档"),
    PPT("教学PPT"),
    EXERCISE("练习资料"),
    OTHER("其他");

    private final String label;

    ResourceType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
