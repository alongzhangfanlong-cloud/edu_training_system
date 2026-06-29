package com.edu.modules.consume.controller;

import com.edu.common.core.response.ApiResult;
import com.edu.modules.consume.dto.ConsumeRequest;
import com.edu.modules.consume.service.ConsumeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/consume")
@RequiredArgsConstructor
@Tag(name = "消课管理", description = "课时核销、回滚")
public class ConsumeController {

    private final ConsumeService consumeService;

    @GetMapping
    @Operation(summary = "消课记录列表")
    public ApiResult<?> list(@RequestParam(required = false) Long studentId) {
        return ApiResult.success(consumeService.list(studentId));
    }

    @PostMapping("/calculate")
    @Operation(summary = "计算消课规则")
    public ApiResult<Map<String, Object>> calculate(
            @RequestParam Long studentId,
            @RequestParam Long scheduleId,
            @RequestParam Integer attendanceStatus) {
        return ApiResult.success(consumeService.calculate(studentId, scheduleId, attendanceStatus));
    }

    @PostMapping("/execute")
    @Operation(summary = "执行消课")
    public ApiResult<Map<String, Object>> execute(@Valid @RequestBody ConsumeRequest request) {
        return ApiResult.success(consumeService.execute(request));
    }

    @PostMapping("/rollback")
    @Operation(summary = "消课回滚")
    public ApiResult<Map<String, Object>> rollback(@RequestParam Long attendanceId,
                                                    @RequestParam(required = false) String reason) {
        return ApiResult.success(consumeService.rollback(attendanceId, reason));
    }
}
