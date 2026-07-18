package com.education.store.common.filter;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.http.HtmlUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.HashMap;
import java.util.Map;

/**
 * XSS 过滤请求包装器
 * <p>
 * 对请求参数中的 HTML 特殊字符进行转义，防止 XSS 攻击。
 * 转义字符：&lt; &gt; &amp; &quot; &#x27;
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    public XssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        return escape(value);
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (ArrayUtil.isEmpty(values)) {
            return values;
        }
        String[] escaped = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            escaped[i] = escape(values[i]);
        }
        return escaped;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> map = super.getParameterMap();
        if (map.isEmpty()) {
            return map;
        }
        Map<String, String[]> escapedMap = new HashMap<>(map.size());
        for (Map.Entry<String, String[]> entry : map.entrySet()) {
            String[] values = entry.getValue();
            if (values != null) {
                String[] escaped = new String[values.length];
                for (int i = 0; i < values.length; i++) {
                    escaped[i] = escape(values[i]);
                }
                escapedMap.put(entry.getKey(), escaped);
            } else {
                escapedMap.put(entry.getKey(), null);
            }
        }
        return escapedMap;
    }

    /**
     * 使用 Hutool HtmlUtil.escape 转义 HTML 特殊字符
     */
    private String escape(String value) {
        if (value == null) {
            return null;
        }
        return HtmlUtil.escape(value);
    }
}
