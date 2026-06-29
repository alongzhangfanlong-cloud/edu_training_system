package com.edu.modules.student.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class StudentDetailDTO {
    private Long id;
    private String studentNo;
    private String name;
    private String avatar;
    private Integer gender;
    private LocalDate birthday;
    private String grade;
    private String school;
    private String phone;
    private List<String> tags;
    private Integer status;
    private String source;
    private String remark;
    private List<ParentDTO> parents;
    private List<CourseInfoDTO> courses;
    private AttendanceStatsDTO attendanceStats;

    @Data
    public static class ParentDTO {
        private Long id;
        private String parentName;
        private String relation;
        private String phone;
        private Boolean isPrimary;
    }

    @Data
    public static class CourseInfoDTO {
        private Long classId;
        private String className;
        private String courseName;
        private String teacherName;
        private BigDecimal totalHours;
        private BigDecimal usedHours;
        private BigDecimal remainHours;
        private Integer status;
    }

    @Data
    public static class AttendanceStatsDTO {
        private Integer total;
        private Integer present;
        private Integer leave;
        private Integer absent;
        private BigDecimal presentRate;
    }
}
