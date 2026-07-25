package com.education.store.module.classinfo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.education.store.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 班级排课规则实体（与班级1:N关联，支持每周重复和指定日期两种模式）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("edu_class_schedule_rule")
public class EduClassScheduleRule extends BaseEntity {

    /** 班级ID → edu_class.id */
    private Long classId;

    /** 规则类型(WEEKLY=每周重复, SPECIFIC=指定日期) */
    private String ruleType;

    /** 上课日（ruleType=WEEKLY 时使用，1=周一, 7=周日） */
    private Integer weekDay;

    /** 上课日期（ruleType=SPECIFIC 时使用） */
    private LocalDate scheduleDate;

    /** 上课开始时间 */
    private LocalTime startTime;

    /** 上课结束时间 */
    private LocalTime endTime;

    /** 每节课时长（分钟） */
    private Integer duration;

    /** 创建者 */
    private String createBy;

    /** 更新者 */
    private String updateBy;
}
