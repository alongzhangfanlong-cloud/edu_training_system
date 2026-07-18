package com.education.store.module.teaching.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.education.store.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 课堂评价实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("edu_evaluation")
public class EduEvaluation extends BaseEntity {

    /** 关联排课记录ID */
    private Long scheduleId;

    /** 关联班级ID（冗余） */
    private Long classId;

    /** 授课教师ID */
    private Long teacherId;

    /** 课堂总结 */
    private String summary;

    /** 状态: 0=草稿, 1=已提交 */
    private Integer status;

    /** 提交时间 */
    private LocalDateTime submitTime;
}
