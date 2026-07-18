package com.education.store.module.course.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程 VO
 */
@Data
public class CourseVO {

    private Long id;
    private String name;
    private String code;
    private Long subjectId;
    private String subjectName;
    private String courseType;
    private BigDecimal price;
    private String applicableGrade;
    private String coverImage;
    private Integer status;
    private Integer sortOrder;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
