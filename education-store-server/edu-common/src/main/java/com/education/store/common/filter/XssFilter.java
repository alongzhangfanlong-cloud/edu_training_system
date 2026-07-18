package com.education.store.common.filter;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * XSS 防护过滤器
 * <p>
 * 对非 JSON 请求的参数进行 HTML 转义，防止 XSS 攻击。
 * application/json 请求跳过过滤，交由 Jackson 反序列化时处理。
 */
@Slf4j
@WebFilter(urlPatterns = "/*")
public class XssFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // JSON 请求跳过 XSS 过滤（由 Jackson 处理）
        String contentType = httpRequest.getContentType();
        if (StrUtil.isNotBlank(contentType) && contentType.contains("application/json")) {
            chain.doFilter(request, response);
            return;
        }

        // 其他请求包装为 XSS 安全请求
        chain.doFilter(new XssHttpServletRequestWrapper(httpRequest), response);
    }
}
