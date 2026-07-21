package com.education.store.module.system.entity.system;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.education.store.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {

    /** 登录账号 */
    private String username;

    /** 密码(BCrypt) - 不返回给前端 */
    @JsonIgnore
    private String password;

    /** 真实姓名 */
    private String realName;

    /** 昵称 */
    private String nickName;

    /** 邮箱 */
    private String email;

    /** 手机号 */
    private String mobile;

    /** 微信OpenID(唯一标识，可空) */
    private String wxOpenid;

    /** 性别(0未知 1男 2女) */
    private Integer gender;

    /** 头像URL */
    private String avatar;

    /** 状态(1正常 0停用) */
    private Integer status;

    /** 最后登录IP */
    private String loginIp;

    /** 最后登录时间 */
    private LocalDateTime loginDate;

    /** 密码最后更新时间 */
    private LocalDateTime pwdUpdateDate;

    /** 创建者 */
    private String createBy;

    /** 更新者 */
    private String updateBy;

    /** 备注 */
    private String remark;

    /** 角色列表(非数据库字段) */
    @TableField(exist = false)
    private List<SysRole> roles;

    /** 角色ID列表(非数据库字段) */
    @TableField(exist = false)
    private List<Long> roleIds;
}
