package com.education.store.module.system.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

/**
 * 登录响应VO
 */
@Getter
@Setter
public class LoginVO {

    private String accessToken;
    private String refreshToken;
    private Long userId;
    private String username;
    private String realName;
    private String avatar;
    private Set<String> permissions;
    private List<String> roles;
}
