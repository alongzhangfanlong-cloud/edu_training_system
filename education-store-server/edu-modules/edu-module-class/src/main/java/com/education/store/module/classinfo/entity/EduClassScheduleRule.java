package com.education.store.module.classinfo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.education.store.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalTime;

/**
 * 班级排课规则实体（与班级1:1关联）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("edu_class_schedule_rule")
public class EduClassScheduleRule extends BaseEntity {

    /** 班级ID → edu_class.id（UNIQUE，1:1） */
    private Long classId;

    /** 每周上课日（逗号分隔，1=周一,7=周日，如 "1,3,5"） */
    private String weekDays;

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
