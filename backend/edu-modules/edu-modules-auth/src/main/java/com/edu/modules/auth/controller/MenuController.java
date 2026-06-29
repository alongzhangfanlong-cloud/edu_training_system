package com.edu.modules.auth.controller;

import com.edu.common.core.response.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/menus")
@RequiredArgsConstructor
@Tag(name = "菜单管理", description = "菜单树CRUD")
public class MenuController {

    private final JdbcTemplate jdbc;

    @GetMapping("/tree")
    @Operation(summary = "获取菜单树")
    public ApiResult<List<Map<String, Object>>> tree() {
        List<Map<String, Object>> all = jdbc.queryForList(
            "SELECT id, parent_id, menu_name, menu_path, menu_icon, menu_type, perm_code, sort_order, visible, status FROM menus ORDER BY sort_order");
        return ApiResult.success(buildTree(all, 0L));
    }

    @GetMapping
    @Operation(summary = "菜单列表(平铺)")
    public ApiResult<List<Map<String, Object>>> list() {
        List<Map<String, Object>> list = jdbc.queryForList(
            "SELECT id, parent_id, menu_name, menu_path, menu_icon, menu_type, perm_code, sort_order, visible, status FROM menus ORDER BY sort_order");
        return ApiResult.success(list);
    }

    @PostMapping
    @Operation(summary = "新增菜单")
    public ApiResult<Void> create(@RequestBody Map<String, Object> body) {
        jdbc.update("INSERT INTO menus (parent_id, menu_name, menu_path, menu_icon, menu_type, perm_code, sort_order, visible) VALUES (?,?,?,?,?,?,?,?)",
                body.get("parentId"), body.get("menuName"), body.get("menuPath"),
                body.get("menuIcon"), body.get("menuType"), body.get("permCode"),
                body.get("sortOrder"), body.get("visible"));
        return ApiResult.success();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新菜单")
    public ApiResult<Void> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        jdbc.update("UPDATE menus SET menu_name=?, menu_path=?, menu_icon=?, perm_code=?, sort_order=?, visible=? WHERE id=?",
                body.get("menuName"), body.get("menuPath"), body.get("menuIcon"),
                body.get("permCode"), body.get("sortOrder"), body.get("visible"), id);
        return ApiResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除菜单")
    public ApiResult<Void> delete(@PathVariable Long id) {
        jdbc.update("DELETE FROM menus WHERE id=? OR parent_id=?", id, id);
        return ApiResult.success();
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> buildTree(List<Map<String, Object>> all, Long parentId) {
        List<Map<String, Object>> tree = new ArrayList<>();
        for (Map<String, Object> m : all) {
            Long pid = ((Number) m.get("parent_id")).longValue();
            if (pid.equals(parentId)) {
                Map<String, Object> node = new LinkedHashMap<>();
                node.put("id", m.get("id"));
                node.put("parentId", pid);
                node.put("menuName", m.get("menu_name"));
                node.put("menuPath", m.get("menu_path"));
                node.put("menuIcon", m.get("menu_icon"));
                node.put("menuType", m.get("menu_type"));
                node.put("permCode", m.get("perm_code"));
                node.put("sortOrder", m.get("sort_order"));
                node.put("visible", m.get("visible"));
                node.put("status", m.get("status"));
                List<Map<String, Object>> children = buildTree(all, ((Number) m.get("id")).longValue());
                if (!children.isEmpty()) node.put("children", children);
                tree.add(node);
            }
        }
        return tree;
    }
}
