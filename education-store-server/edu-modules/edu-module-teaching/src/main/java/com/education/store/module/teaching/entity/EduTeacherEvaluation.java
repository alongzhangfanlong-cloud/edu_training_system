package com.education.store.module.teaching.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.education.store.common.base.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
    @NotNull(message = "教师ID不能为空")
    private Long teacherId;

    /** 提交评价的学生ID */
    @NotNull(message = "学生ID不能为空")
    private Long studentId;

    /** 关联排课记录ID */
    @NotNull(message = "排课ID不能为空")
    private Long scheduleId;

    /** 综合评分 1-5 */
    @NotNull(message = "综合评分不能为空")
    @Positive(message = "评分必须大于0")
    private Integer ratingScore;

    /** 评价文字内容 */
    @NotBlank(message = "评价内容不能为空")
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
