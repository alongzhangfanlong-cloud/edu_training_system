package com.education.store.module.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 用户DTO
 */
@Getter
@Setter
public class UserDTO {

    private Long id;

    @NotBlank(message = "用户名不能为空")
    private String username;

    /** 密码(新增时必填，编辑时选填) */
    private String password;

    private String realName;
    private String avatar;
    private String email;
    private String mobile;
    private Integer gender;
    private Integer status;

    /** 角色ID列表 */
    private List<Long> roleIds;
}
