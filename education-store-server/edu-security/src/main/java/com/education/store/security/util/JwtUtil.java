package com.education.store.security.util;

import com.education.store.common.constant.Constants;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

/**
 * JWT 工具类
 */
@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret:Education-Store-Secret-Key-2026-For-JWT-Token-Generation-Must-Be-Long-Enough}")
    private String secret;

    @Value("${jwt.access-token-ttl:7200}")
    private long accessTokenTtl;  // 秒, 默认2小时

    @Value("${jwt.refresh-token-ttl:604800}")
    private long refreshTokenTtl; // 秒, 默认7天

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /** 生成 AccessToken */
    public String generateAccessToken(Long userId, String username) {
        return buildToken(userId, username, Constants.TOKEN_TYPE_ACCESS, accessTokenTtl * 1000);
    }

    /** 生成 RefreshToken */
    public String generateRefreshToken(Long userId, String username) {
        return buildToken(userId, username, Constants.TOKEN_TYPE_REFRESH, refreshTokenTtl * 1000);
    }

    private String buildToken(Long userId, String username, String type, long ttlMillis) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim(Constants.CLAIM_USER_ID, userId)
                .claim(Constants.CLAIM_USERNAME, username)
                .claim(Constants.CLAIM_TOKEN_TYPE, type)
                .id(UUID.randomUUID().toString())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + ttlMillis))
                .signWith(secretKey)
                .compact();
    }

    /** 解析 Token Claims */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token已过期", e);
        } catch (JwtException e) {
            throw new RuntimeException("Token解析失败", e);
        }
    }

    /** 从Token获取用户ID */
    public Long getUserId(String token) {
        return parseToken(token).get(Constants.CLAIM_USER_ID, Long.class);
    }

    /** 从Token获取用户名 */
    public String getUsername(String token) {
        return parseToken(token).get(Constants.CLAIM_USERNAME, String.class);
    }

    /** 验证Token是否有效 */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            log.debug("Token验证失败: {}", e.getMessage());
            return false;
        }
    }

    /** 判断是否为 AccessToken */
    public boolean isAccessToken(String token) {
        return Constants.TOKEN_TYPE_ACCESS.equals(
                parseToken(token).get(Constants.CLAIM_TOKEN_TYPE, String.class));
    }
}
