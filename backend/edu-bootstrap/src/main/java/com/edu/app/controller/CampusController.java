package com.edu.app.controller;

import com.edu.common.core.response.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/campuses")
@RequiredArgsConstructor
public class CampusController {

    private final JdbcTemplate jdbc;

    @GetMapping
    public ApiResult<List<Map<String, Object>>> list() {
        return ApiResult.success(jdbc.queryForList(
            "SELECT id, campus_name, address, phone, principal_id, status FROM campuses ORDER BY id"));
    }

    @GetMapping("/{id}")
    public ApiResult<Map<String, Object>> get(@PathVariable Long id) {
        return ApiResult.success(jdbc.queryForMap(
            "SELECT * FROM campuses WHERE id=?", id));
    }

    @PostMapping
    public ApiResult<Void> create(@RequestBody Map<String, Object> body) {
        jdbc.update("INSERT INTO campuses (campus_name, address, phone, principal_id) VALUES (?,?,?,?)",
                body.get("campusName"), body.get("address"), body.get("phone"), body.get("principalId"));
        return ApiResult.success();
    }

    @PutMapping("/{id}")
    public ApiResult<Void> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        jdbc.update("UPDATE campuses SET campus_name=?, address=?, phone=?, principal_id=? WHERE id=?",
                body.get("campusName"), body.get("address"), body.get("phone"), body.get("principalId"), id);
        return ApiResult.success();
    }

    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) {
        jdbc.update("DELETE FROM campuses WHERE id=?", id);
        return ApiResult.success();
    }
}
