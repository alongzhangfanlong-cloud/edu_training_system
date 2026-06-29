package com.edu.modules.feedback.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("class_feedbacks")
public class ClassFeedbackEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long scheduleId;
    private Long classId;
    private Long teacherId;
    private String content;
    private String homework;
    private String photos;
    private String overallComment;
    private String nextPreview;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
