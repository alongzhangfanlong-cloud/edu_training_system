package com.edu.modules.schedule.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ConflictCheckRequest {
    private Long teacherId;
    private Long classroomId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Long excludeScheduleId;
}
