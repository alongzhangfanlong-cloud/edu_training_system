package com.education.store.module.dashboard.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 排课日历 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CalendarVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 视图类型：MONTH / WEEK / DAY */
    private String viewType;

    /** 视图标签 */
    private String viewLabel;

    /** 是否可以切换到上一个时间段 */
    private Boolean hasPrev;

    /** 是否可以切换到下一个时间段 */
    private Boolean hasNext;

    /** 是否为当前时间段 */
    private Boolean isCurrentPeriod;

    /** 日期列表 */
    private List<DateEntry> dates;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DateEntry implements Serializable {

        private static final long serialVersionUID = 1L;

        /** 日期 yyyy-MM-dd */
        private String date;

        /** 星期几 1=周一 ~ 7=周日 */
        private Integer dayOfWeek;

        /** 星期中文名 */
        private String dayLabel;

        /** 是否为今天 */
        private Boolean isToday;

        /** 是否为当月 */
        private Boolean isCurrentMonth;

        /** 该日排课列表 */
        private List<ScheduleItem> schedules;

        /** 该日排课总数 */
        private Integer totalCount;

        /** 实际显示的条数 */
        private Integer displayCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ScheduleItem implements Serializable {

        private static final long serialVersionUID = 1L;

        /** 排课记录 ID */
        private Long scheduleId;

        /** 上课时间 HH:mm */
        private String startTime;

        /** 下课时间 HH:mm */
        private String endTime;

        /** 课程名称 */
        private String courseName;

        /** 班级名称 */
        private String className;

        /** 教师姓名 */
        private String teacherName;

        /** 教室名称 */
        private String classroomName;

        /** 应到人数 */
        private Integer expectedCount;

        /** 已签到人数 */
        private Integer attendedCount;

        /** 排课状态：NOT_STARTED / IN_PROGRESS / FINISHED */
        private String status;
    }
}
