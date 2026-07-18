package com.education.store.module.system.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.store.common.annotation.Log;
import com.education.store.common.enums.BusinessType;
import com.education.store.common.exception.BusinessException;
import com.education.store.common.model.Result;
import com.education.store.module.system.dto.RoleCreateDTO;
import com.education.store.module.system.dto.RoleUpdateDTO;
import com.education.store.module.system.entity.system.SysRole;
import com.education.store.module.system.service.SysRoleService;
import com.education.store.module.system.vo.RoleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 角色管理控制器
 */
@Validated
@Tag(name = "角色管理")
@RestController
@RequestMapping("/api/v1/system/role")
@RequiredArgsConstructor
public class SysRoleController {

    private final SysRoleService roleService;

    @Operation(summary = "分页查询角色列表")
    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping("/list")
    public Result<IPage<SysRole>> list(@RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer size,
                                        @RequestParam(required = false) String keyword,
                                        @RequestParam(required = false) Integer status,
                                        @RequestParam(required = false) LocalDateTime beginTime,
                                        @RequestParam(required = false) LocalDateTime endTime) {
        Page<SysRole> p = new Page<>(page, size);
        SysRole condition = new SysRole();
        condition.setStatus(status);
        condition.setName(keyword); // Service 层使用 OR 连接 name/code
        condition.setBeginTime(beginTime);
        condition.setEndTime(endTime);
        IPage<SysRole> result = roleService.selectRoleList(p, condition);
        return Result.success(result);
    }

    @Operation(summary = "全部角色")
    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping("/all")
    public Result<List<SysRole>> all() {
        return Result.success(roleService.selectRoleAll());
    }

    @Operation(summary = "角色详情")
    @PreAuthorize("@ss.hasPermi('system:role:query')")
    @GetMapping("/{id}")
    public Result<RoleVO> get(@PathVariable Long id) {
        SysRole role = roleService.getById(id);
        if (role == null) {
            return Result.error("角色不存在");
        }
        RoleVO vo = BeanUtil.copyProperties(role, RoleVO.class);
        vo.setMenuIds(roleService.selectMenuIdsByRoleId(id));
        return Result.success(vo);
    }

    @Operation(summary = "新增角色")
    @Log(title = "角色管理", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('system:role:add')")
    @PostMapping
    public Result<Map<String, Long>> create(@Valid @RequestBody RoleCreateDTO dto) {
        SysRole role = BeanUtil.copyProperties(dto, SysRole.class);

        // 校验角色编码唯一性
        if (!roleService.checkRoleCodeUnique(role)) {
            return Result.error("角色编码已存在");
        }

        role.setMenuIds(dto.getMenuIds());
        roleService.insertRole(role);
        return Result.success(Map.of("roleId", role.getId()));
    }

    @Operation(summary = "更新角色")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody RoleUpdateDTO dto) {
        SysRole exist = roleService.getById(id);
        if (exist == null) {
            return Result.error("角色不存在");
        }

        // 不允许操作超级管理员角色
        roleService.checkRoleAllowed(exist);

        dto.setId(id);
        SysRole role = BeanUtil.copyProperties(dto, SysRole.class);

        // 校验角色编码唯一性
        if (!roleService.checkRoleCodeUnique(role)) {
            return Result.error("角色编码已存在");
        }

        role.setMenuIds(dto.getMenuIds());
        roleService.updateRole(role);
        return Result.success();
    }

    @Operation(summary = "启/停用角色")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        SysRole exist = roleService.getById(id);
        if (exist == null) {
            return Result.error("角色不存在");
        }
        roleService.checkRoleAllowed(exist);

        Integer status = body.get("status");
        if (status == null || (status != 0 && status != 1)) {
            return Result.error("状态值无效，必须为 0 或 1");
        }

        SysRole role = new SysRole();
        role.setId(id);
        role.setStatus(status);
        roleService.updateRoleStatus(role);
        return Result.success();
    }

    @Operation(summary = "查询角色已分配的用户ID列表")
    @PreAuthorize("@ss.hasPermi('system:role:query')")
    @GetMapping("/{id}/auth-user")
    public Result<Map<String, List<Long>>> getAuthUsers(@PathVariable Long id) {
        SysRole exist = roleService.getById(id);
        if (exist == null) {
            return Result.error("角色不存在");
        }
        List<Long> userIds = roleService.selectUserIdsByRoleId(id);
        return Result.success(Map.of("userIds", userIds));
    }

    @Operation(summary = "分配用户（全量替换）")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @PutMapping("/{id}/auth-user")
    public Result<Void> updateAuthUsers(@PathVariable Long id, @RequestBody Map<String, List<Long>> body) {
        SysRole exist = roleService.getById(id);
        if (exist == null) {
            return Result.error("角色不存在");
        }
        roleService.checkRoleAllowed(exist);

        List<Long> userIdList = body.get("userIds");
        Long[] userIds = userIdList != null ? userIdList.toArray(Long[]::new) : new Long[0];
        roleService.authUserAllot(id, userIds);
        return Result.success();
    }

    @Operation(summary = "批量删除角色")
    @Log(title = "角色管理", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('system:role:delete')")
    @DeleteMapping("/{ids}")
    public Result<Void> delete(@PathVariable String ids) {
        Long[] idArray;
        try {
            idArray = Arrays.stream(ids.split(","))
                    .map(String::trim)
                    .map(Long::valueOf)
                    .toArray(Long[]::new);
        } catch (NumberFormatException e) {
            return Result.error("角色ID格式不正确");
        }

        // 批量查询角色，校验存在性
        List<SysRole> roles = roleService.listByIds(Arrays.asList(idArray));
        if (roles.size() != idArray.length) {
            return Result.error("角色不存在");
        }

        // 校验 admin 角色保护 + 关联用户检查（事务内完成）
        try {
            roleService.deleteRoleByIds(idArray);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        }

        return Result.success();
    }
}
