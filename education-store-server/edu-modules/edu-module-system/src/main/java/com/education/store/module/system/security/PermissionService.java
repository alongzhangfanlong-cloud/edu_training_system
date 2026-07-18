package com.education.store.module.system.security;

import cn.hutool.core.util.StrUtil;
import com.education.store.common.constant.Constants;
import com.education.store.common.utils.SecurityUtils;
import com.education.store.module.system.mapper.SysMenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 获取用户权限集合（Redis优先，DB兜底并回写缓存）
     */
    private Set<String> getUserPermissions(Long userId) {
        String permKey = Constants.REDIS_KEY_PERM + userId;
        Set<String> cached = stringRedisTemplate.opsForSet().members(permKey);
        if (cached != null && !cached.isEmpty()) {
            return cached;
        }
        // Redis未命中，查数据库并回写缓存
        Set<String> permissions = menuMapper.selectMenuPermsByUserId(userId);
        if (permissions != null && !permissions.isEmpty()) {
            stringRedisTemplate.opsForSet().add(permKey, permissions.toArray(new String[0]));
            stringRedisTemplate.expire(permKey, 30, TimeUnit.MINUTES);
        }
        return permissions;
    }

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
        Set<String> permissions = getUserPermissions(userId);
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
        Set<String> userPerms = getUserPermissions(userId);
        return Arrays.stream(permissions.split(","))
                .map(String::trim)
                .anyMatch(p -> userPerms != null && userPerms.contains(p));
    }
}
