package com.education.store.module.teaching.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.store.common.annotation.Log;
import com.education.store.common.enums.BusinessType;
import com.education.store.common.model.PageResult;
import com.education.store.common.model.Result;
import com.education.store.module.teaching.entity.EduTeacherEvaluation;
import com.education.store.module.teaching.service.IEduTeacherEvaluationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "学评教")
@RestController
@RequestMapping("/api/v1/teaching/teacher-evaluation")
@RequiredArgsConstructor
public class TeacherEvaluationController {

    private final IEduTeacherEvaluationService teacherEvalService;

    @Operation(summary = "评价明细列表")
    @PreAuthorize("@ss.hasPermi('teaching:teacher-evaluation:list')")
    @GetMapping("/list")
    public Result<PageResult<EduTeacherEvaluation>> list(@RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) Long teacherId,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Page<EduTeacherEvaluation> p = new Page<>(page, pageSize);
        IPage<EduTeacherEvaluation> result = teacherEvalService.selectList(p, teacherId, studentId, status, startDate, endDate);
        return Result.success(PageResult.of(result));
    }

    @Operation(summary = "评价详情")
    @PreAuthorize("@ss.hasPermi('teaching:teacher-evaluation:list')")
    @GetMapping("/{id}")
    public Result<EduTeacherEvaluation> detail(@PathVariable Long id) {
        return Result.success(teacherEvalService.getById(id));
    }

    @Operation(summary = "提交学评教")
    @Log(title = "学评教", businessType = BusinessType.INSERT)
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public Result<EduTeacherEvaluation> submit(@Valid @RequestBody EduTeacherEvaluation evaluation) {
        teacherEvalService.submit(evaluation);
        return Result.success(evaluation);
    }

    @Operation(summary = "隐藏评价")
    @Log(title = "学评教", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('teaching:teacher-evaluation:export')")
    @PostMapping("/{id}/hide")
    public Result<Void> hide(@PathVariable Long id) {
        teacherEvalService.hideEvaluation(id);
        return Result.success();
    }

    @Operation(summary = "恢复评价")
    @Log(title = "学评教", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('teaching:teacher-evaluation:export')")
    @PostMapping("/{id}/restore")
    public Result<Void> restore(@PathVariable Long id) {
        teacherEvalService.restoreEvaluation(id);
        return Result.success();
    }

    @Operation(summary = "教师评价统计（teacherId=0 获取全部教师概览）")
    @PreAuthorize("@ss.hasPermi('teaching:teacher-evaluation:list')")
    @GetMapping("/stats/teacher/{teacherId}")
    public Result<Map<String, Object>> stats(@PathVariable Long teacherId) {
        if (teacherId == 0) {
            return Result.success(teacherEvalService.getOverviewStats());
        }
        return Result.success(teacherEvalService.getTeacherStats(teacherId));
    }
}
