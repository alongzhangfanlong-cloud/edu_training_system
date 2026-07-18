package com.education.store.module.dashboard.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 排课学生签到详情 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScheduleStudentVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 排课 ID */
    private Long scheduleId;

    /** 应到总人数 */
    private Integer totalCount;

    /** 已签到人数 */
    private Integer attendedCount;

    /** 学生列表 */
    private List<StudentEntry> students;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class StudentEntry implements Serializable {

        private static final long serialVersionUID = 1L;

        /** 学生 ID */
        private Long studentId;

        /** 学生姓名 */
        private String studentName;

        /** 签到状态：PRESENT / LATE / ABSENT / null(未签到) */
        private String attendanceStatus;
    }
}
