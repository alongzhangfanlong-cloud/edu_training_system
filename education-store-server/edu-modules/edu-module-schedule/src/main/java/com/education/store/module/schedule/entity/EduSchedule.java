package com.education.store.module.schedule.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.education.store.common.base.BaseEntity;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 排课记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("edu_schedule")
public class EduSchedule extends BaseEntity {

    /** 关联排课计划ID */
    private Long planId;

    /** 班级ID → edu_class.id */
    @NotNull(message = "班级不能为空")
    private Long classId;

    /** 课程ID → edu_course.id（冗余） */
    @NotNull(message = "课程不能为空")
    private Long courseId;

    /** 授课教师ID → sys_user.id */
    @NotNull(message = "教师不能为空")
    private Long teacherId;

    /** 教室ID → edu_classroom.id */
    private Long roomId;

    /** 上课日期 */
    @NotNull(message = "上课日期不能为空")
    private LocalDate scheduleDate;

    /** 开始时间 */
    @NotNull(message = "开始时间不能为空")
    private LocalTime startTime;

    /** 结束时间 */
    @NotNull(message = "结束时间不能为空")
    private LocalTime endTime;

    /** 排课类型(NORMAL/TRIAL/MAKEUP) */
    private String scheduleType;

    /** 状态(NORMAL/CANCELLED/RESCHEDULED/MAKEUP/SUSPENDED/SUBSTITUTE) */
    private String status;

    /** 是否强制排课 */
    private Integer forceFlag;

    /** 强制排课冲突摘要 */
    private String forceReason;

    /** 取消原因 */
    private String cancelReason;

    /** 取消时间 */
    private LocalDateTime cancelTime;

    /** 取消人 */
    private String cancelBy;

    /** 调课来源排课ID */
    private Long rescheduledFromId;

    /** 调课目标排课ID */
    private Long rescheduledToId;

    /** 是否已消课(0未消 1已消) */
    private Integer consumed;

    /** 消课时间 */
    private LocalDateTime consumeTime;

    /** 消课人 */
    private String consumeBy;

    /** 备注 */
    private String remark;

    /** 创建者 */
    private String createBy;

    /** 更新者 */
    private String updateBy;
}
