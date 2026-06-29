package com.edu.modules.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.edu.common.core.exception.BusinessException;
import com.edu.common.core.exception.ErrorCode;
import com.edu.common.security.JwtTokenProvider;
import com.edu.modules.auth.dto.LoginRequest;
import com.edu.modules.auth.dto.LoginResponse;
import com.edu.modules.auth.dto.RefreshTokenRequest;
import com.edu.modules.auth.entity.UserEntity;
import com.edu.modules.auth.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;
    private final UserMapper userMapper;

    private static final String SMS_PREFIX = "sms:code:";
    private static final String REFRESH_PREFIX = "refresh:token:";
    private static final String BLACKLIST_PREFIX = "token:blacklist:";

    public void sendSms(String phone) {
        String code = "123456";
        redisTemplate.opsForValue().set(SMS_PREFIX + phone, code, 5, TimeUnit.MINUTES);
        log.info("发送验证码到 {}: {}", phone, code);
    }

    /**
     * 用户登录 - 真实DB查询
     * 支持手机号登录: 查找users表匹配phone的用户
     */
    public LoginResponse login(LoginRequest request) {
        // 1. 验证码校验
        String key = SMS_PREFIX + request.getPhone();
        String cachedCode = redisTemplate.opsForValue().get(key);
        if (cachedCode == null || !cachedCode.equals(request.getCode())) {
            throw new BusinessException(ErrorCode.SMS_CODE_ERROR);
        }
        redisTemplate.delete(key);

        // 2. 从DB查找用户 (按手机号 + 角色匹配)
        UserEntity user = findUserByPhoneAndRole(request.getPhone(), request.getRole());
        if (user == null) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED,
                    "未找到该手机号对应的" + getRoleName(request.getRole()) + "账号");
        }
        if (user.getStatus() != 1) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED, "账号已被禁用，请联系管理员");
        }

        // 3. 获取角色和权限
        List<String> roles = userMapper.selectRolesByUserId(user.getId());
        List<String> permissions = userMapper.selectPermissionsByUserId(user.getId());
        String primaryRole = roles.isEmpty() ? request.getRole() : roles.get(0);

        // 4. 获取校区信息(校长/教师)
        Long campusId = null;
        String campusName = null;
        if ("principal".equals(primaryRole)) {
            Map<String, Object> campus = userMapper.selectCampusByPrincipal(user.getId());
            if (campus != null) { campusId = ((Number) campus.get("id")).longValue(); campusName = (String) campus.get("campus_name"); }
        } else if ("teacher".equals(primaryRole)) {
            Map<String, Object> campus = userMapper.selectCampusByTeacherPhone(user.getPhone());
            if (campus != null) { campusId = ((Number) campus.get("id")).longValue(); campusName = (String) campus.get("campus_name"); }
        }

        // 5. 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);

        // 6. 生成Token
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), primaryRole, user.getPhone());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());
        redisTemplate.opsForValue().set(REFRESH_PREFIX + user.getId(), refreshToken, 30, TimeUnit.DAYS);

        log.info("用户登录成功: phone={}, role={}, userId={}, campusId={}",
                request.getPhone(), primaryRole, user.getId(), campusId);

        return LoginResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .user(LoginResponse.UserInfo.builder()
                        .id(user.getId())
                        .name(user.getRealName() != null ? user.getRealName() : user.getUsername())
                        .avatar(user.getAvatar())
                        .role(primaryRole)
                        .phone(user.getPhone())
                        .campusId(campusId)
                        .campusName(campusName)
                        .permissions(permissions)
                        .build())
                .build();
    }

    public LoginResponse refresh(RefreshTokenRequest request) {
        if (!jwtTokenProvider.validateToken(request.getRefreshToken())) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID);
        }
        Long userId = jwtTokenProvider.getUserIdFromToken(request.getRefreshToken());
        String cached = redisTemplate.opsForValue().get(REFRESH_PREFIX + userId);
        if (!request.getRefreshToken().equals(cached)) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID);
        }

        UserEntity user = userMapper.selectById(userId);
        List<String> roles = userMapper.selectRolesByUserId(userId);
        String role = roles.isEmpty() ? "student" : roles.get(0);

        String newToken = jwtTokenProvider.generateAccessToken(userId, role, user != null ? user.getPhone() : "");
        String newRefresh = jwtTokenProvider.generateRefreshToken(userId);
        redisTemplate.opsForValue().set(REFRESH_PREFIX + userId, newRefresh, 30, TimeUnit.DAYS);

        return LoginResponse.builder().token(newToken).refreshToken(newRefresh).build();
    }

    public void logout(String token) {
        redisTemplate.opsForValue().set(BLACKLIST_PREFIX + token, "1", 7, TimeUnit.DAYS);
    }

    // ===== 用户管理 =====

    public List<UserEntity> listUsers(String keyword, Integer status, int page, int pageSize) {
        LambdaQueryWrapper<UserEntity> w = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank())
            w.and(wr -> wr.like(UserEntity::getUsername, keyword)
                    .or().like(UserEntity::getRealName, keyword)
                    .or().like(UserEntity::getPhone, keyword));
        if (status != null) w.eq(UserEntity::getStatus, status);
        w.orderByDesc(UserEntity::getCreatedAt);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<UserEntity> p =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, Math.min(pageSize, 100));
        return userMapper.selectPage(p, w).getRecords();
    }

    public long countUsers(String keyword, Integer status) {
        LambdaQueryWrapper<UserEntity> w = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank())
            w.and(wr -> wr.like(UserEntity::getUsername, keyword)
                    .or().like(UserEntity::getRealName, keyword)
                    .or().like(UserEntity::getPhone, keyword));
        if (status != null) w.eq(UserEntity::getStatus, status);
        return userMapper.selectCount(w);
    }

    public void updateStatus(Long userId, Integer status) {
        UserEntity user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException("用户不存在");
        user.setStatus(status);
        userMapper.updateById(user);
        log.info("用户状态更新: userId={}, status={}", userId, status);
    }

    public Long createUser(Map<String, Object> body) {
        UserEntity user = new UserEntity();
        user.setUsername((String) body.getOrDefault("username", ""));
        user.setRealName((String) body.getOrDefault("realName", ""));
        user.setPhone((String) body.getOrDefault("phone", ""));
        user.setPassword((String) body.getOrDefault("password", "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi"));
        user.setStatus(1);
        userMapper.insert(user);
        log.info("创建用户: id={}, username={}", user.getId(), user.getUsername());
        return user.getId();
    }

    public void updateUser(Long userId, Map<String, Object> body) {
        UserEntity user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException("用户不存在");
        if (body.containsKey("username")) user.setUsername((String) body.get("username"));
        if (body.containsKey("realName")) user.setRealName((String) body.get("realName"));
        if (body.containsKey("phone")) user.setPhone((String) body.get("phone"));
        if (body.containsKey("status")) user.setStatus((Integer) body.get("status"));
        userMapper.updateById(user);
        log.info("更新用户: id={}", userId);
    }

    // ===== private =====

    private UserEntity findUserByPhoneAndRole(String phone, String role) {
        // 查找phone匹配且拥有指定角色的用户
        LambdaQueryWrapper<UserEntity> w = new LambdaQueryWrapper<>();
        w.eq(UserEntity::getPhone, phone).eq(UserEntity::getStatus, 1);
        List<UserEntity> users = userMapper.selectList(w);

        for (UserEntity user : users) {
            List<String> roles = userMapper.selectRolesByUserId(user.getId());
            if (roles.contains(role)) return user;
        }
        // 如果没找到匹配角色的，返回第一个用户（兜底）
        return users.isEmpty() ? null : users.get(0);
    }

    private String getRoleName(String role) {
        return switch (role) {
            case "boss" -> "老板"; case "principal" -> "校长";
            case "teacher" -> "教师"; case "parent" -> "家长";
            default -> "学员";
        };
    }
}
