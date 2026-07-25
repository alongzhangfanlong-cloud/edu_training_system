package com.education.store.module.schedule.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 排课方案保存 DTO — 前端表单直接提交
 */
@Data
public class SchedulePlanSaveDTO {

    /** 关联班级ID */
    @NotNull(message = "班级不能为空")
    private Long classId;

    /** 排课起始日期 */
    @NotNull(message = "起始日期不能为空")
    private LocalDate startDate;

    /** 排课结束日期 */
    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;

    /** 教室ID（可选） */
    private Long roomId;

    /** 备注 */
    private String remark;
}
