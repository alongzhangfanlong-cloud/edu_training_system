package com.education.store.module.system.controller.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.store.common.model.PageResult;
import com.education.store.common.model.Result;
import com.education.store.common.utils.SecurityUtils;
import com.education.store.module.system.entity.system.UserNotification;
import com.education.store.module.system.service.UserNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户消息通知控制器
 */
@Tag(name = "用户消息通知")
@RestController
@RequestMapping("/api/v1/user/notifications")
@RequiredArgsConstructor
public class UserNotificationController {

    private static final int MAX_PAGE_SIZE = 100;

    private final UserNotificationService notificationService;

    @Operation(summary = "获取当前用户的通知列表")
    @GetMapping
    public Result<PageResult<UserNotification>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) String type) {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return Result.unauthorized("用户未登录");
        }
        page = Math.max(1, page);
        pageSize = normalizePageSize(pageSize);
        Page<UserNotification> p = new Page<>(page, pageSize);
        IPage<UserNotification> result = notificationService.selectNotificationList(p, userId, type);
        return Result.success(PageResult.of(result));
    }

    @Operation(summary = "获取通知详情")
    @GetMapping("/{id}")
    public Result<UserNotification> detail(@PathVariable Long id) {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return Result.unauthorized("用户未登录");
        }
        if (id == null) {
            return Result.badRequest("通知ID不能为空");
        }
        UserNotification notification = notificationService.selectNotificationById(id);
        if (notification == null) {
            return Result.notFound("通知不存在");
        }
        // 只能查看自己的通知
        if (!userId.equals(notification.getUserId())) {
            return Result.forbidden("无权查看此通知");
        }
        return Result.success(notification);
    }

    @Operation(summary = "标记单条通知为已读")
    @PutMapping("/{id}/read")
    public Result<Void> markAsRead(@PathVariable Long id) {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return Result.unauthorized("用户未登录");
        }
        if (id == null) {
            return Result.badRequest("通知ID不能为空");
        }
        int rows = notificationService.markAsRead(id, userId);
        if (rows > 0) {
            return Result.success();
        }
        return Result.error("标记失败，通知不存在或无权操作");
    }

    @Operation(summary = "标记所有通知为已读")
    @PutMapping("/read-all")
    public Result<Void> markAllAsRead() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return Result.unauthorized("用户未登录");
        }
        notificationService.markAllAsRead(userId);
        return Result.success();
    }

    @Operation(summary = "获取未读消息数量")
    @GetMapping("/unread-count")
    public Result<Map<String, Object>> unreadCount() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return Result.unauthorized("用户未登录");
        }
        Map<String, Object> result = notificationService.getUnreadCount(userId);
        return Result.success(result);
    }

    private long normalizePageSize(long pageSize) {
        if (pageSize < 1) return 10;
        if (pageSize > MAX_PAGE_SIZE) return MAX_PAGE_SIZE;
        return pageSize;
    }
}
