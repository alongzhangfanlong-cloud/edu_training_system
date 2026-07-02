package com.edu.app.aop;

import com.edu.common.security.UserContextHolder;
import com.edu.modules.interaction.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final OperationLogService operationLogService;

    @Pointcut("execution(* com.edu.modules..controller..*(..)) && " +
              "(@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
              " @annotation(org.springframework.web.bind.annotation.PutMapping) || " +
              " @annotation(org.springframework.web.bind.annotation.DeleteMapping))")
    public void writeOperation() {}

    @AfterReturning(pointcut = "writeOperation()")
    public void logWrite(JoinPoint joinPoint) {
        try {
            String methodName = joinPoint.getSignature().getName();
            String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
            String module = className.replace("Controller", "");

            String action = "UPDATE";
            if (methodName.contains("create") || methodName.contains("add") || methodName.contains("submit")
                    || methodName.contains("apply") || methodName.contains("send") || methodName.contains("assign")
                    || methodName.contains("batch") || methodName.contains("change")) {
                action = "CREATE";
            } else if (methodName.contains("delete") || methodName.contains("remove")) {
                action = "DELETE";
            }

            Long userId = null;
            String userName = "系统";
            String role = "system";
            try {
                userId = UserContextHolder.getUserId();
                role = UserContextHolder.getRole();
                var user = UserContextHolder.get();
                if (user != null) userName = user.getUserName();
            } catch (Exception ignored) {}

            String ip = getClientIp();
            operationLogService.log(userId, userName != null ? userName : "系统", role,
                    module, action, className, null, "执行: " + methodName, ip);
        } catch (Exception e) {
            log.debug("操作日志记录跳过: {}", e.getMessage());
        }
    }

    private String getClientIp() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes)
                    RequestContextHolder.currentRequestAttributes()).getRequest();
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.isEmpty()) ip = request.getHeader("X-Real-IP");
            if (ip == null || ip.isEmpty()) ip = request.getRemoteAddr();
            return ip;
        } catch (Exception e) { return "unknown"; }
    }
}
