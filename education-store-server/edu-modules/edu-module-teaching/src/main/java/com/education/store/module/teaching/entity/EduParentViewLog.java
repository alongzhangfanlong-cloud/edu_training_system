package com.education.store.module.teaching.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 家长查阅记录实体
 */
@Data
@TableName("edu_parent_view_log")
public class EduParentViewLog implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 家长用户ID */
    private Long parentUserId;

    /** 学生ID */
    private Long studentId;

    /** 业务类型: EVALUATION/EXAM/HOMEWORK */
    private String bizType;

    /** 业务记录ID */
    private Long bizId;

    /** 首次查看时间 */
    private LocalDateTime firstViewTime;

    /** 最近查看时间 */
    private LocalDateTime lastViewTime;

    /** 查看次数 */
    private Integer viewCount;
}
