package com.education.store.module.system.controller;

import com.education.store.common.constant.Constants;
import com.education.store.common.enums.BusinessType;
import com.education.store.common.model.Result;
import com.education.store.common.utils.SecurityUtils;
import com.education.store.module.system.dto.LoginDTO;
import com.education.store.module.system.dto.LoginVO;
import com.education.store.module.system.dto.WechatBindDTO;
import com.education.store.module.system.dto.WechatLoginDTO;
import com.education.store.module.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证控制器
 */
@Tag(name = "认证管理")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SysUserService userService;
    private final StringRedisTemplate stringRedisTemplate;

    @Operation(summary = "用户名密码登录")
    @com.education.store.common.annotation.Log(title = "用户登录", businessType = BusinessType.OTHER)
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        LoginVO vo = userService.login(loginDTO);
        return Result.success(vo);
    }

    @Operation(summary = "刷新Token")
    @PostMapping("/refresh")
    public Result<LoginVO> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        LoginVO vo = userService.refreshToken(refreshToken);
        return Result.success(vo);
    }

    @Operation(summary = "微信小程序静默登录")
    @PostMapping("/wechat-login")
    public Result<LoginVO> wechatLogin(@Valid @RequestBody WechatLoginDTO dto) {
        LoginVO vo = userService.wechatLogin(dto.getCode());
        return Result.success(vo);
    }

    @Operation(summary = "微信小程序手机号绑定登录")
    @com.education.store.common.annotation.Log(title = "微信绑定", businessType = BusinessType.OTHER)
    @PostMapping("/wechat-bind")
    public Result<LoginVO> wechatBind(@Valid @RequestBody WechatBindDTO dto) {
        LoginVO vo = userService.wechatBind(dto.getCode(), dto.getEncryptedPhone(), dto.getIv());
        return Result.success(vo);
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public Result<Void> logout() {
        Long userId = SecurityUtils.getUserId();
        if (userId != null) {
            stringRedisTemplate.delete(Constants.REDIS_KEY_TOKEN + userId);
            stringRedisTemplate.delete(Constants.REDIS_KEY_PERM + userId);
        }
        return Result.success();
    }

    @Operation(summary = "修改密码")
    @PreAuthorize("isAuthenticated()")
    @com.education.store.common.annotation.Log(title = "修改密码", businessType = BusinessType.UPDATE)
    @PutMapping("/password")
    public Result<Void> updatePassword(@RequestBody Map<String, String> body) {
        Long userId = SecurityUtils.getUserId();
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");
        userService.changePassword(userId, oldPassword, newPassword);
        return Result.success();
    }
}
