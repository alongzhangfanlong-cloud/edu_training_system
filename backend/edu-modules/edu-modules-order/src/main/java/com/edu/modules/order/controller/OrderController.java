package com.edu.modules.order.controller;

import com.edu.common.core.response.ApiResult;
import com.edu.common.mybatis.PageResult;
import com.edu.modules.order.dto.CreateOrderRequest;
import com.edu.modules.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "报名订单", description = "订单创建、支付、查询")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "创建报名订单")
    public ApiResult<Map<String, Object>> create(@Valid @RequestBody CreateOrderRequest request) {
        return ApiResult.success(orderService.createOrder(request));
    }

    @PostMapping("/{id}/pay")
    @Operation(summary = "订单支付")
    public ApiResult<Map<String, Object>> pay(@PathVariable Long id,
                                               @RequestParam(defaultValue = "3") Integer payMethod,
                                               @RequestParam(required = false) String offlineRemark) {
        return ApiResult.success(orderService.pay(id, payMethod, offlineRemark));
    }

    @GetMapping("/{id}")
    @Operation(summary = "订单详情")
    public ApiResult<Map<String, Object>> detail(@PathVariable Long id) {
        return ApiResult.success(orderService.getDetail(id));
    }

    @GetMapping
    @Operation(summary = "订单列表")
    public ApiResult<PageResult<Map<String, Object>>> list(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Integer payStatus,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return ApiResult.success(orderService.list(studentId, payStatus, page, pageSize));
    }
}
