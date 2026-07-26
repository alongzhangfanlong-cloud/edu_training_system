package com.education.store.module.payroll.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.education.store.common.base.BaseEntity;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 教师课酬单价实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("edu_teacher_pay_rate")
public class EduTeacherPayRate extends BaseEntity {

    /** 教师ID → sys_user.id */
    @NotNull(message = "教师ID不能为空")
    private Long teacherId;

    /** 课程ID → edu_course.id */
    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    /** 单节课酬（元） */
    @NotNull(message = "单节课酬不能为空")
    @DecimalMin(value = "0.01", message = "单节课酬必须大于0")
    private BigDecimal payPerLesson;

    /** 生效起始日期 */
    @NotNull(message = "生效日期不能为空")
    private LocalDate effectiveFrom;

    /** 生效截止日期（NULL=长期有效） */
    private LocalDate effectiveTo;

    /** 创建者 */
    private String createBy;

    /** 更新者 */
    private String updateBy;

    /** 教师姓名（非数据库字段，用于列表展示） */
    @TableField(exist = false)
    private String teacherName;

    /** 课程名称（非数据库字段，用于列表展示） */
    @TableField(exist = false)
    private String courseName;
}
