package com.edu.common.security;

import cn.hutool.core.util.StrUtil;
import com.edu.common.core.response.ApiResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String BLACKLIST_PREFIX = "token:blacklist:";

    // Paths that don't require authentication
    private static final String[] WHITE_LIST = {
            "/api/v1/auth/login",
            "/api/v1/auth/send-sms",
            "/api/v1/auth/refresh",
            "/swagger-ui",
            "/v3/api-docs",
            "/doc.html",
            "/webjars",
            "/api/v1/health",
            "/health"
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        // Skip white-listed paths
        for (String whitePath : WHITE_LIST) {
            if (path.startsWith(whitePath)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        String authHeader = request.getHeader("Authorization");
        if (StrUtil.isBlank(authHeader) || !authHeader.startsWith(TOKEN_PREFIX)) {
            writeUnauthorized(response, "未提供有效的认证令牌");
            return;
        }

        String token = authHeader.substring(TOKEN_PREFIX.length());

        // Check blacklist
        String blacklistKey = BLACKLIST_PREFIX + token;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(blacklistKey))) {
            writeUnauthorized(response, "令牌已失效");
            return;
        }

        if (!jwtTokenProvider.validateToken(token)) {
            writeUnauthorized(response, "令牌无效或已过期");
            return;
        }

        UserPrincipal user = jwtTokenProvider.parseToken(token);
        UserContextHolder.set(user);

        try {
            filterChain.doFilter(request, response);
        } finally {
            UserContextHolder.clear();
        }
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        ApiResult<Void> result = ApiResult.error(40102, message);
        response.getWriter().write(new ObjectMapper().writeValueAsString(result));
    }
}
