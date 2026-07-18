package com.education.store.module.dashboard.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 统计卡片数据 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatisticsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 在学学员总数 */
    private Integer studentCount;

    /** 当月排课总次数 */
    private Integer scheduleCount;

    /** 在职教师总数 */
    private Integer teacherCount;

    /** 本月营收（收费确认 - 退费完成） */
    private BigDecimal monthlyRevenue;

    /** 上月同期营收 */
    private BigDecimal lastMonthRevenue;

    /** 趋势：UP / DOWN / FLAT / NONE */
    private String revenueTrend;

    /** 趋势百分比，trend=NONE 时为 null */
    private BigDecimal revenueTrendPercentage;
}
