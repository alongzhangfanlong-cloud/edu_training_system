package com.edu.modules.course.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class TeacherDTO {
    private Long id;
    private String teacherNo;
    @NotBlank(message = "教师姓名不能为空")
    private String name;
    private String avatar;
    @NotBlank(message = "手机号不能为空")
    private String phone;
    private Integer gender;
    private List<String> subjects;
    private String introduction;
    private LocalDate hireDate;
    private Integer status;
}
