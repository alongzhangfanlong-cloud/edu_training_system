package com.education.store.module.payroll.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.education.store.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 教师课酬记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("edu_teacher_payroll")
public class EduTeacherPayroll extends BaseEntity {

    /** 教师ID → sys_user.id */
    private Long teacherId;

    /** 月份(YYYY-MM) */
    private String month;

    /** 课程ID → edu_course.id */
    private Long courseId;

    /** 消课数（人次） */
    private Integer lessonCount;

    /** 计算时锁定的单节课酬 */
    private BigDecimal payPerLesson;

    /** 小计(lesson_count × pay_per_lesson) */
    private BigDecimal totalPay;

    /** 状态(DRAFT/CONFIRMED/PAID) */
    private String status;

    /** 确认时间 */
    private LocalDateTime confirmedAt;

    /** 确认人ID */
    private Long confirmedBy;

    /** 创建者 */
    private String createBy;

    /** 更新者 */
    private String updateBy;
}
