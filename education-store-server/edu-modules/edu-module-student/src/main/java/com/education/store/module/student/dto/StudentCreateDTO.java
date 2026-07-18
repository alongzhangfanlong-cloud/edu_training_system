package com.education.store.module.student.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

/**
 * 学员新增 DTO
 */
@Data
public class StudentCreateDTO {

    @NotBlank(message = "学员姓名不能为空")
    private String name;

    private String gender;

    private LocalDate birthDate;

    private String grade;

    private String school;

    @NotBlank(message = "手机号不能为空")
    private String mobile;

    private Long counselorId;

    private String source;

    private String tags;

    private String status;

    private String remark;
}
