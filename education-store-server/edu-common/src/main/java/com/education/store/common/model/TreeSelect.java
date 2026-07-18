package com.education.store.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * 树形选择器模型
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TreeSelect {

    /** 节点ID */
    private Long id;

    /** 节点名称 */
    private String label;

    /** 父节点ID */
    private Long parentId;

    /** 权重(排序) */
    private Integer weight;

    /** 是否禁用 */
    private Boolean disabled;

    /** 子节点 */
    private List<TreeSelect> children;

    public TreeSelect() {}

    public TreeSelect(Long id, String label) {
        this.id = id;
        this.label = label;
    }

    public TreeSelect(Long id, String label, Long parentId) {
        this.id = id;
        this.label = label;
        this.parentId = parentId;
    }
}
