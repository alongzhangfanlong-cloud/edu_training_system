package com.education.store.module.system.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 路由元数据 VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MetaVo {

    /** 页面标题 */
    private String title;

    /** 图标 */
    private String icon;

    /** 是否缓存 */
    private Boolean noCache;

    /** 外链地址 */
    private String link;
}
