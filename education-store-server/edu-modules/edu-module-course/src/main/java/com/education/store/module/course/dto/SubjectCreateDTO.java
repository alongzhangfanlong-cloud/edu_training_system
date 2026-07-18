package com.education.store.module.course.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 科目新增 DTO
 */
@Data
public class SubjectCreateDTO {

    @NotBlank(message = "科目名称不能为空")
    private String name;

    private Integer sortOrder;

    private Integer status;

    private String description;
}
