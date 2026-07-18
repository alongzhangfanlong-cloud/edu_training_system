package com.education.store.module.system.entity.system;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.education.store.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 通知公告实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_notice")
public class SysNotice extends BaseEntity {

    /** 公告标题 */
    private String title;

    /** 公告内容 */
    private String content;

    /** 公告类型(1通知 2公告) */
    private String type;

    /** 紧急程度(normal/important/urgent) */
    private String level;

    /** 发布人ID */
    private Long publishBy;

    /** 发布时间 */
    private LocalDateTime publishTime;

    /** 状态(1已发布 0草稿) */
    private Integer status;

    /** 创建者 */
    private String createBy;

    /** 更新者 */
    private String updateBy;

    /** 发布人名称(非数据库字段) */
    @TableField(exist = false)
    private String publisherName;
}
