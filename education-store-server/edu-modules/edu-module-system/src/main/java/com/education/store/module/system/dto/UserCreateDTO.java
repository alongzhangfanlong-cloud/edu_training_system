package com.education.store.module.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 用户新增 DTO — 一体化提交
 */
@Data
public class UserCreateDTO {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 64, message = "用户名长度为2-64个字符")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 128, message = "密码最少6位")
    private String password;

    private String realName;

    private String nickName;

    private String email;

    private String mobile;

    private Integer gender;

    /** 角色ID列表 */
    private List<Long> roleIds;

    private String remark;
}
