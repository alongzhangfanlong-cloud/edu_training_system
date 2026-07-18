package com.education.store.module.classinfo.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 班级 VO
 */
@Data
public class ClassVO {

    private Long id;
    private String name;
    private String code;
    private Long courseId;
    private String courseName;
    private Long teacherId;
    private String teacherName;
    private Long classroomId;
    private String classroomName;
    private Integer maxStudents;
    private Integer currentCount;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
