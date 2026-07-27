package com.education.store.module.system.entity.system;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.education.store.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户消息通知实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_notification")
public class UserNotification extends BaseEntity {

    /** 接收通知的用户ID */
    private Long userId;

    /** 通知标题 */
    private String title;

    /** 通知内容 */
    private String content;

    /** 通知类型: SIGN_IN/EVALUATION/HOMEWORK/EXAM/LEAVE/REDEMPTION/SYSTEM */
    private String type;

    /** 事件类型(冗余字段) */
    private String eventType;

    /** 关联业务ID(如排课ID/作业ID等) */
    private Long relatedId;

    /** 关联业务类型 */
    private String relatedType;

    /** 是否已读(0未读 1已读) */
    private Integer isRead;

    /** 阅读时间 */
    private LocalDateTime readTime;

    /** 发送人ID(系统通知可为空) */
    private Long senderId;

    /** 状态(1正常 0禁用) */
    private Integer status;

    /** 创建者 */
    private String createBy;

    /** 更新者 */
    private String updateBy;

    /** 发送人名称(非数据库字段) */
    @TableField(exist = false)
    private String senderName;

    /** 格式化时间(非数据库字段) */
    @TableField(exist = false)
    private String time;
}
