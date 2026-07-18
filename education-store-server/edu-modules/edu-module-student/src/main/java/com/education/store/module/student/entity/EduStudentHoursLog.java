package com.education.store.module.student.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课时变动明细日志（只追加，不修改不删除）
 *
 * 设计原则：
 * - 不继承 BaseEntity：无 update_time、无 deleted
 * - 类似银行流水，每次课时变动新增一条记录
 * - 当前余额 = SUM(change_amount) GROUP BY student_id, course_id
 */
@Data
@TableName("edu_student_hours_log")
public class EduStudentHoursLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 学员ID → edu_student.id */
    private Long studentId;

    /** 关联课程ID → edu_course.id（赠送/手动调整时可为空） */
    private Long courseId;

    /** 关联报名记录ID → Batch 2 edu_enrollment.id（预留） */
    private Long enrollmentId;

    /** 关联班级ID → edu_class.id */
    private Long classId;

    /** 变动类型(ENROLL_GAIN/CONSUME/LEAVE_RETURN/MAKEUP_CONSUME/REFUND_DEDUCT/GIFT_GAIN/MANUAL_ADJUST) */
    private String changeType;

    /** 变动数量（正数=增加，负数=减少） */
    private BigDecimal changeAmount;

    /** 变动后剩余课时 */
    private BigDecimal balanceAfter;

    /** 来源业务类型(ENROLLMENT/TUITION/ATTENDANCE/LEAVE/GIFT/MANUAL) */
    private String sourceType;

    /** 来源业务记录ID */
    private Long sourceId;

    /** 经手人ID → sys_user.id */
    private Long operatorId;

    /** 备注 */
    private String remark;

    /** 创建时间（即变动发生时间） */
    private LocalDateTime createTime;
}
