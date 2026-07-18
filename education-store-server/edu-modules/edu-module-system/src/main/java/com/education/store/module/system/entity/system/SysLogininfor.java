package com.education.store.module.system.entity.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统登录日志实体
 */
@Data
@TableName("sys_login_log")
public class SysLogininfor implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 访问ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 登录账号 */
    private String username;

    /** 登录IP */
    private String ipaddr;

    /** 登录地点 */
    private String loginLocation;

    /** 浏览器类型 */
    private String browser;

    /** 操作系统 */
    private String os;

    /** 状态(1成功 0失败) */
    private Integer status;

    /** 提示信息 */
    private String msg;

    /** 登录时间 */
    private LocalDateTime loginTime;
}
