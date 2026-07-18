package com.education.store.module.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 排课预览响应 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchedulePreviewVO {

    /** 预览生成的排课明细列表 */
    private List<ScheduleSlot> slots;

    /** 冲突数量 */
    private int conflictCount;

    /** 总槽位数 */
    private int totalCount;

    /**
     * 单个排课槽位（含冲突信息）
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScheduleSlot {
        /** 班级ID */
        private Long classId;
        /** 班级名称 */
        private String className;
        /** 课程ID */
        private Long courseId;
        /** 教师ID */
        private Long teacherId;
        /** 教师姓名 */
        private String teacherName;
        /** 教室ID */
        private Long roomId;
        /** 教室名称 */
        private String roomName;
        /** 上课日期 */
        private LocalDate scheduleDate;
        /** 开始时间 */
        private LocalTime startTime;
        /** 结束时间 */
        private LocalTime endTime;
        /** 是否有冲突 */
        private boolean conflict;
        /** 冲突详情 */
        private String conflictDetail;
    }
}
