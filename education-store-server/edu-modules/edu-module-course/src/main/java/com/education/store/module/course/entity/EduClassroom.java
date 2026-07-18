package com.education.store.module.course.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.education.store.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 教室实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("edu_classroom")
public class EduClassroom extends BaseEntity {

    /** 教室名称 */
    private String name;

    /** 容纳人数（0=不限） */
    private Integer capacity;

    /** 位置描述 */
    private String location;

    /** 设施配置（JSON数组字符串，如 ["投影仪","白板","空调"]） */
    private String facilities;

    /** 状态(1启用 0停用) */
    private Integer status;

    /** 描述 */
    private String description;

    /** 创建者 */
    private String createBy;

    /** 更新者 */
    private String updateBy;
}
