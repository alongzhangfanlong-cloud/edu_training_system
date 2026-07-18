package com.education.store.common.enums;

/**
 * 积分变动类型枚举
 */
public enum PointsChangeType {
    EARN("获取积分"),
    REDEEM("兑换消耗"),
    ADJUST("管理员调整"),
    EXPIRE("积分过期");

    private final String label;

    PointsChangeType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
