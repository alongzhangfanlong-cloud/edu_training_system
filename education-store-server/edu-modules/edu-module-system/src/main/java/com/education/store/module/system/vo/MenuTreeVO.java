package com.education.store.module.system.vo;

import lombok.Data;

import java.util.List;

/**
 * 菜单树 VO
 */
@Data
public class MenuTreeVO {

    private Long id;
    private Long parentId;
    private String name;
    private String type;
    private String path;
    private String component;
    private String routeName;
    private String query;
    private Integer isFrame;
    private Integer isCache;
    private String icon;
    private String permission;
    private Integer sort;
    private Integer visible;
    private Integer status;

    /** 子菜单 */
    private List<MenuTreeVO> children;
}
