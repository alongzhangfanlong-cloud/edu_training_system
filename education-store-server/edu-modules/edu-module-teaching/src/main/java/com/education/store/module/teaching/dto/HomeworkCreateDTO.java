package com.education.store.module.teaching.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 作业创建/编辑请求 DTO
 * 解决 Entity 直接接收请求体的 Mass Assignment 问题，同时支持附件 ID 传递
 */
@Data
public class HomeworkCreateDTO {

    @NotNull(message = "班级不能为空")
    private Long classId;

    @NotNull(message = "课程不能为空")
    private Long courseId;

    /** 关联排课记录 ID，可选 */
    private Long scheduleId;

    /** 作业标题，由后端按内容截取，前端可不传 */
    private String title;

    @NotBlank(message = "作业内容不能为空")
    private String content;

    @NotNull(message = "教师不能为空")
    private Long teacherId;

    /** 附件 ID 列表（已上传到文件服务的资源 ID） */
    private List<Long> attachmentIds;
}
