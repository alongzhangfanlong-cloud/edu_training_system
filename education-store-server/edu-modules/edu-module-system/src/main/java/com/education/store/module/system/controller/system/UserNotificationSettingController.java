package com.education.store.module.system.controller.system;

import com.education.store.common.model.Result;
import com.education.store.common.utils.SecurityUtils;
import com.education.store.module.system.service.IUserNotificationSettingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户通知偏好设置 Controller — 微信订阅消息开关
 */
@Tag(name = "通知设置")
@RestController
@RequestMapping("/api/v1/user/notification-settings")
@RequiredArgsConstructor
public class UserNotificationSettingController {

    private final IUserNotificationSettingService service;

    @Operation(summary = "获取当前用户通知偏好设置")
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public Result<Map<String, Boolean>> getSettings() {
        Long userId = SecurityUtils.getUserId();
        return Result.success(service.getSettings(userId));
    }

    @Operation(summary = "更新通知偏好设置")
    @PreAuthorize("isAuthenticated()")
    @PutMapping
    public Result<Void> updateSettings(@RequestBody Map<String, Boolean> settings) {
        Long userId = SecurityUtils.getUserId();
        service.updateSettings(userId, settings);
        return Result.success();
    }
}
