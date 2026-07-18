package com.education.store.common.enums;

/**
 * 兑换记录状态枚举
 */
public enum RedemptionStatus {
    PENDING_CLAIM("待领取"),
    CLAIMED("已领取"),
    CANCELLED("已取消");

    private final String label;

    RedemptionStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
