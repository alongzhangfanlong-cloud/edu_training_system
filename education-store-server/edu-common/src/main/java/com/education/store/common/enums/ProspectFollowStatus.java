package com.education.store.common.enums;

/**
 * 意向学员跟进状态枚举
 */
public enum ProspectFollowStatus {
    NEW("新线索"),
    CONTACTED("已联系"),
    TRIAL("已试听"),
    CONVERTED("已转化"),
    LOST("已流失");

    private final String label;

    ProspectFollowStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
