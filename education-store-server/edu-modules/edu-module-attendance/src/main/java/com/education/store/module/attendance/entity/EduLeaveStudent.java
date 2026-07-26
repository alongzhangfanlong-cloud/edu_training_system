package com.education.store.module.attendance.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.education.store.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("edu_leave_student")
public class EduLeaveStudent extends BaseEntity {
    private Long studentId;
    private LocalDate leaveDate;
    private Integer leaveType;
    private String reason;
    private Integer status;
    private String applicantType;
    private Long applicantId;
    private Long confirmTeacherId;
    private LocalDateTime confirmTime;
    private String rejectReason;
    private Integer autoApproved;
    private String createBy;
    private String updateBy;

    @TableField(exist = false)
    private String studentName;

    @TableField(exist = false)
    private String confirmTeacherName;
}
