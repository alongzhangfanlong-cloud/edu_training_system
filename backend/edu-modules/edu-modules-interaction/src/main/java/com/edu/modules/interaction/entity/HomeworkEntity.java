package com.edu.modules.interaction.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("homework")
public class HomeworkEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long scheduleId;
    private Long classId;
    private Long teacherId;
    private String content;
    private Long campusId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
