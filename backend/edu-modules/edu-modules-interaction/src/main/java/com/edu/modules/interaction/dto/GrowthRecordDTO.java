package com.edu.modules.interaction.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class GrowthRecordDTO {
    private Long id;
    private Long studentId;
    private Long teacherId;
    private LocalDate recordDate;
    private String content;
    private Integer score;
    private LocalDateTime createdAt;
    // joined fields
    private String studentName;
    private String teacherName;
}
