package com.education.store.module.classinfo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.education.store.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 班级学员关联实体（N02↔N03 跨模块桥接表）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("edu_class_student")
public class EduClassStudent extends BaseEntity {

    /** 班级ID → edu_class.id */
    private Long classId;

    /** 学员ID → edu_student.id */
    private Long studentId;

    /** 加入时间 */
    private LocalDateTime joinTime;

    /** 在班状态(ENROLLED/SUSPENDED/EXITED) */
    private String status;

    /** 备注 */
    private String remark;
}
