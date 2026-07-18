package com.education.store.module.course.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 教室 VO
 */
@Data
public class ClassroomVO {

    private Long id;
    private String name;
    private Integer capacity;
    private String location;
    private String facilities;
    private Integer status;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
