package com.education.store.common.enums;

/**
 * 附件类型枚举
 */
public enum AttachmentType {
    IMAGE("图片"),
    DOCUMENT("文档");

    private final String label;

    AttachmentType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
