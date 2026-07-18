package com.education.store.module.system.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 角色修改 DTO — 与 CreateDTO 对齐，不含 code
 */
@Data
public class RoleUpdateDTO {

    @NotNull(message = "角色ID不能为空")
    private Long id;

    private String name;

    private Integer sort;

    /** 菜单树父子联动（1是 0否） */
    private Integer menuCheckStrictly;

    private Integer status;

    /** 菜单ID列表 */
    private List<Long> menuIds;

    private String remark;
}
