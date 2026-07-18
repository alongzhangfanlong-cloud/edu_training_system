package com.education.store.module.system.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户视图VO
 */
@Getter
@Setter
public class UserVO {

    private Long id;
    private String username;
    private String realName;
    private String avatar;
    private String email;
    private String mobile;
    private Integer gender;
    private Integer status;
    private LocalDateTime createTime;
    private List<String> roles;
    private List<Long> roleIds;
}
