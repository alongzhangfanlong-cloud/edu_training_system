package com.education.store.common.enums;

/**
 * 积分来源类型枚举
 */
public enum PointsSourceType {
    TEACHER("教师发放"),
    ADMIN("管理员操作"),
    REDEEM("兑换消耗"),
    CANCEL_REDEEM("取消兑换退回"),
    ADJUST("手动调整");

    private final String label;

    PointsSourceType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
