package com.edu.modules.consume.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ConsumeRequest {
    @NotNull private Long attendanceId;
    private BigDecimal consumeHours;
}
