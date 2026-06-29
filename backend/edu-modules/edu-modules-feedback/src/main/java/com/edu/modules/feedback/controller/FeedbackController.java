package com.edu.modules.feedback.controller;

import com.edu.common.core.response.ApiResult;
import com.edu.modules.feedback.dto.FeedbackRequest;
import com.edu.modules.feedback.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/feedbacks")
@RequiredArgsConstructor
@Tag(name = "课堂反馈", description = "课堂反馈提交与查看")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    @Operation(summary = "提交课堂反馈")
    public ApiResult<Long> submit(@Valid @RequestBody FeedbackRequest request) {
        Long id = feedbackService.submit(request);
        return ApiResult.success("提交成功", id);
    }

    @GetMapping
    @Operation(summary = "课堂反馈列表")
    public ApiResult<?> list() {
        return ApiResult.success(feedbackService.listAll());
    }

    @GetMapping("/schedule/{scheduleId}")
    @Operation(summary = "查看课堂反馈")
    public ApiResult<?> getBySchedule(@PathVariable Long scheduleId) {
        return ApiResult.success(feedbackService.getByScheduleId(scheduleId));
    }

    @GetMapping("/schedule/{scheduleId}/student/{studentId}")
    @Operation(summary = "查看学员个人反馈")
    public ApiResult<?> getStudentFeedback(@PathVariable Long scheduleId,
                                            @PathVariable Long studentId) {
        return ApiResult.success(feedbackService.getStudentFeedback(scheduleId, studentId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除课堂反馈")
    public ApiResult<Void> delete(@PathVariable Long id) {
        feedbackService.delete(id);
        return ApiResult.success();
    }
}
