package com.edu.modules.student.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CreateStudentRequest {

    @NotBlank(message = "学员姓名不能为空")
    @Size(max = 50)
    private String name;

    private String phone;
    private Integer gender;
    private LocalDate birthday;
    private String grade;
    private String school;
    private List<String> tags;
    private String source;
    private String remark;
    private List<ParentInfo> parents;

    @Data
    public static class ParentInfo {
        private String name;
        private String relation;
        private String phone;
        private Boolean isPrimary;
    }
}
