package com.education.store.common.enums;

/**
 * 课时变动类型枚举
 */
public enum HoursChangeType {
    ENROLL_GAIN("报名获得"),
    CONSUME("消课扣减"),
    LEAVE_RETURN("请假返还"),
    MAKEUP_CONSUME("补课消耗"),
    REFUND_DEDUCT("退费扣减"),
    GIFT_GAIN("赠送获得"),
    MANUAL_ADJUST("手动调整");

    private final String label;

    HoursChangeType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
