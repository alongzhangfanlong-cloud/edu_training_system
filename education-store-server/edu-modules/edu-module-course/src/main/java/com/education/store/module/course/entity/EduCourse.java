package com.education.store.module.course.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.education.store.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 课程实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("edu_course")
public class EduCourse extends BaseEntity {

    /** 课程名称 */
    private String name;

    /** 课程编号（全局唯一） */
    private String code;

    /** 所属科目ID → edu_subject.id */
    private Long subjectId;

    /** 课程类型(BATCH/ONE_ON_ONE/SMALL_GROUP) */
    private String courseType;

    /** 默认单价 */
    private BigDecimal price;

    /** 适用年级编码（NULL=不限） */
    private String applicableGrade;

    /** 封面图URL */
    private String coverImage;

    /** 状态(1上架 0下架) */
    private Integer status;

    /** 排序号 */
    private Integer sortOrder;

    /** 课程介绍 */
    private String description;

    /** 创建者 */
    private String createBy;

    /** 更新者 */
    private String updateBy;
}
