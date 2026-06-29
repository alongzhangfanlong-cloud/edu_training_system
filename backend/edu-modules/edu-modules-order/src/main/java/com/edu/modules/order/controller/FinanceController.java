package com.edu.modules.order.controller;

import com.edu.common.core.response.ApiResult;
import com.edu.modules.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/finance")
@RequiredArgsConstructor
@Tag(name = "财务管理", description = "营收统计、财务流水、教师课时费")
public class FinanceController {

    private final OrderService orderService;

    @GetMapping("/summary")
    @Operation(summary = "财务摘要")
    public ApiResult<Map<String, Object>> summary() {
        return ApiResult.success(orderService.financeSummary());
    }
}
