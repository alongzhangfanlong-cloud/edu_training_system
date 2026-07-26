package com.education.store.module.course.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.education.store.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 课件/资料实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("edu_resource")
public class EduResource extends BaseEntity {

    /** 课件名称 */
    private String name;

    /** 关联科目ID → edu_subject.id */
    private Long subjectId;

    /** 适用年级 */
    private String grade;

    /** 资源类型(DOCUMENT/PPT/EXERCISE/OTHER) */
    private String resourceType;

    /** 原始文件名 */
    private String fileName;

    /** 存储路径 */
    private String filePath;

    /** 访问URL */
    private String fileUrl;

    /** 文件大小（字节） */
    private Long fileSize;

    /** MIME类型 */
    private String fileType;

    /** 共享范围(1公开 0私有) */
    private Integer isPublic;

    /** 上传人ID → sys_user.id */
    private Long uploadBy;

    /** 下载次数 */
    private Integer downloadCount;

    /** 状态(1正常 0禁用) */
    private Integer status;

    /** 描述 */
    private String description;

    /** 创建者 */
    private String createBy;

    /** 更新者 */
    private String updateBy;

    /** 科目名称（非数据库字段，关联查询填充） */
    @TableField(exist = false)
    private String subjectName;

    /** 上传人姓名（非数据库字段，关联查询填充） */
    @TableField(exist = false)
    private String uploadByName;
}
