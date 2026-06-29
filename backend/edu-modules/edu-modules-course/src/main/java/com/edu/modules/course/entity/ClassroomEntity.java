package com.edu.modules.course.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("classrooms")
public class ClassroomEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Integer capacity;
    private String location;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String facilities;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
