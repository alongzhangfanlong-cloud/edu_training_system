package com.education.store.module.course.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.education.store.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 科目实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("edu_subject")
public class EduSubject extends BaseEntity {

    /** 科目名称 */
    private String name;

    /** 排序号 */
    private Integer sortOrder;

    /** 状态(1启用 0停用) */
    private Integer status;

    /** 描述 */
    private String description;

    /** 创建者 */
    private String createBy;

    /** 更新者 */
    private String updateBy;
}
