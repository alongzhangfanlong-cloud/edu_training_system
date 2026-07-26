package com.education.store.module.teaching.entity;

import com.baomidou.mybatisplus.annotation.TableField;
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
 * 考试成绩主实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("edu_exam_score")
public class EduExamScore extends BaseEntity {

    /** 班级ID */
    @NotNull(message = "班级ID不能为空")
    private Long classId;

    /** 科目ID */
    @NotNull(message = "科目ID不能为空")
    private Long subjectId;

    /** 考试类型: MONTHLY/MIDTERM/FINAL/QUIZ/CUSTOM */
    @NotBlank(message = "考试类型不能为空")
    private String examType;

    /** 自定义考试名称 */
    @NotBlank(message = "考试名称不能为空")
    private String examName;

    /** 考试日期 */
    private LocalDate examDate;

    /** 满分分值 */
    @NotNull(message = "满分分值不能为空")
    @Positive(message = "满分分值必须大于0")
    private BigDecimal totalScore;

    /** 录入教师ID */
    @NotNull(message = "教师ID不能为空")
    private Long teacherId;

    @TableField(exist = false)
    private String className;

    @TableField(exist = false)
    private String subjectName;

    @TableField(exist = false)
    private String teacherName;
}
