package com.education.store.module.student.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.education.store.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 在学学员实体（N02 核心）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("edu_student")
public class EduStudent extends BaseEntity {

    /** 学员姓名 */
    private String name;

    /** 性别(M/F) */
    private String gender;

    /** 出生日期（年龄由此派生） */
    private LocalDate birthDate;

    /** 年级编码 */
    private String grade;

    /** 就读学校 */
    private String school;

    /** 手机号 */
    private String mobile;

    /** 顾问ID → sys_user.id */
    private Long counselorId;

    /** 家长用户ID → sys_user.id（主家长，用于微信通知等场景） */
    private Long parentId;

    /** 来源渠道(DIRECT_MARKETING/REFERRAL/ONLINE/OTHER) */
    private String source;

    /** 标签（逗号分隔） */
    private String tags;

    /** 状态(ENROLLED/SUSPENDED/COMPLETED/LOST) */
    private String status;

    /** 累计获得总课时（冗余缓存，由edu_student_hours_log汇总同步更新） */
    private java.math.BigDecimal totalHours;

    /** 剩余课时汇总（冗余缓存，由edu_student_hours_log汇总同步更新） */
    private java.math.BigDecimal remainingHours;

    /** 最近到校时间（冗余缓存，取最近签到记录） */
    private LocalDateTime lastVisitTime;

    /** 结业时间 */
    private LocalDateTime completedAt;

    /** 备注 */
    private String remark;

    /** 创建者 */
    private String createBy;

    /** 更新者 */
    private String updateBy;
}
