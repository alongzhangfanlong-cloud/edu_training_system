package com.education.store.module.enrollment.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 报名 VO
 */
@Data
public class EnrollmentVO {

    private Long id;
    private Long studentId;
    private String studentName;
    private Long courseId;
    private String courseName;
    private Long classId;
    private String className;
    private BigDecimal amount;
    private Long handlerId;
    private LocalDate enrollmentDate;
    private String paymentStatus;
    private Long tuitionRecordId;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
