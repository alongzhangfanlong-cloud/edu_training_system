package com.education.store.module.tuition.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.education.store.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("edu_tuition_refund")
public class EduTuitionRefund extends BaseEntity {
    private String refundNo;
    private Long feeId;
    private Long studentId;
    private Long courseId;
    private BigDecimal amount;
    private String reason;
    private String reasonCustom;
    private String refundMethod;
    private String status;
    private Integer hoursDeducted;
    private Long applicantId;
    private Long approverId;
    private LocalDateTime approvalTime;
    private String rejectReason;
    private String remark;
    private String createBy;
    private String updateBy;
}
