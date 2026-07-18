package com.education.store.module.system.aspect;

import cn.hutool.json.JSONUtil;
import com.education.store.common.annotation.Log;
import com.education.store.common.utils.AsyncManager;
import com.education.store.common.utils.SecurityUtils;
import com.education.store.module.system.entity.system.SysOperLog;
import com.education.store.module.system.mapper.SysOperLogMapper;
import com.education.store.module.system.mapper.SysUserMapper;
import com.education.store.module.system.entity.system.SysUser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

/**
 * 操作日志 AOP 切面
 */
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {

    private final SysOperLogMapper operLogMapper;
    private final SysUserMapper userMapper;

    @Around("@annotation(log)")
    public Object around(ProceedingJoinPoint point, Log log) throws Throwable {
        long start = System.currentTimeMillis();

        SysOperLog operLog = new SysOperLog();
        operLog.setTitle(log.title());
        operLog.setBusinessType(log.businessType().name());
        MethodSignature sig = (MethodSignature) point.getSignature();
        operLog.setMethod(sig.getDeclaringTypeName() + "." + sig.getName());
        operLog.setOperTime(LocalDateTime.now());

        // 获取操作人信息
        Long userId = SecurityUtils.getUserId();
        if (userId != null) {
            operLog.setOperName(String.valueOf(userId));
            try {
                SysUser user = userMapper.selectById(userId);
                if (user != null) {
                    operLog.setOperName(user.getRealName() != null ? user.getRealName() : user.getUsername());
                }
            } catch (Exception ignored) {}
        } else {
            operLog.setOperName("anonymous");
        }

        // 获取请求信息
        try {
            HttpServletRequest request = ((ServletRequestAttributes)
                    RequestContextHolder.currentRequestAttributes()).getRequest();
            operLog.setRequestMethod(request.getMethod());
            operLog.setOperUrl(request.getRequestURI());
            operLog.setOperIp(getIpAddress(request));
        } catch (Exception ignored) {}

        // 保存请求参数
        if (log.isSaveRequestData()) {
            try {
                Object[] args = point.getArgs();
                if (args != null && args.length > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (Object arg : args) {
                        if (arg != null
                                && !(arg instanceof jakarta.servlet.http.HttpServletRequest)
                                && !(arg instanceof jakarta.servlet.http.HttpServletResponse)) {
                            try {
                                sb.append(JSONUtil.toJsonStr(arg)).append(";");
                            } catch (Exception ignored) {}
                        }
                    }
                    if (sb.length() > 0) {
                        operLog.setOperParam(sb.toString());
                    }
                }
            } catch (Exception ignored) {}
        }

        try {
            Object result = point.proceed();
            operLog.setStatus(1);
            operLog.setCostTime(System.currentTimeMillis() - start);

            // 保存返回结果
            if (log.isSaveResponseData()) {
                try {
                    operLog.setJsonResult(JSONUtil.toJsonStr(result));
                } catch (Exception ignored) {}
            }
            return result;
        } catch (Exception e) {
            operLog.setStatus(0);
            operLog.setErrorMsg(e.getMessage() != null
                    ? e.getMessage().substring(0, Math.min(e.getMessage().length(), 2000)) : "");
            operLog.setCostTime(System.currentTimeMillis() - start);
            throw e;
        } finally {
            // 异步保存日志
            AsyncManager.execute(() -> {
                try {
                    operLogMapper.insert(operLog);
                } catch (Exception ignored) {}
            });
        }
    }

    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
