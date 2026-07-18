package com.education.store.module.system.controller;

import cn.hutool.core.bean.BeanUtil;
import com.education.store.common.model.Result;
import com.education.store.module.system.dto.MenuCreateDTO;
import com.education.store.module.system.dto.MenuUpdateDTO;
import com.education.store.module.system.entity.system.SysMenu;
import com.education.store.module.system.service.SysMenuService;
import com.education.store.module.system.vo.MenuTreeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 菜单管理控制器
 */
@Tag(name = "菜单管理")
@RestController
@RequestMapping("/api/v1/system/menu")
@RequiredArgsConstructor
public class SysMenuController {

    private final SysMenuService menuService;

    @Operation(summary = "菜单树（全部）")
    @PreAuthorize("@ss.hasPermi('system:menu:list')")
    @GetMapping("/tree")
    public Result<List<MenuTreeVO>> tree() {
        List<SysMenu> menus = menuService.selectMenuTreeAll();
        return Result.success(convertToMenuTreeVO(menus));
    }

    @Operation(summary = "用户菜单树")
    @PreAuthorize("@ss.hasPermi('system:menu:list')")
    @GetMapping("/tree/{userId}")
    public Result<List<MenuTreeVO>> userTree(@PathVariable Long userId) {
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        return Result.success(convertToMenuTreeVO(menus));
    }

    private List<MenuTreeVO> convertToMenuTreeVO(List<SysMenu> menus) {
        return menus.stream().map(m -> {
            MenuTreeVO vo = BeanUtil.copyProperties(m, MenuTreeVO.class, "children");
            if (m.getChildren() != null && !m.getChildren().isEmpty()) {
                vo.setChildren(convertToMenuTreeVO(m.getChildren()));
            }
            return vo;
        }).collect(Collectors.toList());
    }

    @Operation(summary = "菜单详情")
    @PreAuthorize("@ss.hasPermi('system:menu:query')")
    @GetMapping("/{id}")
    public Result<SysMenu> get(@PathVariable Long id) {
        return Result.success(menuService.selectMenuById(id));
    }

    @Operation(summary = "新增菜单")
    @PreAuthorize("@ss.hasPermi('system:menu:add')")
    @PostMapping
    public Result<Map<String, Long>> create(@Valid @RequestBody MenuCreateDTO dto) {
        // 校验父菜单存在性
        if (dto.getParentId() != null && dto.getParentId() != 0) {
            SysMenu parent = menuService.selectMenuById(dto.getParentId());
            if (parent == null) {
                return Result.error("父菜单不存在");
            }
        }
        SysMenu menu = BeanUtil.copyProperties(dto, SysMenu.class);

        // 校验同父节点下名称唯一
        if (!menuService.checkMenuNameUnique(menu)) {
            return Result.error("同父节点下已存在同名菜单");
        }

        menuService.insertMenu(menu);
        return Result.success(Map.of("menuId", menu.getId()));
    }

    @Operation(summary = "更新菜单")
    @PreAuthorize("@ss.hasPermi('system:menu:edit')")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody MenuUpdateDTO dto) {
        dto.setId(id);
        SysMenu menu = BeanUtil.copyProperties(dto, SysMenu.class);

        // 校验菜单存在性
        SysMenu exist = menuService.selectMenuById(id);
        if (exist == null) {
            return Result.error("菜单不存在");
        }

        // 校验父菜单存在性
        if (menu.getParentId() != null && menu.getParentId() != 0) {
            SysMenu parent = menuService.selectMenuById(menu.getParentId());
            if (parent == null) {
                return Result.error("父菜单不存在");
            }
        }

        // 校验同父节点下名称唯一
        if (!menuService.checkMenuNameUnique(menu)) {
            return Result.error("同父节点下已存在同名菜单");
        }

        menuService.updateMenu(menu);
        return Result.success();
    }

    @Operation(summary = "删除菜单")
    @PreAuthorize("@ss.hasPermi('system:menu:delete')")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        menuService.deleteMenuById(id);
        return Result.success();
    }
}
