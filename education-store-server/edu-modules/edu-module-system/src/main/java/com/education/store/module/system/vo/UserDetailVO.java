package com.education.store.module.system.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户详情 VO
 */
@Data
public class UserDetailVO {

    private Long id;
    private String username;
    private String realName;
    private String nickName;
    private String email;
    private String mobile;
    private Integer gender;
    private String avatar;
    private Integer status;

    /** 角色列表 */
    private List<RoleSimple> roles;

    /** 角色ID列表 */
    private List<Long> roleIds;

    private String remark;
    private String loginIp;
    private LocalDateTime loginDate;
    private LocalDateTime pwdUpdateDate;
    private LocalDateTime createTime;

    @Data
    public static class RoleSimple {
        private Long id;
        private String name;
        private String code;
    }
}
