package com.education.store.module.student.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学员 VO
 */
@Data
public class StudentVO {

    private Long id;
    private String name;
    private String gender;
    private LocalDate birthDate;
    private String grade;
    private String school;
    private String mobile;
    private Long counselorId;
    private String counselorName;
    private String source;
    private String tags;
    private String status;
    private BigDecimal totalHours;
    private BigDecimal remainingHours;
    private LocalDateTime lastVisitTime;
    private LocalDateTime completedAt;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
