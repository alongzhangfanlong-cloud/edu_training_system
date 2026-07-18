package com.education.store.common.enums;

/**
 * 跟进方式枚举
 */
public enum FollowUpMethod {
    PHONE("电话"),
    WECHAT("微信"),
    FACE_TO_FACE("面谈");

    private final String label;

    FollowUpMethod(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
