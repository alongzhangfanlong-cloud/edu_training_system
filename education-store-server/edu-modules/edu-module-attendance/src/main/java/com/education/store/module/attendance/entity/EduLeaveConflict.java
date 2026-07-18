package com.education.store.module.attendance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.education.store.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("edu_leave_conflict")
public class EduLeaveConflict extends BaseEntity {
    private Long leaveId;
    private Long scheduleId;
    private Integer resolveType;
    private Long substituteTeacherId;
    private LocalDate newScheduleDate;
    private String newTimeSlot;
    private Long newClassroomId;
    private Integer status;
    private String createBy;
    private String updateBy;
}
