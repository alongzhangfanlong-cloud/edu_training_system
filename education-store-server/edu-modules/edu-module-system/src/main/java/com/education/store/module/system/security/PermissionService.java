package com.education.store.module.system.security;

import cn.hutool.core.util.StrUtil;
import com.education.store.common.utils.SecurityUtils;
import com.education.store.module.system.mapper.SysMenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;

/**
 * 权限验证服务 - Spring Security @PreAuthorize 中使用
 * <p>
 * 用法示例：
 * <pre>
 * @PreAuthorize("@ss.hasPermi('system:user:list')")
 * @PreAuthorize("@ss.hasAnyPermi('system:user:add,system:user:edit')")
 * </pre>
 */
@Component("ss")
@RequiredArgsConstructor
public class PermissionService {

    private final SysMenuMapper menuMapper;

    /** 验证用户是否拥有指定权限 */
    public boolean hasPermi(String permission) {
        if (StrUtil.isBlank(permission)) {
            return false;
        }
        // 管理员拥有所有权限
        if (SecurityUtils.isAdmin()) {
            return true;
        }
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return false;
        }
        Set<String> permissions = menuMapper.selectMenuPermsByUserId(userId);
        return permissions != null && permissions.contains(permission);
    }

    /** 验证是否拥有任意一个权限（逗号分隔） */
    public boolean hasAnyPermi(String permissions) {
        if (StrUtil.isBlank(permissions)) {
            return false;
        }
        if (SecurityUtils.isAdmin()) {
            return true;
        }
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return false;
        }
        Set<String> userPerms = menuMapper.selectMenuPermsByUserId(userId);
        return Arrays.stream(permissions.split(","))
                .map(String::trim)
                .anyMatch(p -> userPerms != null && userPerms.contains(p));
    }
}
