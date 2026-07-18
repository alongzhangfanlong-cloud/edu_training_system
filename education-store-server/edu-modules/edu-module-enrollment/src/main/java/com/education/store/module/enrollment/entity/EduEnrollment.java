package com.education.store.module.enrollment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.education.store.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 报名记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("edu_enrollment")
public class EduEnrollment extends BaseEntity {

    /** 学员ID → edu_student.id */
    private Long studentId;

    /** 课程ID → edu_course.id */
    private Long courseId;

    /** 班级ID → edu_class.id（可空） */
    private Long classId;

    /** 报名应缴金额 */
    private BigDecimal amount;

    /** 经手人ID → sys_user.id */
    private Long handlerId;

    /** 报名日期 */
    private LocalDate enrollmentDate;

    /** 收费状态(DRAFT/PENDING/PARTIAL/PAID/REFUNDED/CANCELLED) */
    private String paymentStatus;

    /** 关联首条收费记录ID */
    private Long tuitionRecordId;

    /** 备注 */
    private String remark;

    /** 创建者 */
    private String createBy;

    /** 更新者 */
    private String updateBy;
}
