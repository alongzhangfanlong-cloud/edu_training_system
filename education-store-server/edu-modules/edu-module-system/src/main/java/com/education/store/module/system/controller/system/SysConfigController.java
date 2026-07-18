package com.education.store.module.system.controller.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.store.common.annotation.Log;
import com.education.store.common.enums.BusinessType;
import com.education.store.common.model.PageResult;
import com.education.store.common.model.Result;
import com.education.store.module.system.entity.system.SysConfig;
import com.education.store.module.system.service.ISysConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 参数配置控制器
 */
@Tag(name = "参数配置")
@RestController
@RequestMapping("/api/v1/system/config")
@RequiredArgsConstructor
public class SysConfigController {

    private final ISysConfigService configService;

    @Operation(summary = "参数配置列表(分页)")
    @PreAuthorize("@ss.hasPermi('system:config:list')")
    @GetMapping("/list")
    public Result<PageResult<SysConfig>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String configType,
            @RequestParam(required = false) String sysBuiltIn) {
        Page<SysConfig> p = new Page<>(page, pageSize);
        SysConfig condition = new SysConfig();
        if (keyword != null && !keyword.isEmpty()) {
            condition.setName(keyword);
        }
        if (configType != null && !configType.isEmpty()) {
            condition.setKey(configType);
        }
        if (sysBuiltIn != null && !sysBuiltIn.isEmpty()) {
            condition.setType(sysBuiltIn);
        }
        IPage<SysConfig> result = configService.selectConfigList(p, condition);
        return Result.success(PageResult.of(result));
    }

    @Operation(summary = "参数配置详情")
    @PreAuthorize("@ss.hasPermi('system:config:query')")
    @GetMapping("/{configId}")
    public Result<SysConfig> detail(@PathVariable Long configId) {
        return Result.success(configService.getById(configId));
    }

    @Operation(summary = "根据键名查询配置")
    @GetMapping("/configKey/{configKey}")
    public Result<String> getByKey(@PathVariable String configKey) {
        return Result.success(configService.selectConfigByKey(configKey));
    }

    @Operation(summary = "新增参数配置")
    @Log(title = "参数配置", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('system:config:add')")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody SysConfig config) {
        if (!configService.checkConfigKeyUnique(config)) {
            return Result.error("参数键名已存在");
        }
        configService.insertConfig(config);
        return Result.success();
    }

    @Operation(summary = "更新参数配置")
    @Log(title = "参数配置", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('system:config:edit')")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody SysConfig config) {
        configService.updateConfig(config);
        return Result.success();
    }

    @Operation(summary = "批量删除参数配置")
    @Log(title = "参数配置", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('system:config:delete')")
    @DeleteMapping("/{configIds}")
    public Result<Void> delete(@PathVariable Long[] configIds) {
        configService.deleteConfigByIds(configIds);
        return Result.success();
    }

    @Operation(summary = "刷新参数缓存")
    @Log(title = "参数配置", businessType = BusinessType.CLEAN)
    @PreAuthorize("@ss.hasPermi('system:config:edit')")
    @PostMapping("/refreshCache")
    public Result<Void> refreshCache() {
        configService.resetConfigCache();
        return Result.success();
    }
}
