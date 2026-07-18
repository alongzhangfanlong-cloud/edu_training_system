package com.education.store.module.schedule.entity;

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

    /** 关联班级ID列表（JSON数组） */
    private String classIds;

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

    /** 创建者 */
    private String createBy;

    /** 更新者 */
    private String updateBy;
}
