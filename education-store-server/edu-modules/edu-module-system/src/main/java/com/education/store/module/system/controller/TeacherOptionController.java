package com.education.store.module.system.controller;

import com.education.store.common.model.Result;
import com.education.store.module.system.service.SysUserService;
import com.education.store.module.system.vo.UserSimpleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教师选项控制器 — 为排课、课酬等模块提供教师角色用户下拉数据
 */
@Tag(name = "教师选项")
@RestController
@RequestMapping("/api/v1/teachers")
@RequiredArgsConstructor
public class TeacherOptionController {

    private final SysUserService userService;

    @Operation(summary = "查询所有拥有教师角色的启用用户（用于下拉选项）")
    @GetMapping("/options")
    public Result<List<UserSimpleVO>> options(@RequestParam(required = false) String keyword) {
        return Result.success(userService.listUsersByRoleCode("teacher", keyword));
    }
}
