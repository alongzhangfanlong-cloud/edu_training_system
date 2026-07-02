package com.edu.modules.interaction.controller;

import com.edu.common.core.response.ApiResult;
import com.edu.common.mybatis.PageResult;
import com.edu.modules.interaction.service.OperationLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/operation-logs")
@RequiredArgsConstructor
@Tag(name = "操作日志", description = "操作日志查询")
public class OperationLogController {

    private final OperationLogService operationLogService;

    @GetMapping
    @Operation(summary = "操作日志列表")
    public ApiResult<PageResult<Map<String, Object>>> list(
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ApiResult.success(operationLogService.list(module, action, keyword, page, pageSize));
    }
}
