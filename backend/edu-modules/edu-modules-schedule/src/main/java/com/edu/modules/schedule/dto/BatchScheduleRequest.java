package com.edu.modules.schedule.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class BatchScheduleRequest {
    @NotNull private Long classId;
    @NotNull private Long teacherId;
    @NotNull private Long classroomId;
    @NotNull private LocalDate startDate;
    @NotNull private LocalDate endDate;
    @NotNull private List<Integer> weekdays;
    @NotNull private LocalTime startTime;
    @NotNull private LocalTime endTime;
    private Boolean skipHolidays = false;
}
