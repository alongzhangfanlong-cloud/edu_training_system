package com.education.store.common.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全工具类 - 获取当前登录用户信息
 */
public class SecurityUtils {

    private SecurityUtils() {}

    /** 获取当前登录用户ID */
    public static Long getUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Long uid) {
            return uid;
        }
        return null;
    }

    /** 获取当前登录用户ID的字符串形式（用于 createBy/updateBy 等字段填充） */
    public static String getUserIdAsString() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Long) {
            return String.valueOf(auth.getPrincipal());
        }
        return null;
    }

    /**
     * 获取当前登录用户名
     * @deprecated 当前 JWT 中 Principal 存储的是 userId，此方法实际返回 userId 字符串。
     * 请使用 {@link #getUserId()} 或 {@link #getUserIdAsString()} 以明确语义。
     */
    @Deprecated
    public static String getUsername() {
        return getUserIdAsString();
    }

    /** 判断是否为管理员（基于角色判断，备用硬编码 userId == 1） */
    public static boolean isAdmin() {
        Long userId = getUserId();
        if (userId == null) return false;
        // 优先检查角色
        Authentication auth = getAuthentication();
        if (auth != null && auth.getAuthorities() != null) {
            return auth.getAuthorities().stream()
                    .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        }
        // 降级：硬编码 userId == 1（向后兼容）
        return userId == 1L;
    }

    /** 获取认证对象 */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
