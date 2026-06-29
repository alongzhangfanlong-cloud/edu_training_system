package com.edu.modules.student.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class HourTransactionDTO {
    private Long id;
    private Integer type;
    private String typeName;
    private BigDecimal changeHours;
    private BigDecimal beforeHours;
    private BigDecimal afterHours;
    private String className;
    private LocalDate scheduleDate;
    private String remark;
    private LocalDateTime createdAt;
}
