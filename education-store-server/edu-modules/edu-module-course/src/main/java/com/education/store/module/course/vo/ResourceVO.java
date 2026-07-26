package com.education.store.module.course.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 课件/资料 VO（不暴露文件物理路径 filePath）
 */
@Data
public class ResourceVO {

    private Long id;
    private String name;
    private Long subjectId;
    private String subjectName;
    private String grade;
    private String resourceType;
    private String fileName;
    private String fileUrl;
    private Long fileSize;
    private String fileType;
    private Integer isPublic;
    private Long uploadBy;
    private String uploadByName;
    private Integer downloadCount;
    private Integer status;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
