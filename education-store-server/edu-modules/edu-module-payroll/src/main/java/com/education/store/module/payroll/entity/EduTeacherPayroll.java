package com.education.store.module.payroll.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.education.store.common.base.BaseEntity;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "教师ID不能为空")
    private Long teacherId;

    /** 月份(YYYY-MM) */
    @NotBlank(message = "月份不能为空")
    private String month;

    /** 课程ID → edu_course.id */
    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    /** 消课数（人次） */
    @NotNull(message = "消课数不能为空")
    @Min(value = 0, message = "消课数不能为负数")
    private Integer lessonCount;

    /** 计算时锁定的单节课酬 */
    @NotNull(message = "单节课酬不能为空")
    @DecimalMin(value = "0.00", message = "单节课酬不能为负数")
    private BigDecimal payPerLesson;

    /** 小计(lesson_count × pay_per_lesson) — 服务端计算，客户端不可设置 */
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

    /** 教师姓名（非数据库字段） */
    @TableField(exist = false)
    private String teacherName;

    /** 课程名称（非数据库字段） */
    @TableField(exist = false)
    private String courseName;
}
