package com.education.store.module.dashboard.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 本月报名统计 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnrollmentStatsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 月份标签 */
    private String monthLabel;

    /** 本月报名总人次 */
    private Integer totalEnrollments;

    /** 课程报名列表 */
    private List<CourseEnrollment> courses;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CourseEnrollment implements Serializable {

        private static final long serialVersionUID = 1L;

        /** 课程 ID */
        private Long courseId;

        /** 课程名称 */
        private String courseName;

        /** 本月报名人数 */
        private Integer enrollmentCount;
    }
}
