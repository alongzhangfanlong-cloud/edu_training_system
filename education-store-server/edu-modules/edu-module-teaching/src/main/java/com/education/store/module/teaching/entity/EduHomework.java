package com.education.store.module.teaching.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.education.store.common.base.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 课后作业实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("edu_homework")
public class EduHomework extends BaseEntity {

    /** 关联班级ID */
    @NotNull(message = "班级ID不能为空")
    private Long classId;

    /** 关联课程ID */
    private Long courseId;

    /** 关联排课记录ID */
    private Long scheduleId;

    /** 作业标题 */
    @NotBlank(message = "作业标题不能为空")
    private String title;

    /** 作业内容 */
    @NotBlank(message = "作业内容不能为空")
    private String content;

    /** 布置教师ID */
    @NotNull(message = "教师ID不能为空")
    private Long teacherId;

    /** 截止时间（非数据库字段，从排课关联） */
    @TableField(exist = false)
    private LocalDate deadline;

    /** 是否被编辑更新过: 0=否, 1=是 */
    private Integer isUpdated;
}
