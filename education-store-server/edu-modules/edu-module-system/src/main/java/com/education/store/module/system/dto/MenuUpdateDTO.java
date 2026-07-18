package com.education.store.module.system.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 菜单修改 DTO — 与 CreateDTO 对齐，不含 type
 */
@Data
public class MenuUpdateDTO {

    @NotNull(message = "菜单ID不能为空")
    private Long id;

    /** 父菜单ID */
    private Long parentId;

    private String name;

    /** 路由路径 */
    private String path;

    /** 组件路径 */
    private String component;

    /** 路由名称 */
    private String routeName;

    /** 路由参数 */
    private String query;

    /** 是否外链 */
    private Integer isFrame;

    /** 是否缓存 */
    private Integer isCache;

    /** 图标 */
    private String icon;

    /** 权限标识 */
    private String permission;

    /** 排序 */
    private Integer sort;

    /** 是否可见 */
    private Integer visible;

    /** 状态 */
    private Integer status;
}
