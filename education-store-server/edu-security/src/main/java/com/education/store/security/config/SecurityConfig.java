package com.education.store.security.config;

import com.education.store.security.filter.JwtAuthenticationFilter;
import com.education.store.security.handler.AccessDeniedHandler;
import com.education.store.security.handler.AuthEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

/**
 * Spring Security 配置
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthEntryPoint authEntryPoint;
    private final AccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 禁用 CSRF
            .csrf(AbstractHttpConfigurer::disable)
            // 基于Token，禁用Session
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 异常处理
            .exceptionHandling(ex -> ex
                    .authenticationEntryPoint(authEntryPoint)
                    .accessDeniedHandler(accessDeniedHandler)
            )
            // 请求权限配置
            .authorizeHttpRequests(auth -> auth
                    // 放行接口
                    .requestMatchers(
                            "/api/v1/auth/login",
                            "/api/v1/auth/refresh",
                            "/api/v1/auth/wechat-login",
                            "/api/v1/auth/wechat-bind",
                            "/api/v1/captcha",
                            "/doc.html",
                            "/swagger-ui/**",
                            "/v3/api-docs/**",
                            "/webjars/**",
                            "/actuator/health"
                    ).permitAll()
                    // WebSocket
                    .requestMatchers("/ws/**").permitAll()
                    // 上传文件访问控制 — 仅允许安全文件扩展名，防止恶意脚本执行
                    // 扩展名白名单：图片、文档、文本、视频/音频
                    .requestMatchers(new RegexRequestMatcher(
                            "/upload/.*\\.(jpg|jpeg|png|gif|bmp|svg|webp|pdf|doc|docx|xls|xlsx|ppt|pptx|txt|csv|mp4|mp3)$",
                            null
                    )).permitAll()
                    // 拒绝 upload 目录下高危文件类型的直接访问（jsp/php/exe/sh/bat 等）
                    // 建议：生产环境应同时在 Nginx/CDN 层配置 upload 目录只允许静态资源扩展名
                    .requestMatchers(new RegexRequestMatcher(
                            "/upload/.*\\.(jsp|jspx|php|asp|aspx|exe|sh|bat|cmd|com|msi|dll|so|war|jar|class)$",
                            null
                    )).denyAll()
                    // 其他 upload 路径（不含扩展名或非白名单扩展名）需要认证
                    .requestMatchers("/upload/**").authenticated()
                    // 其他静态资源
                    .requestMatchers(
                            "/static/**",
                            "/favicon.ico"
                    ).permitAll()
                    // 其余接口全部需要认证
                    .anyRequest().authenticated()
            )
            // JWT过滤器
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
