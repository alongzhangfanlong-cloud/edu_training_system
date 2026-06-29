package com.edu.modules.student.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("students")
public class StudentEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String studentNo;
    private String name;
    private String avatar;
    private Integer gender;
    private LocalDate birthday;
    private String grade;
    private String school;
    private String phone;
    private String tags; // JSON string
    private Integer status;
    private String source;
    private Long consultantId;
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer isDeleted;
}
