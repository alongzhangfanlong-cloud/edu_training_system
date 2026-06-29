package com.edu.modules.feedback.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("student_feedbacks")
public class StudentFeedbackEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long scheduleId;
    private Long studentId;
    private Long teacherId;
    private Integer performanceScore;
    private String performanceComment;
    private Integer homeworkStatus;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
