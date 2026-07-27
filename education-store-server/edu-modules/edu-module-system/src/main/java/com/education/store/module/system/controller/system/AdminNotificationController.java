package com.education.store.module.system.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.store.common.exception.BusinessException;
import com.education.store.common.model.PageResult;
import com.education.store.common.model.Result;
import com.education.store.common.utils.SecurityUtils;
import com.education.store.module.system.entity.system.SysRole;
import com.education.store.module.system.entity.system.SysUser;
import com.education.store.module.system.entity.system.UserNotification;
import com.education.store.module.system.mapper.SysRoleMapper;
import com.education.store.module.system.mapper.SysUserMapper;
import com.education.store.module.system.mapper.SysUserRoleMapper;
import com.education.store.module.system.service.UserNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 管理员消息通知管理 Controller
 */
@Slf4j
@Validated
@Tag(name = "消息通知管理")
@RestController
@RequestMapping("/api/v1/system/notification")
@RequiredArgsConstructor
public class AdminNotificationController {

    private static final int MAX_PAGE_SIZE = 100;
    private static final int MAX_TITLE_LENGTH = 100;
    private static final int MAX_CONTENT_LENGTH = 2000;

    private final UserNotificationService notificationService;
    private final SysUserMapper userMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final SysRoleMapper roleMapper;

    @Operation(summary = "发送系统通知给指定用户")
    @PostMapping("/send")
    public Result<Integer> sendToUser(@Valid @RequestBody SendNotificationReq req) {
        requireAdmin();
        validateContent(req.getTitle(), req.getContent());

        Long senderId = SecurityUtils.getUserId();
        int count = 0;
        if (req.getUserIds() != null && !req.getUserIds().isEmpty()) {
            for (Long userId : req.getUserIds()) {
                if (userId == null) continue;
                if (insertOne(senderId, userId, req)) count++;
            }
        }
        return Result.success(count);
    }

    @Operation(summary = "按角色群发系统通知(PARENT/TEACHER/ADMIN/ALL)")
    @PostMapping("/send-by-role")
    public Result<Integer> sendByRole(@Valid @RequestBody SendByRoleReq req) {
        requireAdmin();
        validateContent(req.getTitle(), req.getContent());

        Long senderId = SecurityUtils.getUserId();
        String role = req.getRole();
        if (role == null || role.isBlank()) {
            throw new BusinessException("角色参数不能为空");
        }

        Set<Long> userIds = new HashSet<>();
        if ("ALL".equalsIgnoreCase(role)) {
            LambdaQueryWrapper<SysUser> w = new LambdaQueryWrapper<>();
            w.eq(SysUser::getStatus, 1);
            userMapper.selectList(w).forEach(u -> {
                if (u != null && u.getId() != null) userIds.add(u.getId());
            });
        } else {
            String roleKey = role.toUpperCase();
            LambdaQueryWrapper<SysRole> rw = new LambdaQueryWrapper<>();
            rw.eq(SysRole::getCode, roleKey).eq(SysRole::getStatus, 1);
            SysRole sysRole = roleMapper.selectOne(rw);
            if (sysRole != null) {
                List<Long> uids = userRoleMapper.selectUserIdsByRoleId(sysRole.getId());
                if (uids != null) {
                    for (Long uid : uids) {
                        if (uid != null) userIds.add(uid);
                    }
                }
            } else {
                throw new BusinessException("角色不存在: " + role);
            }
        }

        int count = 0;
        for (Long uid : userIds) {
            if (uid == null) continue;
            SysUser u = userMapper.selectById(uid);
            if (u == null) continue;
            if (u.getStatus() != null && u.getStatus() != 1) continue;
            if (insertOne(senderId, uid, req.getTitle(), req.getContent(), "SYSTEM", null, null)) {
                count++;
            }
        }
        log.info("管理员发送系统通知: senderId={}, role={}, title={}, count={}",
                senderId, role, req.getTitle(), count);
        return Result.success(count);
    }

