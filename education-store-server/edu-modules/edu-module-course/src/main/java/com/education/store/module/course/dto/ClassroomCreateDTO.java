package com.education.store.module.course.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 教室新增 DTO
 */
@Data
public class ClassroomCreateDTO {

    @NotBlank(message = "教室名称不能为空")
    private String name;

    private Integer capacity;

    private String location;

    private String facilities;

    private Integer status;

    private String description;
}
