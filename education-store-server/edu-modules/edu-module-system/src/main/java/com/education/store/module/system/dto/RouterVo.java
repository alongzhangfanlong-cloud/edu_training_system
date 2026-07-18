package com.education.store.module.system.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * 前端路由配置 VO
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RouterVo {

    /** 路由名称 */
    private String name;

    /** 路由路径 */
    private String path;

    /** 是否隐藏 */
    private Boolean hidden;

    /** 重定向 */
    private String redirect;

    /** 组件路径 */
    private String component;

    /** 路由参数 */
    private String query;

    /** 是否始终显示根路由 */
    private Boolean alwaysShow;

    /** 路由元数据 */
    private MetaVo meta;

    /** 子路由 */
    private List<RouterVo> children;
}
