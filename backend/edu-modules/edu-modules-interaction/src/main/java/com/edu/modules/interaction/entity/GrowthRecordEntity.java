package com.edu.modules.interaction.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("growth_records")
public class GrowthRecordEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private Long teacherId;
    private LocalDate recordDate;
    private String content;
    private Integer score;
    private Long campusId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
