package com.edu.modules.interaction.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("points")
public class PointsEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private Integer balance;
    private Long campusId;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
