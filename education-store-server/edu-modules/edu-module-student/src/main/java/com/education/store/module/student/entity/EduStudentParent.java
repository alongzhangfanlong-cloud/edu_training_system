package com.education.store.module.student.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.education.store.common.base.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 家长账号关联实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("edu_student_parent")
public class EduStudentParent extends BaseEntity {

    /** 学员ID → edu_student.id */
    private Long studentId;

    /** 关联系统用户ID → sys_user.id（家长登录账号，可为空） */
    private Long userId;

    /** 家长姓名 */
    @NotBlank(message = "家长姓名不能为空")
    private String name;

    /** 亲属关系(FATHER/MOTHER/GRANDFATHER/GRANDMOTHER/GRANDFATHER_M/GRANDMOTHER_M/OTHER) */
    @NotBlank(message = "亲属关系不能为空")
    private String relation;

    /** 家长手机号 */
    @Pattern(regexp = "^(1[3-9]\\d{9})?$", message = "家长手机号格式不正确")
    private String mobile;

    /** 排序（决定展示顺序，越小越靠前） */
    private Integer sort;

    /** 创建者 */
    private String createBy;

    /** 更新者 */
    private String updateBy;
}
