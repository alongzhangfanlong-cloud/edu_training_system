package com.education.store.module.course.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 科目更新 DTO
 */
@Data
public class SubjectUpdateDTO {

    @NotNull(message = "科目ID不能为空")
    private Long id;

    private String name;

    private Integer sortOrder;

    private Integer status;

    private String description;
}
