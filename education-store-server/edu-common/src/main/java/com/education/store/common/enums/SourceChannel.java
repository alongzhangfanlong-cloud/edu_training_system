package com.education.store.common.enums;

/**
 * 来源渠道枚举
 */
public enum SourceChannel {
    DIRECT_MARKETING("地推"),
    REFERRAL("转介绍"),
    ONLINE("线上"),
    OTHER("其他");

    private final String label;

    SourceChannel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
