package com.education.store.module.system.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色详情 VO
 */
@Data
public class RoleVO {

    private Long id;
    private String name;
    private String code;
    private Integer sort;
    private Integer menuCheckStrictly;
    private Integer status;

    /** 菜单ID列表 */
    private List<Long> menuIds;

    private String remark;
    private LocalDateTime createTime;
}
