package com.education.store.module.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 菜单新增 DTO
 */
@Data
public class MenuCreateDTO {

    /** 父菜单ID（0=根节点） */
    private Long parentId;

    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 64)
    private String name;

    /** 类型（M目录 C菜单 F按钮） */
    @NotBlank(message = "菜单类型不能为空")
    private String type;

    /** 路由路径 */
    private String path;

    /** 组件路径 */
    private String component;

    /** 路由名称 */
    private String routeName;

    /** 路由参数 */
    private String query;

    /** 是否外链（1是 0否） */
    private Integer isFrame;

    /** 是否缓存（1是 0否） */
    private Integer isCache;

    /** 图标 */
    private String icon;

    /** 权限标识 */
    private String permission;

    /** 排序 */
    private Integer sort;

    /** 是否可见（1可见 0隐藏） */
    private Integer visible;

    /** 状态（1正常 0停用） */
    private Integer status;
}
