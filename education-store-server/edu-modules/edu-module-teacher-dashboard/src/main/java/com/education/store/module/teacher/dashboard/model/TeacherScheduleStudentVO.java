package com.education.store.module.teacher.dashboard.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 教师课表学生签到 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TeacherScheduleStudentVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 学生 ID */
    private Long studentId;

    /** 学生姓名 */
    private String studentName;

    /** 签到状态：PRESENT / LATE / ABSENT / null(未签到) */
    private String attendanceStatus;

    /** 课程名称 */
    private String courseName;

    /** 班级名称 */
    private String className;
}
