package com.edu.modules.auth.controller;

import com.edu.common.core.response.ApiResult;
import com.edu.common.mybatis.PageResult;
import com.edu.modules.auth.entity.UserEntity;
import com.edu.modules.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "系统用户CRUD")
public class UserController {

    private final AuthService authService;

    @GetMapping
    @Operation(summary = "用户列表")
    public ApiResult<Map<String, Object>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        List<UserEntity> users = authService.listUsers(keyword, status, page, pageSize);
        long total = authService.countUsers(keyword, status);
        List<Map<String, Object>> list = users.stream().map(u -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", u.getId()); m.put("username", u.getUsername());
            m.put("realName", u.getRealName()); m.put("phone", u.getPhone());
            m.put("email", u.getEmail()); m.put("status", u.getStatus());
            m.put("lastLoginTime", u.getLastLoginTime()); m.put("createdAt", u.getCreatedAt());
            return m;
        }).toList();
        return ApiResult.success(Map.of("list", list, "total", total, "page", page, "pageSize", pageSize));
    }

    @PostMapping
    @Operation(summary = "创建用户")
    public ApiResult<Long> create(@RequestBody Map<String, Object> body) {
        Long id = authService.createUser(body);
        return ApiResult.success("创建成功", id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新用户信息")
    public ApiResult<Void> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        authService.updateUser(id, body);
        return ApiResult.success();
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "启用/禁用用户")
    public ApiResult<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        authService.updateStatus(id, body.get("status"));
        return ApiResult.success();
    }

    @GetMapping("/menus")
    @Operation(summary = "获取当前用户菜单")
    public ApiResult<List<Map<String, Object>>> menus(@RequestHeader("Authorization") String auth) {
        // 简化: 返回默认admin菜单
        List<Map<String, Object>> menus = List.of(
            menu(1L, 0L, "工作台", "/dashboard", "DataAnalysis"),
            menu(2L, 0L, "教务管理", "", "Calendar",
                menu(3L, 2L, "排课管理", "/schedule-manage", ""),
                menu(4L, 2L, "课程管理", "/course-manage", ""),
                menu(5L, 2L, "教室管理", "/classroom-manage", ""),
                menu(6L, 2L, "教师管理", "/teacher-manage", "")),
            menu(7L, 0L, "学员管理", "/student-manage", "User"),
            menu(8L, 0L, "报名缴费", "/enrollment", "ShoppingCart"),
            menu(9L, 0L, "考勤查询", "/attendance-query", "Check"),
            menu(10L, 0L, "财务管理", "/finance", "Money"),
            menu(11L, 0L, "系统设置", "", "Setting",
                menu(12L, 11L, "用户管理", "/user-manage", ""),
                menu(13L, 11L, "系统配置", "/system-config", ""))
        );
        return ApiResult.success(menus);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> menu(Long id, Long parentId, String name, String path, String icon, Map<String, Object>... children) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", id); m.put("parentId", parentId); m.put("name", name);
        m.put("path", path); m.put("icon", icon);
        if (children.length > 0) m.put("children", List.of(children));
        return m;
    }
}
