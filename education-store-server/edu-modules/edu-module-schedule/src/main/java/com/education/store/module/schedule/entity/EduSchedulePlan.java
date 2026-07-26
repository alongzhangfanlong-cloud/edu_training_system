package com.education.store.module.schedule.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.education.store.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 排课计划实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("edu_schedule_plan")
public class EduSchedulePlan extends BaseEntity {

    /** 关联班级ID（兼容旧代码，单班级） */
    private Long classId;

    /** 排课起始日期 */
    private LocalDate startDate;

    /** 排课结束日期 */
    private LocalDate endDate;

    /** 状态(UNGENERATED/GENERATED) */
    private String status;

    /** 冲突详情JSON */
    private String conflictDetail;

    /** 备注 */
    private String remark;

    /** 教室ID */
    private Long roomId;

    /** 排课配置JSON(weekDays/startTime/endTime/teacherId) */
    private String planConfig;

    /** 班级名称（非数据库字段，关联查询填充） */
    @TableField(exist = false)
    private String className;

    /** 课程名称（非数据库字段，由关联班级的课程推导） */
    @TableField(exist = false)
    private String courseName;

    /** 教师姓名（非数据库字段，由关联班级的教师推导） */
    @TableField(exist = false)
    private String teacherName;

    /** 教室名称（非数据库字段，关联查询填充） */
    @TableField(exist = false)
    private String roomName;

    /** 计划名称（非数据库字段，默认用班级名称） */
    @TableField(exist = false)
    private String name;

    /** 创建者 */
    private String createBy;

    /** 更新者 */
    private String updateBy;

    // ========== 非数据库字段：排课参数（从planConfig解析） ==========
    @TableField(exist = false)
    private List<Integer> weekDays;

    @TableField(exist = false)
    private LocalTime startTime;

    @TableField(exist = false)
    private LocalTime endTime;

    @TableField(exist = false)
    private Long teacherId;

    @TableField(exist = false)
    private Integer plannedLessons;

    @TableField(exist = false)
    private Integer scheduledLessons;
}
