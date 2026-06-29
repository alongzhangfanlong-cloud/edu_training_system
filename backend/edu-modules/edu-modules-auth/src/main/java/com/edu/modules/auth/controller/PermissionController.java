package com.edu.modules.auth.controller;

import com.edu.common.core.response.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
@Tag(name = "权限管理", description = "权限列表及角色权限分配")
public class PermissionController {

    private final JdbcTemplate jdbc;

    @GetMapping
    @Operation(summary = "权限列表")
    public ApiResult<List<Map<String, Object>>> list() {
        List<Map<String, Object>> list = jdbc.queryForList(
            "SELECT id, perm_code, perm_name, perm_type, parent_id, sort_order FROM permissions ORDER BY sort_order");
        return ApiResult.success(list);
    }

    @GetMapping("/role-permissions")
    @Operation(summary = "角色权限矩阵")
    public ApiResult<Map<String, Object>> rolePermissionMatrix() {
        // 所有角色
        List<Map<String, Object>> roles = jdbc.queryForList("SELECT id, role_code, role_name FROM roles WHERE status=1");
        // 所有权限
        List<Map<String, Object>> perms = jdbc.queryForList("SELECT id, perm_code, perm_name, perm_type FROM permissions ORDER BY sort_order");
        // 角色已有权限
        List<Map<String, Object>> rp = jdbc.queryForList("SELECT role_id, permission_id FROM role_permissions");
        Set<String> rpSet = new HashSet<>();
        for (Map<String, Object> r : rp) {
            rpSet.add(r.get("role_id") + "_" + r.get("permission_id"));
        }

        // 构建矩阵
        List<Map<String, Object>> matrix = new ArrayList<>();
        for (Map<String, Object> perm : perms) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("permissionId", perm.get("id"));
            row.put("permCode", perm.get("perm_code"));
            row.put("permName", perm.get("perm_name"));
            row.put("permType", perm.get("perm_type"));
            Map<String, Boolean> rolePerms = new LinkedHashMap<>();
            for (Map<String, Object> role : roles) {
                String key = role.get("id") + "_" + perm.get("id");
                rolePerms.put((String) role.get("role_code"), rpSet.contains(key));
            }
            row.put("roles", rolePerms);
            matrix.add(row);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("roles", roles);
        result.put("matrix", matrix);
        return ApiResult.success(result);
    }

    @PostMapping("/assign")
    @Operation(summary = "分配角色权限")
    public ApiResult<Void> assign(@RequestBody Map<String, Object> body) {
        Long roleId = ((Number) body.get("roleId")).longValue();
        @SuppressWarnings("unchecked")
        List<Integer> permIds = (List<Integer>) body.get("permissionIds");

        jdbc.update("DELETE FROM role_permissions WHERE role_id=?", roleId);
        for (Integer pid : permIds) {
            jdbc.update("INSERT INTO role_permissions (role_id, permission_id) VALUES (?,?)", roleId, pid);
        }
        return ApiResult.success();
    }
}
