package com.education.store.module.teaching.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.education.store.common.base.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 教师档案实体
 *
 * @see com.education.store.module.teaching.mapper.EduTeacherMapper
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("edu_teacher")
public class EduTeacher extends BaseEntity {

    /** 关联系统用户ID → sys_user.id */
    private Long userId;

    /** 教师姓名 */
    @NotBlank(message = "教师姓名不能为空")
    private String realName;

    /** 在职状态: ACTIVE / INACTIVE */
    private String status;

    /** 可授科目（逗号分隔的科目名称或JSON数组） */
    private String subjects;

    /** 入职日期 */
    private LocalDate hireDate;

    /** 默认课酬单价（元/节） */
    @NotNull(message = "课酬单价不能为空")
    @Positive(message = "课酬单价必须大于0")
    private BigDecimal defaultPayPerLesson;

    /** 联系电话 */
    @NotBlank(message = "联系电话不能为空")
    private String phone;

    /** 备注 */
    private String remark;
}
