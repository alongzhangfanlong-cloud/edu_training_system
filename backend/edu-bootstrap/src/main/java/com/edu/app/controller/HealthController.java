package com.edu.app.controller;

import com.edu.common.core.response.ApiResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/api/v1/health")
    public ApiResult<Map<String, Object>> health() {
        return ApiResult.success(Map.of(
                "status", "UP",
                "service", "edu-training-system",
                "version", "1.0.0",
                "timestamp", LocalDateTime.now().toString()
        ));
    }
}
