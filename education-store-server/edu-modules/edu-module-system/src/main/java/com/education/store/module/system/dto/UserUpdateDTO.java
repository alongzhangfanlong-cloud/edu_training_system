package com.education.store.module.system.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 用户修改 DTO — 与 CreateDTO 对齐，不含 username/password
 */
@Data
public class UserUpdateDTO {

    @NotNull(message = "用户ID不能为空")
    private Long id;

    private String realName;

    private String nickName;

    private String email;

    private String mobile;

    private Integer gender;

    /** 角色ID列表 */
    private List<Long> roleIds;

    private String remark;
}
