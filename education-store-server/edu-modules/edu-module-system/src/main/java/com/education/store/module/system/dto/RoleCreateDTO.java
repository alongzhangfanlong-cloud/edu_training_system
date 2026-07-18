package com.education.store.module.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 角色新增 DTO
 */
@Data
public class RoleCreateDTO {

    @NotBlank(message = "角色名称不能为空")
    @Size(max = 64)
    private String name;

    @NotBlank(message = "角色编码不能为空")
    @Size(max = 64)
    private String code;

    private Integer sort;

    /** 菜单树父子联动（1是 0否） */
    private Integer menuCheckStrictly;

    /** 状态(1正常 0停用)，默认正常 */
    private Integer status = 1;

    /** 菜单ID列表 */
    private List<Long> menuIds;

    private String remark;
}
