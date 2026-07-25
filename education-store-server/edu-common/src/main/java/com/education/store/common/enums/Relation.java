package com.education.store.common.enums;

/**
 * 亲属关系枚举
 */
public enum Relation {
    SELF("本人"),
    FATHER("父亲"),
    MOTHER("母亲"),
    GRANDFATHER("爷爷"),
    GRANDMOTHER("奶奶"),
    GRANDFATHER_M("外公"),
    GRANDMOTHER_M("外婆"),
    OTHER("其他");

    private final String label;

    Relation(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
