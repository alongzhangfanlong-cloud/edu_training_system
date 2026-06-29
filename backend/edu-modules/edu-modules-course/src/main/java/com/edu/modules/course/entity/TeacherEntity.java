package com.edu.modules.course.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("teachers")
public class TeacherEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String teacherNo;
    private String name;
    private String avatar;
    private String phone;
    private Integer gender;
    private String subjects;
    private String introduction;
    private LocalDate hireDate;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
