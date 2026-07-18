package com.education.store.security.aspect;

import com.education.store.common.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 登录限流 AOP 切面
 * <p>
 * 使用 Redis 滑动窗口计数，限制同一 IP 的登录频率。
 * 默认：同一 IP 每分钟最多 5 次登录尝试，超限抛出 BusinessException。
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    private static final String RATE_LIMIT_KEY_PREFIX = "rate_limit:login:";
    private static final int MAX_ATTEMPTS = 5;
    private static final int WINDOW_SECONDS = 60;

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 拦截 AuthController.login 方法进行登录限流
     */
    @Around("execution(* com.education.store.module.system.controller.AuthController.login(..))")
    public Object loginRateLimit(ProceedingJoinPoint point) throws Throwable {
        String ip = getClientIp();

        String key = RATE_LIMIT_KEY_PREFIX + ip;
        long now = Instant.now().getEpochSecond();
        long windowStart = now - WINDOW_SECONDS;

        // 使用 Redis 有序集合实现滑动窗口
        // 1. 移除窗口外的旧记录
        stringRedisTemplate.opsForZSet().removeRangeByScore(key, 0, windowStart);
        // 2. 统计当前窗口内的请求次数
        Long count = stringRedisTemplate.opsForZSet().count(key, windowStart, now);

        if (count != null && count >= MAX_ATTEMPTS) {
            log.warn("登录限流触发，IP: {}, 当前窗口内次数: {}", ip, count);
            throw new BusinessException("登录尝试过于频繁，请1分钟后再试");
        }

        // 3. 记录本次请求
        stringRedisTemplate.opsForZSet().add(key, String.valueOf(now), now);
        // 4. 设置 Key 过期时间（比窗口略长，避免 Key 残留）
        stringRedisTemplate.expire(key, WINDOW_SECONDS + 10, TimeUnit.SECONDS);

        return point.proceed();
    }

    /**
     * 获取客户端真实 IP
     * <p>
     * 考虑反向代理场景，优先从 X-Forwarded-For 和 X-Real-IP 头获取。
     */
    private String getClientIp() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes)
                    Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();

            // X-Forwarded-For: client, proxy1, proxy2 → 取第一个
            String ip = request.getHeader("X-Forwarded-For");
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                int idx = ip.indexOf(',');
                if (idx > 0) {
                    ip = ip.substring(0, idx).trim();
                }
                return ip;
            }

            // X-Real-IP
            ip = request.getHeader("X-Real-IP");
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip;
            }

            // 降级到直接 IP
            return request.getRemoteAddr();
        } catch (Exception e) {
            log.debug("获取客户端 IP 失败: {}", e.getMessage());
            return "unknown";
        }
    }
}
