package com.education.store.module.teaching.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.store.common.annotation.Log;
import com.education.store.common.enums.BusinessType;
import com.education.store.common.model.PageResult;
import com.education.store.common.model.Result;
import com.education.store.module.teaching.entity.EduExamScore;
import com.education.store.module.teaching.entity.EduExamScoreStudent;
import com.education.store.module.teaching.service.IEduExamScoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "成绩管理")
@RestController
@RequestMapping("/api/v1/teaching/exam")
@RequiredArgsConstructor
public class ExamScoreController {

    private final IEduExamScoreService examService;

    @Operation(summary = "考试成绩列表")
    @PreAuthorize("@ss.hasPermi('teaching:exam:list')")
    @GetMapping("/list")
    public Result<PageResult<EduExamScore>> list(@RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) String examType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Page<EduExamScore> p = new Page<>(page, pageSize);
        IPage<EduExamScore> result = examService.selectExamList(p, classId, subjectId, examType, startDate, endDate);
        return Result.success(PageResult.of(result));
    }

    @Operation(summary = "考试详情（含学生成绩）")
    @PreAuthorize("@ss.hasPermi('teaching:exam:list')")
    @GetMapping("/{id}")
    public Result<EduExamScore> detail(@PathVariable Long id) {
        return Result.success(examService.getById(id));
    }

    @Operation(summary = "考试学生成绩列表")
    @PreAuthorize("@ss.hasPermi('teaching:exam:list')")
    @GetMapping("/{id}/students")
    public Result<List<EduExamScoreStudent>> students(@PathVariable Long id) {
        return Result.success(examService.getStudentsByExamId(id));
    }

    @Operation(summary = "录入成绩")
    @Log(title = "成绩管理", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('teaching:exam:add')")
    @PostMapping
    public Result<EduExamScore> add(@Valid @RequestBody EduExamScore exam, @RequestParam(required = false) List<EduExamScoreStudent> students) {
        if (students == null) students = List.of();
        examService.addExam(exam, students);
        return Result.success(exam);
    }

    @Operation(summary = "编辑成绩")
    @Log(title = "成绩管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('teaching:exam:edit')")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody EduExamScore exam,
            @RequestParam(required = false) List<EduExamScoreStudent> students) {
        exam.setId(id);
        if (students == null) students = List.of();
        examService.updateExam(exam, students);
        return Result.success();
    }

    @Operation(summary = "删除成绩")
    @Log(title = "成绩管理", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('teaching:exam:remove')")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, @RequestParam(required = false) Long currentUserId) {
        examService.deleteExam(id, currentUserId);
        return Result.success();
    }
}
