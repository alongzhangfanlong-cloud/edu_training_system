package com.education.store.module.system.service.impl;

import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.constant.Constants;
import com.education.store.common.enums.BusinessType;
import com.education.store.common.exception.BusinessException;
import com.education.store.common.utils.AsyncManager;
import com.education.store.module.system.dto.LoginDTO;
import com.education.store.module.system.dto.LoginVO;
import com.education.store.module.system.entity.system.SysOperLog;
import com.education.store.module.system.entity.system.SysUser;
import com.education.store.module.system.vo.UserSimpleVO;
import com.education.store.module.system.mapper.SysOperLogMapper;
import com.education.store.module.system.mapper.SysUserMapper;
import com.education.store.module.system.service.ISysUserRoleService;
import com.education.store.module.system.service.SysUserService;
import com.education.store.module.system.service.WechatAuthService;
import com.education.store.security.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户 Service 实现
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser>
        implements SysUserService {

    private final SysUserMapper userMapper;
    private final SysOperLogMapper operLogMapper;
    private final ISysUserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate stringRedisTemplate;
    private final WechatAuthService wechatAuthService;

    @org.springframework.beans.factory.annotation.Value("${edu.user.default-password:123456}")
    private String defaultPassword;

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        long start = System.currentTimeMillis();
        // 查询用户
        SysUser user = getByUsername(loginDTO.getUsername());
        if (user == null) {
            recordLoginLog("用户登录", loginDTO.getUsername(), false, "用户名或密码错误", start);
            throw new BusinessException("用户名或密码错误");
        }
        if (Constants.STATUS_DISABLE.equals(user.getStatus())) {
            recordLoginLog("用户登录", user.getUsername(), false, "账号已被禁用", start);
            throw new BusinessException("账号已被禁用，请联系管理员");
        }

        // 验证密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            recordLoginLog("用户登录", user.getUsername(), false, "用户名或密码错误", start);
            throw new BusinessException("用户名或密码错误");
        }

        LoginVO result = buildLoginResponse(user);
        recordLoginLog("用户登录", user.getUsername(), true, null, start);
        return result;
    }

    @Override
    public LoginVO refreshToken(String refreshToken) {
        // 解析RefreshToken
        io.jsonwebtoken.Claims claims;
        try {
            claims = jwtUtil.parseToken(refreshToken);
        } catch (Exception e) {
            throw new BusinessException("RefreshToken无效或已过期，请重新登录");
        }

        // 校验类型
        String tokenType = claims.get(Constants.CLAIM_TOKEN_TYPE, String.class);
        if (!Constants.TOKEN_TYPE_REFRESH.equals(tokenType)) {
            throw new BusinessException("非法的RefreshToken类型");
        }

        Long userId = claims.get(Constants.CLAIM_USER_ID, Long.class);

        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (Constants.STATUS_DISABLE.equals(user.getStatus())) {
            throw new BusinessException("账号已被禁用，请联系管理员");
        }

        return buildLoginResponse(user);
    }

    private LoginVO buildLoginResponse(SysUser user) {
        // 生成Token
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getUsername());

        // 存储Token到Redis
        String tokenKey = Constants.REDIS_KEY_TOKEN + user.getId();
        stringRedisTemplate.opsForValue().set(tokenKey, accessToken, 2, TimeUnit.HOURS);

        // 获取用户权限和角色
        Set<String> permissions = getUserPermissions(user.getId());
        List<String> roleCodes = userMapper.selectRoleCodesByUserId(user.getId());

        // 缓存权限（null + 空权限集合跳过 Redis SADD，避免无参数报错）
        String permKey = Constants.REDIS_KEY_PERM + user.getId();
        if (permissions != null && !permissions.isEmpty()) {
            stringRedisTemplate.opsForSet().add(permKey, permissions.toArray(new String[0]));
            stringRedisTemplate.expire(permKey, 30, TimeUnit.MINUTES);
        }

        // 构建返回
        LoginVO vo = new LoginVO();
        vo.setAccessToken(accessToken);
        vo.setRefreshToken(refreshToken);
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setAvatar(user.getAvatar());
        vo.setPermissions(permissions);
        vo.setRoles(roleCodes);
        return vo;
    }

    @Override
    public SysUser getByUsername(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(SysUser::getUsername, username)
                .or()
                .eq(SysUser::getMobile, username)
                .or()
                .eq(SysUser::getEmail, username));
        return userMapper.selectOne(wrapper);
    }

    @Override
    public LoginVO wechatLogin(String code) {
        long start = System.currentTimeMillis();
        // code → openid
        String openid = wechatAuthService.getOpenid(code);

        // 查用户
        SysUser user = getByOpenid(openid);
        if (user == null) {
            recordLoginLog("微信登录", "unknown", false, "请先绑定手机号", start);
            throw new BusinessException("请先绑定手机号");
        }
        if (Constants.STATUS_DISABLE.equals(user.getStatus())) {
            recordLoginLog("微信登录", user.getUsername(), false, "账号已被禁用", start);
            throw new BusinessException("账号已被禁用，请联系管理员");
        }

        LoginVO result = buildLoginResponse(user);
        recordLoginLog("微信登录", user.getUsername(), true, null, start);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginVO wechatBind(String code, String encryptedPhone, String iv) {
        long start = System.currentTimeMillis();
        // code 只能使用一次：一次性获取 session 信息（openid + sessionKey）
        WxMaJscode2SessionResult session = wechatAuthService.getSessionInfo(code);

        // sessionKey → 解密手机号
        String phone = wechatAuthService.decryptPhone(session.getSessionKey(), encryptedPhone, iv);

        // 按手机号查用户
        SysUser user = getByMobile(phone);
        if (user == null) {
            recordLoginLog("微信绑定登录", "unknown", false, "手机号未关联账户", start);
            throw new BusinessException("手机号未关联账户，请联系机构");
        }
        if (Constants.STATUS_DISABLE.equals(user.getStatus())) {
            recordLoginLog("微信绑定登录", user.getUsername(), false, "账号已被禁用", start);
            throw new BusinessException("账号已被禁用，请联系管理员");
        }

        // 绑定 openid（使用同一次 code2session 返回的 openid，不再重复请求）
        String openid = session.getOpenid();

        // 检查该 openid 是否已被其他用户绑定
        SysUser existingOpenidUser = getByOpenid(openid);
        if (existingOpenidUser != null && !existingOpenidUser.getId().equals(user.getId())) {
            recordLoginLog("微信绑定登录", user.getUsername(), false, "该微信已绑定其他账号", start);
            throw new BusinessException("该微信已绑定其他账号");
        }

        user.setWxOpenid(openid);
        userMapper.updateById(user);

        LoginVO result = buildLoginResponse(user);
        recordLoginLog("微信绑定登录", user.getUsername(), true, null, start);
        return result;
    }

    @Override
    public SysUser getByOpenid(String openid) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getWxOpenid, openid);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public SysUser getByMobile(String mobile) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getMobile, mobile);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public void bindOpenid(Long userId, String openid) {
        SysUser user = userMapper.selectById(userId);
        if (user != null) {
            user.setWxOpenid(openid);
            userMapper.updateById(user);
        }
    }

    @Override
    public Page<SysUser> getUserPage(long page, long size, String keyword, Integer status) {
        return userMapper.selectUserPage(new Page<>(page, size), keyword, status);
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
    }

    @Override
    public Set<String> getUserPermissions(Long userId) {
        return userMapper.selectPermissionsByUserId(userId);
    }

    // ======== 系统管理 CRUD 方法 ========

    @Override
    public IPage<SysUser> selectUserList(IPage<SysUser> page, SysUser user) {
        // 从 SysUser 对象提取 keyword（Controller 将 keyword 复制到三个字段）
        String keyword = StrUtil.isNotBlank(user.getUsername())
                ? user.getUsername()
                : (StrUtil.isNotBlank(user.getRealName())
                        ? user.getRealName()
                        : user.getMobile());
        LambdaQueryWrapper<SysUser> wrapper = buildUserQueryWrapper(keyword, user.getStatus());
        return userMapper.selectPage(page, wrapper);
    }

    @Override
    public SysUser selectUserById(Long userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public SysUser selectUserByUsername(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        return userMapper.selectOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertUser(SysUser user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        int rows = userMapper.insert(user);

        userRoleService.insertUserRoles(user.getId(), user.getRoleIds());

        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateUser(SysUser user) {
        // 检查并保护 admin 用户
        SysUser exist = userMapper.selectById(user.getId());
        if (exist != null) {
            checkUserAllowed(exist);
        }

        if (StrUtil.isNotBlank(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        // 注意：不传密码时不更新 password 字段。MyBatis-Plus updateById 默认
        // 忽略 null 值字段（update-strategy: NOT_NULL），因此 password 为 null 时不会写入 DB。
        // 如果全局配置变更（如改为 IGNORED），需改用 LambdaUpdateWrapper 显式选择更新列。

        int rows = userMapper.updateById(user);

        // 仅当 roleIds 明确传入时才更新角色关联（null 表示不修改角色）
        if (user.getRoleIds() != null) {
            userRoleService.deleteByUserId(user.getId());
            userRoleService.insertUserRoles(user.getId(), user.getRoleIds());
        }

        // 清除权限缓存，使角色变更立即生效
        stringRedisTemplate.delete(Constants.REDIS_KEY_PERM + user.getId());

        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteUserByIds(Long[] userIds) {
        Long currentUserId = com.education.store.common.utils.SecurityUtils.getUserId();
        for (Long userId : userIds) {
            SysUser user = userMapper.selectById(userId);
            checkUserAllowed(user);
            if (currentUserId != null && currentUserId.equals(userId)) {
                throw new BusinessException("不能删除当前登录用户");
            }
            userMapper.deleteById(userId);
            userRoleService.deleteByUserId(userId);
            stringRedisTemplate.delete(Constants.REDIS_KEY_TOKEN + userId);
            stringRedisTemplate.delete(Constants.REDIS_KEY_PERM + userId);
        }
        return userIds.length;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int resetPwd(SysUser user) {
        SysUser exist = userMapper.selectById(user.getId());
        if (exist != null) {
            checkUserAllowed(exist);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateUserStatus(SysUser user) {
        SysUser exist = userMapper.selectById(user.getId());
        if (exist != null) {
            checkUserAllowed(exist);
        }
        return userMapper.updateById(user);
    }

    @Override
    public String importUser(List<SysUser> list,
                              boolean updateSupport, String operName) {
        if (list == null || list.isEmpty()) {
            throw new BusinessException("导入数据不能为空");
        }
        int successCount = 0;
        int failCount = 0;
        StringBuilder msg = new StringBuilder();

        for (SysUser user : list) {
            try {
                if (StrUtil.isBlank(user.getUsername())) {
                    failCount++;
                    continue;
                }
                SysUser exist = selectUserByUsername(user.getUsername());
                if (exist != null) {
                    if (updateSupport) {
                        user.setId(exist.getId());
                        updateUser(user);
                        successCount++;
                    } else {
                        failCount++;
                    }
                } else {
                    if (StrUtil.isBlank(user.getPassword())) {
                        user.setPassword(defaultPassword);
                    }
                    user.setCreateBy(operName);
                    insertUser(user);
                    successCount++;
                }
            } catch (Exception e) {
                failCount++;
                msg.append("<br/>").append(user.getUsername()).append("：").append(e.getMessage());
            }
        }
        return "成功导入 " + successCount + " 条，失败 " + failCount + " 条" + msg.toString();
    }

    @Override
    public boolean checkUserNameUnique(SysUser user) {
        Long id = user.getId() != null ? user.getId() : -1L;
        SysUser exist = selectUserByUsername(user.getUsername());
        return exist == null || exist.getId().equals(id);
    }

    @Override
    public boolean checkPhoneUnique(SysUser user) {
        Long id = user.getId() != null ? user.getId() : -1L;
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getMobile, user.getMobile());
        SysUser exist = userMapper.selectOne(wrapper);
        return exist == null || exist.getId().equals(id);
    }

    @Override
    public boolean checkEmailUnique(SysUser user) {
        Long id = user.getId() != null ? user.getId() : -1L;
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getEmail, user.getEmail());
        SysUser exist = userMapper.selectOne(wrapper);
        return exist == null || exist.getId().equals(id);
    }

    @Override
    public void checkUserAllowed(SysUser user) {
        if (user != null && "admin".equals(user.getUsername())) {
            throw new BusinessException("不允许操作超级管理员用户");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertUserAuth(Long userId, Long[] roleIds) {
        userRoleService.deleteByUserId(userId);
        if (roleIds != null && roleIds.length > 0) {
            userRoleService.insertUserRoles(userId, Arrays.asList(roleIds));
        }
    }

    @Override
    public List<Long> selectUserRoleIds(Long userId) {
        return userRoleService.selectRoleIdsByUserId(userId);
    }

    @Override
    public byte[] exportUser(String keyword, Integer status) {
        List<SysUser> users = userMapper.selectList(buildUserQueryWrapper(keyword, status));

        // 限制最大导出行数
        if (users.size() > 10000) {
            throw new BusinessException("导出数据量过大（超过 10000 条），请缩小筛选范围后重试");
        }

        // 使用 POI 生成 Excel（流式写入避免 OOM）
        try (SXSSFWorkbook workbook = new SXSSFWorkbook(100);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("用户数据");
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            String[] headers = {"用户ID", "用户名", "真实姓名", "邮箱", "手机号", "性别", "状态", "创建时间"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            for (int i = 0; i < users.size(); i++) {
                SysUser user = users.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(user.getId() != null ? user.getId() : 0);
                row.createCell(1).setCellValue(user.getUsername() != null ? user.getUsername() : "");
                row.createCell(2).setCellValue(user.getRealName() != null ? user.getRealName() : "");
                row.createCell(3).setCellValue(user.getEmail() != null ? user.getEmail() : "");
                row.createCell(4).setCellValue(user.getMobile() != null ? user.getMobile() : "");
                row.createCell(5).setCellValue(toGenderText(user.getGender()));
                row.createCell(6).setCellValue(user.getStatus() != null && user.getStatus() == 1 ? "正常" : "停用");
                row.createCell(7).setCellValue(user.getCreateTime() != null ? user.getCreateTime().format(dtf) : "");
            }

            // 固定列宽（避免 autoSizeColumn 性能问题）
            int[] widths = {10, 16, 14, 24, 18, 8, 8, 22};
            for (int i = 0; i < widths.length; i++) {
                sheet.setColumnWidth(i, widths[i] * 256);
            }

            workbook.write(bos);
            return bos.toByteArray();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("导出失败，请稍后重试", e);
        }
    }

    /** 构建用户查询条件（selectUserList 和 exportUser 共用） */
    private LambdaQueryWrapper<SysUser> buildUserQueryWrapper(String keyword, Integer status) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w ->
                    w.like(SysUser::getUsername, keyword)
                            .or()
                            .like(SysUser::getRealName, keyword)
                            .or()
                            .like(SysUser::getMobile, keyword));
        }
        if (status != null) {
            wrapper.eq(SysUser::getStatus, status);
        }
        wrapper.orderByDesc(SysUser::getCreateTime);
        return wrapper;
    }

    @Override
    public List<UserSimpleVO> listUsersByRoleCode(String roleCode) {
        return userMapper.selectUsersByRoleCode(roleCode).stream()
                .map(m -> new UserSimpleVO(
                        ((Number) m.get("userId")).longValue(),
                        (String) m.get("realName")))
                .collect(Collectors.toList());
    }

    /** 性别数值转中文 */
    private String toGenderText(Integer gender) {
        if (gender == null) return "";
        return switch (gender) {
            case 1 -> "男";
            case 2 -> "女";
            default -> "未知";
        };
    }

    // ======== 登录日志记录 ========

    /**
     * 记录登录操作日志（写入 sys_oper_log，businessType=LOGIN）
     */
    private void recordLoginLog(String title, String username, boolean success,
                                String errorMsg, long startTime) {
        SysOperLog operLog = new SysOperLog();
        operLog.setTitle(title);
        operLog.setBusinessType(BusinessType.LOGIN.name());
        operLog.setOperName(username);
        operLog.setStatus(success ? 1 : 0);
        operLog.setCostTime(System.currentTimeMillis() - startTime);
        operLog.setOperTime(LocalDateTime.now());
        if (errorMsg != null && !errorMsg.isEmpty()) {
            operLog.setErrorMsg(errorMsg.length() > 2000
                    ? errorMsg.substring(0, 2000) : errorMsg);
        }

        // 获取请求信息（IP、浏览器、OS）并构建脱敏参数
        try {
            HttpServletRequest request = ((ServletRequestAttributes)
                    RequestContextHolder.currentRequestAttributes()).getRequest();
            operLog.setRequestMethod(request.getMethod());
            operLog.setOperUrl(request.getRequestURI());
            operLog.setOperIp(getIpAddress(request));

            // 构建请求参数：username 保留，password 脱敏，附加浏览器/OS
            Map<String, Object> params = new LinkedHashMap<>();
            params.put("username", username);
            params.put("password", "****");
            String uaStr = request.getHeader("User-Agent");
            if (uaStr != null) {
                try {
                    UserAgent ua = UserAgentUtil.parse(uaStr);
                    params.put("browser", ua.getBrowser().toString());
                    params.put("os", ua.getPlatform().toString());
                } catch (Exception ignored) {}
            }
            operLog.setOperParam(JSONUtil.toJsonStr(params));
        } catch (Exception ignored) {}

        // 异步保存
        AsyncManager.execute(() -> {
            try {
                operLogMapper.insert(operLog);
            } catch (Exception ignored) {}
        });
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
