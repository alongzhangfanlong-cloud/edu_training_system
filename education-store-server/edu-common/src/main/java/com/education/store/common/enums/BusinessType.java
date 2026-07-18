package com.education.store.common.enums;

/**
 * 业务操作类型
 */
public enum BusinessType {
    INSERT("新增"),
    UPDATE("修改"),
    DELETE("删除"),
    GRANT("授权"),
    EXPORT("导出"),
    IMPORT("导入"),
    FORCE("强退"),
    CLEAN("清空"),
    OTHER("其他");

    private final String label;

    BusinessType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
