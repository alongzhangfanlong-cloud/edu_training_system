package com.education.store.module.teaching.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.education.store.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 课后作业实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("edu_homework")
public class EduHomework extends BaseEntity {

    /** 关联班级ID */
    private Long classId;

    /** 关联课程ID */
    private Long courseId;

    /** 关联排课记录ID */
    private Long scheduleId;

    /** 作业标题 */
    private String title;

    /** 作业内容 */
    private String content;

    /** 布置教师ID */
    private Long teacherId;

    /** 是否被编辑更新过: 0=否, 1=是 */
    private Integer isUpdated;
}
