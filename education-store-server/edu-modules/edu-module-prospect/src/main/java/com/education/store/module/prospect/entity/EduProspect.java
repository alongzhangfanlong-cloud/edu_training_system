package com.education.store.module.prospect.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.education.store.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 意向学员实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("edu_prospect")
public class EduProspect extends BaseEntity {

    /** 姓名 */
    private String name;

    /** 性别(M/F/U) */
    private String gender;

    /** 出生日期 */
    private LocalDate birthDate;

    /** 年级编码 */
    private String grade;

    /** 就读学校 */
    private String school;

    /** 联系电话 */
    private String mobile;

    /** 家长姓名 */
    private String parentName;

    /** 亲属关系（同 Relation 枚举: FATHER/MOTHER/GRANDFATHER/GRANDMOTHER/GRANDFATHER_M/GRANDMOTHER_M/OTHER） */
    private String parentRelation;

    /** 家长手机号 */
    private String parentMobile;

    /** 微信昵称 */
    private String wechatNickname;

    /** 意向课程ID → edu_course.id */
    private Long intendedCourseId;

    /** 意向等级(HIGH/MEDIUM/LOW) */
    private String interestLevel;

    /** 来源渠道(DIRECT_MARKETING/REFERRAL/ONLINE/OTHER) */
    private String source;

    /** 顾问ID → sys_user.id */
    private Long counselorId;

    /** 跟进状态(NEW/CONTACTED/TRIAL/CONVERTED/LOST) */
    private String followStatus;

    /** 体验课次数（由排课/考勤自动累加） */
    private Integer trialLessonCount;

    /** 最近跟进日期（新增跟进记录时自动更新） */
    private LocalDateTime lastFollowupDate;

    /** 转化后在学学员ID → edu_student.id（转化后回填） */
    private Long convertedStudentId;

    /** 备注 */
    private String remark;

    /** 创建者 */
    private String createBy;

    /** 更新者 */
    private String updateBy;

    // ===== 以下为展示用 transient 字段，不映射数据库 =====

    /** 顾问姓名（关联 sys_user.real_name） */
    @TableField(exist = false)
    private String counselorName;

    /** 意向课程名称（用户填写的文本，若有关联课程ID则由 fillNames 覆盖为实际课程名） */
    private String intendedCourseName;
}
