package com.edu.modules.student.dto;

import lombok.Data;

import java.util.List;

@Data
public class UpdateStudentRequest {
    private String name;
    private String phone;
    private Integer gender;
    private String grade;
    private String school;
    private List<String> tags;
    private String source;
    private String remark;
    private Integer status;
}
