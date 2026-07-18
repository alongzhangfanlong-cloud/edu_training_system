package com.education.store.module.teaching.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.education.store.common.base.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "排课ID不能为空")
    private Long scheduleId;

    /** 关联班级ID（冗余） */
    @NotNull(message = "班级ID不能为空")
    private Long classId;

    /** 授课教师ID */
    @NotNull(message = "教师ID不能为空")
    private Long teacherId;

    /** 课堂总结 */
    @NotBlank(message = "课堂总结不能为空")
    private String summary;

    /** 状态: 0=草稿, 1=已提交 */
    private Integer status;

    /** 提交时间 */
    private LocalDateTime submitTime;
}
