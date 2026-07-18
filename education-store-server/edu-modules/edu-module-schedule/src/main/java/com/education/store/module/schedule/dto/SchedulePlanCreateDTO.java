package com.education.store.module.schedule.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 排课计划创建/预览 DTO — 包含排课生成所需的所有参数
 */
@Data
public class SchedulePlanCreateDTO {

    /** 关联班级ID列表 */
    @NotEmpty(message = "班级列表不能为空")
    private List<Long> classIds;

    /** 排课起始日期 */
    @NotNull(message = "起始日期不能为空")
    private LocalDate startDate;

    /** 排课结束日期 */
    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;

    /** 每周上课日（1=Mon ... 7=Sun），空则默认周一 */
    private List<Integer> weekDays;

    /** 每天上课开始时间 */
    @NotNull(message = "上课开始时间不能为空")
    private LocalTime startTime;

    /** 每天上课结束时间 */
    @NotNull(message = "上课结束时间不能为空")
    private LocalTime endTime;

    /** 授课教师ID（可选，为空则使用班级默认教师） */
    private Long teacherId;

    /** 教室ID（可选，为空则使用班级默认教室） */
    private Long roomId;

    /** 排课类型(NORMAL/TRIAL/MAKEUP)，默认 NORMAL */
    private String scheduleType;

    /** 备注 */
    private String remark;
}
