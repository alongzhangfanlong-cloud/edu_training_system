package com.edu.modules.attendance.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class BatchCheckInRequest {
    @NotNull private Long scheduleId;
    @NotNull private List<Attendee> attendances;

    @Data
    public static class Attendee {
        @NotNull private Long studentId;
        @NotNull private Integer status; // 1-出勤 2-请假 3-旷课
        private String remark;
    }
}
