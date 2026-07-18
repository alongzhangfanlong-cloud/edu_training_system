package com.education.store.module.course.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 课件新增 DTO — 防止 Mass Assignment 漏洞
 */
@Data
public class ResourceCreateDTO {

    @NotBlank(message = "课件名称不能为空")
    private String name;

    @NotNull(message = "所属科目不能为空")
    private Long subjectId;

    /** 适用年级 */
    private String grade;

    /** 资源类型(DOCUMENT/PPT/EXERCISE/OTHER)，默认 DOCUMENT */
    private String resourceType;

    /** 共享范围(1公开 0私有)，默认 1 */
    private Integer isPublic;

    /** 状态(1正常 0禁用)，默认 1 */
    private Integer status;

    /** 描述 */
    private String description;
}
