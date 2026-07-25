package com.education.store.module.schedule.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.education.store.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 排课计划实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("edu_schedule_plan")
public class EduSchedulePlan extends BaseEntity {

    /** 关联班级ID */
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

    /** 班级名称（非数据库字段，关联查询填充） */
    @TableField(exist = false)
    private String className;

    /** 课程名称（非数据库字段，由关联班级的课程推导） */
    @TableField(exist = false)
    private String courseName;

    /** 教师姓名（非数据库字段，由关联班级的教师推导） */
    @TableField(exist = false)
    private String teacherName;

    /** 教室ID → edu_classroom.id（创建计划时选定） */
    private Long roomId;

    /** 教室名称（非数据库字段，关联查询填充） */
    @TableField(exist = false)
    private String roomName;

    /** 创建者 */
    private String createBy;

    /** 更新者 */
    private String updateBy;
}
