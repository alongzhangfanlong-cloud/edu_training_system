package com.education.store.module.system.entity.system;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 用户通知偏好设置 — 微信订阅消息开关
 */
@Getter
@Setter
@TableName("user_notification_setting")
public class UserNotificationSetting {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 通知类型：SIGN_IN / EVALUATION / HOMEWORK / EXAM / LEAVE / REDEMPTION */
    private String notifyType;

    /** 是否开启（1开启 0关闭） */
    private Integer enabled;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
