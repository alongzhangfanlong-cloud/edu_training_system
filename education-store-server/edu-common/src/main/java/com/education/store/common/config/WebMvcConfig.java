package com.education.store.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * Web MVC 配置 — 跨域 + 静态资源映射
 * <p>
 * 生产环境通过环境变量 CORS_ALLOWED_ORIGINS 指定前端域名白名单（逗号分隔）。
 * 默认仅允许本地开发端口，避免任意来源携带 Cookie 发起跨域请求。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${edu.cors.allowed-origins:http://localhost:3000,http://localhost:5173,http://127.0.0.1:3000,http://127.0.0.1:5173}")
    private String allowedOriginsConfig;

    @Value("${edu.upload-path:upload}")
    private String uploadPath;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        List<String> origins = Arrays.stream(allowedOriginsConfig.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        registry.addMapping("/**")
                .allowedOrigins(origins.toArray(new String[0]))
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * 静态资源映射 — 将 /upload/** 映射到上传目录，使上传的图片等文件可通过公开 URL 访问
     * SecurityConfig 已允许 /upload/** 安全扩展名匿名访问
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String absolutePath = Paths.get(uploadPath).toAbsolutePath().normalize().toString();
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:" + absolutePath + "/");
    }
}
