package com.edu.modules.interaction.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("birthday_greetings")
public class BirthdayGreetingEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private LocalDate greetingDate;
    private String message;
    private Long campusId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
