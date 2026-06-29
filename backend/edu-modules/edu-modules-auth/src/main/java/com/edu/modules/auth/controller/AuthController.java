package com.edu.modules.auth.controller;

import com.edu.common.core.response.ApiResult;
import com.edu.modules.auth.dto.*;
import com.edu.modules.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "认证模块", description = "用户登录、验证码、Token刷新")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/send-sms")
    @Operation(summary = "发送验证码")
    public ApiResult<Void> sendSms(@Valid @RequestBody SmsRequest request) {
        authService.sendSms(request.getPhone());
        return ApiResult.success();
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public ApiResult<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResult.success(authService.login(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "刷新Token")
    public ApiResult<LoginResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ApiResult.success(authService.refresh(request));
    }

    @PostMapping("/logout")
    @Operation(summary = "登出")
    public ApiResult<Void> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        authService.logout(token);
        return ApiResult.success();
    }
}
