package com.edu.common.core.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * 系统配置服务 - Redis缓存 + MySQL持久化
 * 用于业务规则引擎的动态参数
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemConfigService {

    private final JdbcTemplate jdbcTemplate;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String CACHE_PREFIX = "config:";
    private static final long CACHE_TTL_MINUTES = 30;

    /**
     * 获取字符串配置
     */
    public String getString(String key) {
        String cacheKey = CACHE_PREFIX + key;
        String value = redisTemplate.opsForValue().get(cacheKey);
        if (value != null) return value;

        value = loadFromDb(key);
        if (value != null) {
            redisTemplate.opsForValue().set(cacheKey, value, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        }
        return value;
    }

    /**
     * 获取整数配置
     */
    public int getInt(String key, int defaultValue) {
        String value = getString(key);
        if (value == null || value.isEmpty()) return defaultValue;
        try { return Integer.parseInt(value); }
        catch (NumberFormatException e) { return defaultValue; }
    }

    /**
     * 获取小数配置
     */
    public BigDecimal getDecimal(String key, BigDecimal defaultValue) {
        String value = getString(key);
        if (value == null || value.isEmpty()) return defaultValue;
        try { return new BigDecimal(value); }
        catch (NumberFormatException e) { return defaultValue; }
    }

    /**
     * 刷新配置缓存
     */
    public void refresh(String key) {
        redisTemplate.delete(CACHE_PREFIX + key);
        getString(key); // 重新加载
    }

    /**
     * 更新配置
     */
    public void update(String key, String value) {
        jdbcTemplate.update(
            "INSERT INTO system_configs (config_key, config_value) VALUES (?, ?) " +
            "ON DUPLICATE KEY UPDATE config_value = ?",
            key, value, value);
        redisTemplate.delete(CACHE_PREFIX + key);
        log.info("系统配置更新: {} = {}", key, value);
    }

    private String loadFromDb(String key) {
        try {
            return jdbcTemplate.queryForObject(
                "SELECT config_value FROM system_configs WHERE config_key = ?",
                String.class, key);
        } catch (Exception e) {
            return null;
        }
    }

    // ===== 业务规则快捷方法 =====

    /** 免费请假提前小时数 (默认24h) */
    public int getLeaveFreeHours() {
        return getInt("leave_free_hours", 24);
    }

    /** 收费请假提前小时数 (默认2h) */
    public int getLeaveChargeHours() {
        return getInt("leave_charge_hours", 2);
    }

    /** 签到二维码有效期分钟数 (默认5分钟) */
    public int getCheckinQrExpireMinutes() {
        return getInt("checkin_qr_expire_minutes", 5);
    }

    /** 签到允许最大距离米数 (默认500米) */
    public int getCheckinDistanceMeters() {
        return getInt("checkin_distance_meters", 500);
    }

    /** 课时预警阈值 (默认5课时) */
    public int getHourWarningThreshold() {
        return getInt("hour_warning_threshold", 5);
    }

    /** 补课有效期天数 (默认30天) */
    public int getMakeupValidDays() {
        return getInt("makeup_valid_days", 30);
    }
}
