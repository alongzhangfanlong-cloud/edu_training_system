package com.education.store.module.tuition.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 收费 VO
 */
@Data
public class FeeVO {

    private Long id;
    private String feeNo;
    private Long enrollmentId;
    private Long studentId;
    private String studentName;
    private Long courseId;
    private String courseName;
    private Long classId;
    private String className;
    private String feeType;
    private BigDecimal amountReceivable;
    private BigDecimal amountPaid;
    private String paymentMethod;
    private String status;
    private LocalDateTime payTime;
    private String remark;
    private Long operatorId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
