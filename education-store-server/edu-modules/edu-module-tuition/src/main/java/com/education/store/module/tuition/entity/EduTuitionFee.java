package com.education.store.module.tuition.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.education.store.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("edu_tuition_fee")
public class EduTuitionFee extends BaseEntity {
    private String feeNo;
    private Long enrollmentId;
    private Long studentId;
    private Long courseId;
    private Long classId;
    private String feeType;
    private BigDecimal amountReceivable;
    private BigDecimal amountPaid;
    private String paymentMethod;
    private String status;
    private LocalDateTime payTime;
    private String remark;
    private Long operatorId;
    private String updateBy;

    @TableField(exist = false)
    private String studentName;

    @TableField(exist = false)
    private String courseName;

    @TableField(exist = false)
    private String className;
}
