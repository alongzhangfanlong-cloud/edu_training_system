package com.education.store.module.attendance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.education.store.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("edu_leave_teacher")
public class EduLeaveTeacher extends BaseEntity {
    private Long teacherId;
    private LocalDate leaveStartDate;
    private LocalDate leaveEndDate;
    private Integer leaveType;
    private String reason;
    private Integer status;
    private String applicantType;
    private Long applicantId;
    private Long approverId;
    private LocalDateTime approveTime;
    private String rejectReason;
    private String createBy;
    private String updateBy;
}
