package com.edu.modules.student.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StudentListItemDTO {
    private Long id;
    private String studentNo;
    private String name;
    private String avatar;
    private String grade;
    private String phone;
    private Integer status;
    private Integer totalCourses;
    private BigDecimal totalHours;
    private BigDecimal remainHours;
}
