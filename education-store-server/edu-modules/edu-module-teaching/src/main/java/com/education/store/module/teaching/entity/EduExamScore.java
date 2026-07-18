package com.education.store.module.teaching.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.education.store.common.base.BaseEntity;
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
    private Long classId;

    /** 科目ID */
    private Long subjectId;

    /** 考试类型: MONTHLY/MIDTERM/FINAL/QUIZ/CUSTOM */
    private String examType;

    /** 自定义考试名称 */
    private String examName;

    /** 考试日期 */
    private LocalDate examDate;

    /** 满分分值 */
    private BigDecimal totalScore;

    /** 录入教师ID */
    private Long teacherId;
}
