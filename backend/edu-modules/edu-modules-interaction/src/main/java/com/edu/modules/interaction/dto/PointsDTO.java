package com.edu.modules.interaction.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PointsDTO {
    private Long id;
    private Long studentId;
    private Integer balance;
    private LocalDateTime updatedAt;
    // joined fields
    private String studentName;
}
