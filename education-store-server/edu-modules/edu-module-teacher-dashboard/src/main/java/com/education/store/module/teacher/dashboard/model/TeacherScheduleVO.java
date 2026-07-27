package com.education.store.module.teacher.dashboard.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 教师今日课表 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TeacherScheduleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 排课记录 ID */
    private Long scheduleId;

    /** 排课日期 yyyy-MM-dd */
    private String scheduleDate;

    /** 开始时间 HH:mm */
    private String startTime;

    /** 结束时间 HH:mm */
    private String endTime;

    /** 课程名称 */
    private String courseName;

    /** 班级名称 */
    private String className;

    /** 教室名称 */
    private String classroomName;

    /** 应到人数 */
    private Integer expectedCount;

    /** 已签到人数 */
    private Integer checkedCount;

    /** 课程状态：NOT_STARTED / IN_PROGRESS / FINISHED */
    private String courseStatus;

    /** 签到状态：NOT_STARTED / UNCHECKED / PARTIAL / COMPLETED */
    private String attendanceStatus;
}
