package com.education.store.module.teaching.dto;

import com.education.store.module.teaching.entity.EduExamScoreStudent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 成绩录入/编辑请求 DTO
 * 将考试信息与学生成绩列表合并为一个请求体，解决 @RequestParam 无法绑定复杂 List 的问题
 */
@Data
public class ExamCreateDTO {

    @NotBlank(message = "考试名称不能为空")
    private String examName;

    @NotBlank(message = "考试类型不能为空")
    private String examType;

    @NotNull(message = "班级不能为空")
    private Long classId;

    @NotNull(message = "科目不能为空")
    private Long subjectId;

    @NotNull(message = "满分不能为空")
    @Positive(message = "满分必须大于0")
    private BigDecimal totalScore;

    @NotNull(message = "考试日期不能为空")
    private LocalDate examDate;

    @NotNull(message = "教师不能为空")
    private Long teacherId;

    /** 学生成绩列表 */
    private List<EduExamScoreStudent> students;
}