    @Operation(summary = "管理员查看所有通知列表(分页)")
    @GetMapping("/list")
    public Result<PageResult<UserNotification>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) String type) {
        requireAdmin();
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 10;
        if (pageSize > MAX_PAGE_SIZE) pageSize = MAX_PAGE_SIZE;

        Page<UserNotification> p = new Page<>(page, pageSize);
        LambdaQueryWrapper<UserNotification> wrapper = new LambdaQueryWrapper<>();
        if (type != null && !type.isBlank()) {
            wrapper.eq(UserNotification::getType, type);
        }
        wrapper.orderByDesc(UserNotification::getCreateTime);
        IPage<UserNotification> result = notificationService.page(p, wrapper);
        return Result.success(PageResult.of(result));
    }

    @Operation(summary = "管理员删除通知")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        requireAdmin();
        if (id == null) {
            throw new BusinessException("通知ID不能为空");
        }
        UserNotification n = notificationService.getById(id);
        if (n == null) {
            throw new BusinessException("通知不存在");
        }
        notificationService.removeById(id);
        log.info("管理员删除通知: id={}, operator={}", id, SecurityUtils.getUserId());
        return Result.success();
    }

    private void requireAdmin() {
        if (!SecurityUtils.isAdmin()) {
            throw new BusinessException("无权限操作，仅管理员可访问");
        }
    }

    private void validateContent(String title, String content) {
        if (title == null || title.isBlank()) {
            throw new BusinessException("通知标题不能为空");
        }
        if (title.length() > MAX_TITLE_LENGTH) {
            throw new BusinessException("通知标题不能超过" + MAX_TITLE_LENGTH + "个字符");
        }
        if (content == null || content.isBlank()) {
            throw new BusinessException("通知内容不能为空");
        }
        if (content.length() > MAX_CONTENT_LENGTH) {
            throw new BusinessException("通知内容不能超过" + MAX_CONTENT_LENGTH + "个字符");
        }
    }

    private boolean insertOne(Long senderId, Long userId, SendNotificationReq req) {
        String type = req.getType() != null ? req.getType() : "SYSTEM";
        return insertOne(senderId, userId, req.getTitle(), req.getContent(),
                type, req.getRelatedId(), req.getRelatedType());
    }

    private boolean insertOne(Long senderId, Long userId, String title, String content,
                              String type, Long relatedId, String relatedType) {
        if (userId == null) return false;
        try {
            UserNotification n = new UserNotification();
            n.setUserId(userId);
            n.setSenderId(senderId);
            n.setTitle(title);
            n.setContent(content);
            n.setType(type);
            n.setEventType(type);
            n.setRelatedId(relatedId);
            n.setRelatedType(relatedType);
            n.setIsRead(0);
            n.setStatus(1);
            notificationService.insertNotification(n);
            return true;
        } catch (Exception e) {
            log.error("写入系统通知失败: userId={}, type={}, title={}", userId, type, title, e);
            return false;
        }
    }

    @Data
    public static class SendNotificationReq {
        @NotEmpty(message = "用户ID列表不能为空")
        private List<Long> userIds;
        @NotBlank(message = "通知标题不能为空")
        @Size(max = 100, message = "通知标题不能超过100个字符")
        private String title;
        @NotBlank(message = "通知内容不能为空")
        @Size(max = 2000, message = "通知内容不能超过2000个字符")
        private String content;
        private String type = "SYSTEM";
        private Long relatedId;
        private String relatedType;
    }

    @Data
    public static class SendByRoleReq {
        @NotBlank(message = "角色不能为空")
        private String role; // PARENT/TEACHER/ADMIN/ALL
        @NotBlank(message = "通知标题不能为空")
        @Size(max = 100, message = "通知标题不能超过100个字符")
        private String title;
        @NotBlank(message = "通知内容不能为空")
        @Size(max = 2000, message = "通知内容不能超过2000个字符")
        private String content;
    }
}
