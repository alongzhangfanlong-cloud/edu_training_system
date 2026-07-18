package com.education.store.module.student.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * 学员更新 DTO（对齐 PRD §11.3 边界输入校验）
 */
@Data
public class StudentUpdateDTO {

    @NotNull(message = "学员ID不能为空")
    private Long id;

    @Size(max = 20, message = "姓名最多20字符")
    private String name;

    private String gender;

    private LocalDate birthDate;

    private String grade;

    @Size(max = 50, message = "学校名称最多50字符")
    private String school;

    @Pattern(regexp = "^1\\d{10}$", message = "手机号格式不正确")
    private String mobile;

    private Long counselorId;

    private String source;

    @Size(max = 255, message = "标签过长")
    private String tags;

    /** 状态变更请使用 PUT /{id}/status 端点（走状态机校验），此处保留仅用于兼容 */
    private String status;

    @Size(max = 500, message = "备注最多500字符")
    private String remark;
}
