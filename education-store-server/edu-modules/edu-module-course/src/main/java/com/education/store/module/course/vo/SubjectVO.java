package com.education.store.module.course.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 科目 VO
 */
@Data
public class SubjectVO {

    private Long id;
    private String name;
    private Integer sortOrder;
    private Integer status;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
