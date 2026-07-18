package com.education.store.module.teaching.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.store.common.annotation.Log;
import com.education.store.common.enums.BusinessType;
import com.education.store.common.model.PageResult;
import com.education.store.common.model.Result;
import com.education.store.module.teaching.entity.EduEvaluation;
import com.education.store.module.teaching.entity.EduEvaluationStudent;
import com.education.store.module.teaching.service.IEduEvaluationService;
import com.education.store.module.teaching.service.IEduEvaluationStudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "教评学")
@RestController
@RequestMapping("/api/v1/teaching/evaluation")
@RequiredArgsConstructor
public class EvaluationController {

    private final IEduEvaluationService evaluationService;
    private final IEduEvaluationStudentService studentService;

    @Operation(summary = "评价列表")
    @PreAuthorize("@ss.hasPermi('teaching:evaluation:list')")
    @GetMapping("/list")
    public Result<PageResult<EduEvaluation>> list(@RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) Long scheduleId,
            @RequestParam(required = false) Long teacherId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Page<EduEvaluation> p = new Page<>(page, pageSize);
        IPage<EduEvaluation> result = evaluationService.selectEvaluationList(p, scheduleId, teacherId, null, startDate, endDate, status);
        return Result.success(PageResult.of(result));
    }

    @Operation(summary = "评价详情")
    @PreAuthorize("@ss.hasPermi('teaching:evaluation:list')")
    @GetMapping("/{id}")
    public Result<Map<String, Object>> detail(@PathVariable Long id) {
        EduEvaluation evaluation = evaluationService.getDetail(id);
        List<EduEvaluationStudent> students = studentService.selectByEvaluationId(id);
        return Result.success(Map.of("evaluation", evaluation, "students", students));
    }

    @Operation(summary = "暂存草稿")
    @Log(title = "教评学", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('teaching:evaluation:add')")
    @PostMapping("/draft")
    public Result<EduEvaluation> saveDraft(@RequestParam Long scheduleId,
            @RequestParam Long classId,
            @RequestParam Long teacherId,
            @RequestParam(required = false) String summary,
            @Valid @RequestBody(required = false) List<EduEvaluationStudent> students) {
        if (students == null) students = List.of();
        EduEvaluation evaluation = evaluationService.saveDraft(scheduleId, classId, teacherId, summary, students);
        return Result.success(evaluation);
    }

    @Operation(summary = "提交评价")
    @Log(title = "教评学", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('teaching:evaluation:add')")
    @PostMapping("/{id}/submit")
    public Result<EduEvaluation> submit(@PathVariable Long id,
            @RequestParam String summary,
            @RequestParam Long teacherId,
            @Valid @RequestBody List<EduEvaluationStudent> students) {
        EduEvaluation evaluation = evaluationService.submitEvaluation(id, summary, students, teacherId);
        return Result.success(evaluation);
    }

    @Operation(summary = "删除评价")
    @Log(title = "教评学", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('teaching:evaluation:remove')")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, @RequestParam(required = false) Long currentUserId) {
        evaluationService.deleteEvaluation(id, currentUserId);
        return Result.success();
    }
}
