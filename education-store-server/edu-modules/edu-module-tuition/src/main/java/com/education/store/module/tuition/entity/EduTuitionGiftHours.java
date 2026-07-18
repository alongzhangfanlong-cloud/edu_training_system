package com.education.store.module.tuition.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.education.store.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("edu_tuition_gift_hours")
public class EduTuitionGiftHours extends BaseEntity {
    private String giftNo;
    private Long studentId;
    private Long courseId;
    private Integer hours;
    private String reason;
    private String reasonCustom;
    private String remark;
    private Long operatorId;
    private String createBy;
    private String updateBy;
}
