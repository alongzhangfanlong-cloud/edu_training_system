package com.edu.common.security;

/**
 * ThreadLocal 持有当前请求用户信息
 */
public class UserContextHolder {

    private static final ThreadLocal<UserPrincipal> CONTEXT = new ThreadLocal<>();

    public static void set(UserPrincipal user) {
        CONTEXT.set(user);
    }

    public static UserPrincipal get() {
        return CONTEXT.get();
    }

    public static Long getUserId() {
        UserPrincipal user = CONTEXT.get();
        return user != null ? user.getUserId() : null;
    }

    public static String getRole() {
        UserPrincipal user = CONTEXT.get();
        return user != null ? user.getRole() : null;
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
