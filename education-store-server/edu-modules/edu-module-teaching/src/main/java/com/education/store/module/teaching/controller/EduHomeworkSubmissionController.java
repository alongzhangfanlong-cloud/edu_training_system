package com.education.store.module.teaching.controller;

import com.education.store.common.annotation.Log;
import com.education.store.common.enums.BusinessType;
import com.education.store.common.model.Result;
import com.education.store.module.teaching.entity.EduHomeworkSubmission;
import com.education.store.module.teaching.service.IEduHomeworkSubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 作业提交 Controller
 */
@Tag(name = "作业提交管理")
@RestController
@RequestMapping("/api/v1/teaching/homework-submission")
@RequiredArgsConstructor
public class EduHomeworkSubmissionController {

    private final IEduHomeworkSubmissionService submissionService;

    @GetMapping("/list")
    @Operation(summary = "查询作业提交列表")
    @PreAuthorize("isAuthenticated()")
    public Result<?> list() {
        return Result.success(submissionService.list());
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询提交详情")
    @PreAuthorize("isAuthenticated()")
    public Result<EduHomeworkSubmission> getById(@PathVariable Long id) {
        return Result.success(submissionService.getById(id));
    }

    @PutMapping("/{id}/grade")
    @Operation(summary = "批改作业")
    @Log(title = "作业批改", businessType = BusinessType.UPDATE)
    @PreAuthorize("isAuthenticated()")
    public Result<?> grade(@PathVariable Long id, @Valid @RequestBody EduHomeworkSubmission submission) {
        submission.setId(id);
        return Result.success(submissionService.updateById(submission));
    }
}
