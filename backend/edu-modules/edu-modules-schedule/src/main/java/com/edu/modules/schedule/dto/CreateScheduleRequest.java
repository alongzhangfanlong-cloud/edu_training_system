package com.edu.modules.schedule.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CreateScheduleRequest {
    @NotNull private Long classId;
    @NotNull private Long teacherId;
    @NotNull private Long classroomId;
    @NotNull private LocalDate date;
    @NotNull private LocalTime startTime;
    @NotNull private LocalTime endTime;
    private Integer lessonNo;
    private String remark;
}
