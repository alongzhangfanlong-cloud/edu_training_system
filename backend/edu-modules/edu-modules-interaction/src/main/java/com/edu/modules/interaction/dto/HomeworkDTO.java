package com.edu.modules.interaction.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class HomeworkDTO {
    private Long id;
    private Long scheduleId;
    private Long classId;
    private Long teacherId;
    private String content;
    private LocalDateTime createdAt;
    // joined fields
    private String className;
    private String courseName;
    private String teacherName;
}
