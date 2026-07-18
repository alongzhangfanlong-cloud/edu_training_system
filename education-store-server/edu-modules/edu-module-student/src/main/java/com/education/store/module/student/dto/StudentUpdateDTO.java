package com.education.store.module.student.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 学员更新 DTO
 */
@Data
public class StudentUpdateDTO {

    @NotNull(message = "学员ID不能为空")
    private Long id;

    private String name;

    private String gender;

    private LocalDate birthDate;

    private String grade;

    private String school;

    private String mobile;

    private Long counselorId;

    private String source;

    private String tags;

    private String status;

    private String remark;
}
