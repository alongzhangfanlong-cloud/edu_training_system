package com.education.store.module.course.dto;

import lombok.Data;

/**
 * 课件更新 DTO — 防止 Mass Assignment 漏洞
 */
@Data
public class ResourceUpdateDTO {

    /** 课件名称 */
    private String name;

    /** 所属科目ID */
    private Long subjectId;

    /** 适用年级 */
    private String grade;

    /** 资源类型(DOCUMENT/PPT/EXERCISE/OTHER) */
    private String resourceType;

    /** 共享范围(1公开 0私有) */
    private Integer isPublic;

    /** 状态(1正常 0禁用) */
    private Integer status;

    /** 描述 */
    private String description;
}
