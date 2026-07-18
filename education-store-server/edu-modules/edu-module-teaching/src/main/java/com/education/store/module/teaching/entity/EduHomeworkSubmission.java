package com.education.store.module.teaching.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.education.store.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 作业提交实体
 *
 * @see com.education.store.module.teaching.mapper.EduHomeworkSubmissionMapper
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("edu_homework_submission")
public class EduHomeworkSubmission extends BaseEntity {

    /** 关联作业ID → edu_homework.id */
    private Long homeworkId;

    /** 学生ID → edu_student.id */
    private Long studentId;

    /** 文字内容（学生提交的文本备注） */
    private String content;

    /** 图片URL列表（JSON数组） */
    private String imageUrls;

    /** 状态: SUBMITTED=已提交, GRADED=已批改, LATE=迟交 */
    private String status;

    /** 评分（1-5） */
    private Integer score;

    /** 教师评语 */
    private String comment;

    /** 提交时间 */
    private LocalDateTime submittedAt;

    /** 批改时间 */
    private LocalDateTime gradedAt;

    /** 批改教师ID */
    private Long gradedBy;
}
