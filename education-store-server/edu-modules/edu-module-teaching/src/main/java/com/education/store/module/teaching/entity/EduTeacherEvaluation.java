package com.education.store.module.teaching.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.education.store.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 学评教实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("edu_teacher_evaluation")
public class EduTeacherEvaluation extends BaseEntity {

    /** 被评价教师ID */
    private Long teacherId;

    /** 提交评价的学生ID */
    private Long studentId;

    /** 关联排课记录ID */
    private Long scheduleId;

    /** 综合评分 1-5 */
    private Integer ratingScore;

    /** 评价文字内容 */
    private String content;

    /** 评价标签JSON */
    private String tags;

    /** 是否匿名: 0=实名, 1=匿名 */
    private Integer isAnonymous;

    /** 状态: ACTIVE/HIDDEN */
    private String status;

    /** 提交时间 */
    private LocalDateTime submitTime;
}
