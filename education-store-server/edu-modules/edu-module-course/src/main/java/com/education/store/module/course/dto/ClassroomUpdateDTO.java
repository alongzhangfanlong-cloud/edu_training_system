package com.education.store.module.course.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 教室更新 DTO
 */
@Data
public class ClassroomUpdateDTO {

    @NotNull(message = "教室ID不能为空")
    private Long id;

    private String name;

    private Integer capacity;

    private String location;

    private String facilities;

    private Integer status;

    private String description;
}
