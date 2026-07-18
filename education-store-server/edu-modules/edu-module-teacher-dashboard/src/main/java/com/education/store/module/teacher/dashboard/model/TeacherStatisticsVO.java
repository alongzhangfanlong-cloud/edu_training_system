package com.education.store.module.teacher.dashboard.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 教师工作台统计卡片 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TeacherStatisticsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 今日课程数 */
    private Integer todayCourseCount;

    /** 今日待签到班级数 */
    private Integer todayUncheckedClassCount;

    /** 待评价学生人次 */
    private Integer pendingEvaluationCount;
}
