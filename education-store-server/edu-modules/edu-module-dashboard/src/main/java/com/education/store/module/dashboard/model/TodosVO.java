package com.education.store.module.dashboard.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 待办提醒 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TodosVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 待审批请假 */
    private TodoSection<PendingLeaveItem> pendingLeaves;

    /** 待处理退费 */
    private TodoSection<PendingRefundItem> pendingRefunds;

    /** 排课冲突预警 */
    private TodoSection<ScheduleConflictItem> scheduleConflicts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TodoSection<T> implements Serializable {

        private static final long serialVersionUID = 1L;

        /** 待办数量 */
        private Integer count;

        /** 待办条目列表 */
        private List<T> items;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PendingLeaveItem implements Serializable {

        private static final long serialVersionUID = 1L;

        /** 请假 ID */
        private Long leaveId;

        /** 申请人类型：TEACHER / STUDENT */
        private String applicantType;

        /** 教师姓名（教师请假时） */
        private String teacherName;

        /** 学生姓名（学生请假时） */
        private String studentName;

        /** 请假类型 */
        private String leaveType;

        /** 请假类型名称 */
        private String leaveTypeName;

        /** 开始日期 */
        private String startDate;

        /** 结束日期 */
        private String endDate;

        /** 时段：FULL_DAY / AM / PM */
        private String period;

        /** 申请时间 */
        private String applyTime;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PendingRefundItem implements Serializable {

        private static final long serialVersionUID = 1L;

        /** 退费 ID */
        private Long refundId;

        /** 学生姓名 */
        private String studentName;

        /** 退费金额 */
        private BigDecimal amount;

        /** 申请时间 */
        private String applyTime;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ScheduleConflictItem implements Serializable {

        private static final long serialVersionUID = 1L;

        /** 冲突描述 */
        private String description;

        /** 冲突日期 */
        private String date;

        /** 冲突时间段 */
        private String timeRange;

        /** 涉及班级A */
        private String classA;

        /** 涉及班级B */
        private String classB;
    }
}
