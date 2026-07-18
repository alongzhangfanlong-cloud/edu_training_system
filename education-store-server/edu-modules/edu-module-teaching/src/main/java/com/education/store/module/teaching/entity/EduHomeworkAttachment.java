package com.education.store.module.teaching.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 作业附件实体
 */
@Data
@TableName("edu_homework_attachment")
public class EduHomeworkAttachment implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联作业ID */
    private Long homeworkId;

    /** 原始文件名 */
    private String fileName;

    /** 文件存储URL */
    private String fileUrl;

    /** 文件大小（字节） */
    private Long fileSize;

    /** 文件类型: IMAGE/DOCUMENT */
    private String fileType;

    /** MIME类型 */
    private String mimeType;

    /** 排序号 */
    private Integer sortOrder;

    /** 创建时间 */
    private LocalDateTime createTime;
}
