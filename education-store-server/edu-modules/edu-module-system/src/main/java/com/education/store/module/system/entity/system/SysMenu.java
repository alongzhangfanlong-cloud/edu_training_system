package com.education.store.module.system.entity.system;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.education.store.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 菜单实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
public class SysMenu extends BaseEntity {

    /** 父菜单ID */
    private Long parentId;

    /** 菜单名称 */
    private String name;

    /** 类型(M目录 C菜单 F按钮) */
    private String type;

    /** 路由路径 */
    private String path;

    /** 组件路径 */
    private String component;

    /** 路由名称 */
    private String routeName;

    /** 路由参数 */
    private String query;

    /** 是否外链(1是 0否) */
    private Integer isFrame;

    /** 是否缓存(1是 0否) */
    private Integer isCache;

    /** 图标 */
    private String icon;

    /** 权限标识 */
    private String permission;

    /** 排序 */
    private Integer sort;

    /** 是否可见(1可见 0隐藏) */
    private Integer visible;

    /** 状态(1正常 0停用) */
    private Integer status;

    /** 创建者 */
    private String createBy;

    /** 更新者 */
    private String updateBy;

    /** 子菜单(非数据库字段) */
    @TableField(exist = false)
    private List<SysMenu> children;

    /** 父菜单名称(非数据库字段) */
    @TableField(exist = false)
    private String parentName;
}
