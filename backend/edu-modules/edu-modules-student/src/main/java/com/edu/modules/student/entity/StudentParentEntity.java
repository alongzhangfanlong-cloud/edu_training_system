package com.edu.modules.student.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("student_parents")
public class StudentParentEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long studentId;
    private String parentName;
    private String relation;
    private String phone;
    private Integer isPrimary;
    private String wxOpenid;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
