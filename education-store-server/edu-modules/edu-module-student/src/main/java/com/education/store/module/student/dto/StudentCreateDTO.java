package com.education.store.module.student.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * 学员新增 DTO（对齐 PRD §11.3 边界输入校验）
 */
@Data
public class StudentCreateDTO {

    @NotBlank(message = "学员姓名不能为空")
    @Size(max = 20, message = "姓名最多20字符")
    private String name;

    private String gender;

    private LocalDate birthDate;

    private String grade;

    @Size(max = 50, message = "学校名称最多50字符")
    private String school;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1\\d{10}$", message = "手机号格式不正确")
    private String mobile;

    private Long counselorId;

    /** 家长姓名 */
    private String parentName;

    /** 亲属关系（FATHER/MOTHER/GRANDFATHER/GRANDMOTHER/GRANDFATHER_M/GRANDMOTHER_M/OTHER） */
    private String parentRelation;

    /** 微信昵称 */
    @Size(max = 50, message = "微信昵称最多50字符")
    private String wechatNickname;

    /** 报名课程ID → edu_course.id */
    private Long intendedCourseId;

    private String source;

    @Size(max = 255, message = "标签过长")
    private String tags;

    private String status;

    @Size(max = 500, message = "备注最多500字符")
    private String remark;
}
