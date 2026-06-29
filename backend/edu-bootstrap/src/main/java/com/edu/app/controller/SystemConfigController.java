package com.edu.app.controller;

import com.edu.common.core.response.ApiResult;
import com.edu.common.core.utils.SystemConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/system-configs")
@RequiredArgsConstructor
@Tag(name = "系统配置", description = "系统参数配置管理")
public class SystemConfigController {

    private final SystemConfigService configService;

    @GetMapping
    @Operation(summary = "获取所有配置")
    public ApiResult<List<Map<String, String>>> list() {
        List<Map<String, String>> configs = List.of(
            configItem("leave_free_hours", String.valueOf(configService.getLeaveFreeHours()), "免费请假提前小时数"),
            configItem("leave_charge_hours", String.valueOf(configService.getLeaveChargeHours()), "收费请假提前小时数"),
            configItem("checkin_qr_expire_minutes", String.valueOf(configService.getCheckinQrExpireMinutes()), "签到二维码有效期(分钟)"),
            configItem("checkin_distance_meters", String.valueOf(configService.getCheckinDistanceMeters()), "签到允许最大距离(米)"),
            configItem("hour_warning_threshold", String.valueOf(configService.getHourWarningThreshold()), "课时预警阈值"),
            configItem("makeup_valid_days", String.valueOf(configService.getMakeupValidDays()), "补课有效期(天)")
        );
        return ApiResult.success(configs);
    }

    @PutMapping("/{key}")
    @Operation(summary = "更新配置")
    public ApiResult<Void> update(@PathVariable String key, @RequestBody Map<String, String> body) {
        configService.update(key, body.get("value"));
        return ApiResult.success();
    }

    @GetMapping("/{key}")
    @Operation(summary = "获取单个配置")
    public ApiResult<Map<String, String>> get(@PathVariable String key) {
        String value = configService.getString(key);
        Map<String, String> result = new HashMap<>();
        result.put("key", key);
        result.put("value", value);
        return ApiResult.success(result);
    }

    private Map<String, String> configItem(String key, String value, String desc) {
        Map<String, String> item = new HashMap<>();
        item.put("key", key); item.put("value", value); item.put("description", desc);
        return item;
    }
}
