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

    /** 获取当前登录用户名 */
    public static String getUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Long) {
            return String.valueOf(auth.getPrincipal());
        }
        return null;
    }

    /** 判断是否为管理员 (userId == 1) */
    public static boolean isAdmin() {
        Long userId = getUserId();
        return userId != null && userId == 1L;
    }

    /** 获取认证对象 */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
