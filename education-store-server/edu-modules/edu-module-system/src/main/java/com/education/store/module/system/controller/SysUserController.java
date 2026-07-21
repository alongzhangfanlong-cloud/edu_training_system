package com.education.store.module.system.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.store.common.model.Result;
import com.education.store.common.utils.SecurityUtils;
import com.education.store.module.system.dto.RouterVo;
import com.education.store.module.system.dto.UserCreateDTO;
import com.education.store.module.system.dto.UserQueryDTO;
import com.education.store.module.system.dto.UserUpdateDTO;
import com.education.store.module.system.entity.system.SysUser;
import com.education.store.module.system.service.SysMenuService;
import com.education.store.module.system.service.SysRoleService;
import com.education.store.module.system.service.SysUserService;
import com.education.store.module.system.vo.UserDetailVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户管理控制器
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/v1/system/user")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService userService;
    private final SysRoleService roleService;
    private final SysMenuService menuService;

    @Value("${edu.user.default-password:123456}")
    private String defaultPassword;

    @Operation(summary = "分页查询用户列表")
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/list")
    public Result<IPage<SysUser>> list(UserQueryDTO query) {
        Page<SysUser> page = new Page<>(query.getPage(), query.getSize());
        SysUser condition = new SysUser();
        condition.setStatus(query.getStatus());
        if (query.getKeyword() != null) {
            condition.setUsername(query.getKeyword());
            condition.setRealName(query.getKeyword());
            condition.setMobile(query.getKeyword());
        }
        IPage<SysUser> result = userService.selectUserList(page, condition);
        return Result.success(result);
    }

    @Operation(summary = "用户详情")
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping("/{id}")
    public Result<UserDetailVO> get(@PathVariable Long id) {
        SysUser user = userService.selectUserById(id);
        UserDetailVO vo = buildDetailVO(user);
        return Result.success(vo);
    }

    @Operation(summary = "新增用户")
    @PreAuthorize("@ss.hasPermi('system:user:add')")
    @PostMapping
    public Result<Map<String, Long>> create(@Valid @RequestBody UserCreateDTO dto) {
        SysUser user = BeanUtil.copyProperties(dto, SysUser.class);
        user.setRoleIds(dto.getRoleIds());
        userService.insertUser(user);
        return Result.success(Map.of("userId", user.getId()));
    }

    @Operation(summary = "更新用户")
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO dto) {
        dto.setId(id);
        SysUser user = BeanUtil.copyProperties(dto, SysUser.class);
        user.setRoleIds(dto.getRoleIds());
        userService.updateUser(user);
        return Result.success();
    }

    @Operation(summary = "启/停用用户")
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setStatus(body.get("status"));
        userService.updateUserStatus(user);
        return Result.success();
    }

    @Operation(summary = "重置密码")
    @PreAuthorize("@ss.hasPermi('system:user:resetPwd')")
    @PutMapping("/{id}/password")
    public Result<Void> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> body) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setPassword(body.get("password"));
        userService.resetPwd(user);
        return Result.success();
    }

    @Operation(summary = "批量删除用户")
    @PreAuthorize("@ss.hasPermi('system:user:delete')")
    @DeleteMapping("/{ids}")
    public Result<Void> delete(@PathVariable String ids) {
        Long[] idArray = Arrays.stream(ids.split(","))
                .map(String::trim)
                .map(Long::valueOf)
                .toArray(Long[]::new);
        userService.deleteUserByIds(idArray);
        return Result.success();
    }

    @Operation(summary = "查询用户角色")
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping("/{id}/role")
    public Result<Map<String, List<Long>>> getUserRoles(@PathVariable Long id) {
        List<Long> roleIds = userService.selectUserRoleIds(id);
        return Result.success(Map.of("roleIds", roleIds));
    }

    @Operation(summary = "更新用户角色")
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @PutMapping("/{id}/role")
    public Result<Void> updateUserRoles(@PathVariable Long id, @RequestBody Map<String, List<Long>> body) {
        List<Long> roleIds = body.get("roleIds");
        userService.insertUserAuth(id, roleIds != null ? roleIds.toArray(new Long[0]) : new Long[0]);
        return Result.success();
    }

    @Operation(summary = "获取当前用户信息")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/getInfo")
    public Result<Map<String, Object>> getInfo() {
        Long userId = SecurityUtils.getUserId();
        SysUser user = userService.selectUserById(userId);
        List<String> roles = roleService.selectRolesByUserId(userId).stream()
                .map(r -> r.getCode())
                .collect(Collectors.toList());
        Set<String> permissions = menuService.selectMenuPermsByUserId(userId);

        Map<String, Object> userInfo = new LinkedHashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("realName", user.getRealName());
        userInfo.put("avatar", user.getAvatar());
        userInfo.put("email", user.getEmail());
        userInfo.put("mobile", user.getMobile());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("user", userInfo);
        result.put("roles", roles);
        result.put("permissions", permissions);
        return Result.success(result);
    }

    @Operation(summary = "获取当前用户路由菜单")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/getRouters")
    public Result<List<RouterVo>> getRouters() {
        Long userId = SecurityUtils.getUserId();
        List<com.education.store.module.system.entity.system.SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        List<RouterVo> routers = menuService.buildRouters(menus);
        return Result.success(routers);
    }

    private static final String EXCEL_MIME = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final int IMPORT_MAX_ROWS = 10000;
    private static final DataFormatter FORMATTER = new DataFormatter();

    @Operation(summary = "下载导入模板")
    @PreAuthorize("@ss.hasPermi('system:user:import')")
    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadTemplate() throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("用户导入模板");
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            String[] headers = {"用户名（必填）", "密码（默认123456）", "真实姓名", "昵称", "邮箱", "手机号", "性别（1男2女0未知）", "备注"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            int[] widths = {18, 20, 14, 14, 24, 18, 22, 20};
            for (int i = 0; i < widths.length; i++) {
                sheet.setColumnWidth(i, widths[i] * 256);
            }

            workbook.write(bos);
            return buildExcelResponse(bos.toByteArray(), "用户导入模板.xlsx");
        }
    }

    @Operation(summary = "导入用户")
    @PreAuthorize("@ss.hasPermi('system:user:import')")
    @PostMapping("/import")
    public Result<String> importUser(@RequestParam(value = "file", required = false) MultipartFile file,
                                       @RequestParam(defaultValue = "false") boolean updateSupport) throws IOException {
        if (file == null || file.isEmpty()) {
            return Result.error("上传文件不能为空");
        }
        // 文件大小限制 10MB
        if (file.getSize() > 10 * 1024 * 1024) {
            return Result.error("文件大小不能超过 10MB");
        }
        String originalName = file.getOriginalFilename();
        if (originalName == null || (!originalName.endsWith(".xlsx") && !originalName.endsWith(".xls"))) {
            return Result.error("仅支持 .xlsx 或 .xls 格式的 Excel 文件");
        }

        List<SysUser> users;
        try {
            users = parseUserExcel(file);
        } catch (Exception e) {
            return Result.error("文件解析失败，请检查文件格式是否正确");
        }

        if (users.isEmpty()) {
            return Result.error("未从文件中读取到任何用户数据，请检查文件格式");
        }

        String operatorName = SecurityUtils.getUsername();
        String msg = userService.importUser(users, updateSupport, operatorName);
        return Result.success(msg);
    }

    @Operation(summary = "导出用户")
    @PreAuthorize("@ss.hasPermi('system:user:export')")
    @GetMapping("/export")
    public ResponseEntity<byte[]> export(UserQueryDTO query) {
        byte[] bytes = userService.exportUser(query.getKeyword(), query.getStatus());
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return buildExcelResponse(bytes, "用户数据_" + dateStr + ".xlsx");
    }

    // ======== Excel 辅助方法 ========

    /** 构建 Excel 文件下载响应 */
    private ResponseEntity<byte[]> buildExcelResponse(byte[] data, String rawFilename) {
        String filename = URLEncoder.encode(rawFilename, StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(EXCEL_MIME))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + filename)
                .body(data);
    }

    /** 安全读取单元格字符串（使用 DataFormatter 处理所有类型） */
    private String getCellString(Row row, int idx) {
        Cell cell = row.getCell(idx);
        if (cell == null) return null;
        String val = FORMATTER.formatCellValue(cell).trim();
        return val.isEmpty() ? null : val;
    }

    private Long getCellLong(Row row, int idx) {
        String val = getCellString(row, idx);
        if (val == null || val.isBlank()) return null;
        try { return Long.parseLong(val); }
        catch (NumberFormatException e) { return null; }
    }

    /** 解析性别列：支持数字(1/2/0)和中文(男/女) */
    private Integer parseGender(String s) {
        if (s == null || s.isBlank()) return null;
        String t = s.trim();
        if ("男".equals(t) || "1".equals(t)) return 1;
        if ("女".equals(t) || "2".equals(t)) return 2;
        if ("0".equals(t)) return 0;
        try { return Integer.parseInt(t); } catch (NumberFormatException e) { return null; }
    }

    private List<SysUser> parseUserExcel(MultipartFile file) throws IOException {
        List<SysUser> users = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            int totalRows = sheet.getLastRowNum();
            if (totalRows < 1) {
                throw new IOException("文件内容为空，请下载模板并按格式填写数据");
            }
            if (totalRows > IMPORT_MAX_ROWS) {
                throw new IOException("单次导入不能超过 " + IMPORT_MAX_ROWS + " 行，当前文件包含 " + totalRows + " 行");
            }
            for (int i = 1; i <= totalRows; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String username = getCellString(row, 0);
                if (username == null || username.isBlank()) continue;

                SysUser user = new SysUser();
                user.setUsername(username.trim());
                String password = getCellString(row, 1);
                user.setPassword(password != null && !password.isBlank() ? password.trim() : defaultPassword);
                user.setRealName(getCellString(row, 2));
                user.setNickName(getCellString(row, 3));
                user.setEmail(getCellString(row, 4));
                user.setMobile(getCellString(row, 5));
                user.setGender(parseGender(getCellString(row, 6)));
                user.setRemark(getCellString(row, 7));
                users.add(user);
            }
        }
        return users;
    }

    private UserDetailVO buildDetailVO(SysUser user) {
        UserDetailVO vo = BeanUtil.copyProperties(user, UserDetailVO.class);

        // 角色信息
        List<Long> roleIds = userService.selectUserRoleIds(user.getId());
        vo.setRoleIds(roleIds);
        if (roleIds != null && !roleIds.isEmpty()) {
            vo.setRoles(roleService.listByIds(roleIds).stream()
                    .map(r -> {
                        UserDetailVO.RoleSimple rs = new UserDetailVO.RoleSimple();
                        rs.setId(r.getId());
                        rs.setName(r.getName());
                        rs.setCode(r.getCode());
                        return rs;
                    })
                    .collect(Collectors.toList()));
        } else {
            vo.setRoles(Collections.emptyList());
        }

        return vo;
    }
}
