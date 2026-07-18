package com.education.store.module.payroll.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.education.store.common.base.BaseEntity;
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
    private Long teacherId;

    /** 课程ID → edu_course.id */
    private Long courseId;

    /** 单节课酬（元） */
    private BigDecimal payPerLesson;

    /** 生效起始日期 */
    private LocalDate effectiveFrom;

    /** 生效截止日期（NULL=长期有效） */
    private LocalDate effectiveTo;

    /** 创建者 */
    private String createBy;

    /** 更新者 */
    private String updateBy;
}
