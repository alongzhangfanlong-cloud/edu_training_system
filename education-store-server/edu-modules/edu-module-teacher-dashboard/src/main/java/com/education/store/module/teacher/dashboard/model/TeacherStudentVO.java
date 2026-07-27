package com.education.store.module.teacher.dashboard.model;

import lombok.Data;

/**
 * 教师端学生列表VO
 */
@Data
public class TeacherStudentVO {

    /** 学生ID */
    private Long studentId;

    /** 学生姓名 */
    private String studentName;

    /** 手机号 */
    private String mobile;

    /** 年级 */
    private String grade;

    /** 性别 */
    private String gender;

    /** 所属班级名称 */
    private String className;

    /** 课程名称 */
    private String courseName;

    /** 剩余课时 */
    private Integer remainingHours;
}
