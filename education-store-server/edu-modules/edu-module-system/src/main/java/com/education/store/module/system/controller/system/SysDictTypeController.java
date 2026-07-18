package com.education.store.module.system.controller.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.store.common.annotation.Log;
import com.education.store.common.enums.BusinessType;
import com.education.store.common.model.PageResult;
import com.education.store.common.model.Result;
import com.education.store.module.system.entity.system.SysDictType;
import com.education.store.module.system.service.ISysDictTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典类型控制器
 */
@Tag(name = "字典类型")
@RestController
@RequestMapping("/api/v1/system/dict/type")
@RequiredArgsConstructor
public class SysDictTypeController {

    private final ISysDictTypeService dictTypeService;

    @Operation(summary = "字典类型列表(分页)")
    @PreAuthorize("@ss.hasPermi('system:dict:list')")
    @GetMapping("/list")
    public Result<PageResult<SysDictType>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        Page<SysDictType> p = new Page<>(page, pageSize);
        SysDictType condition = new SysDictType();
        if (keyword != null) condition.setName(keyword);
        if (status != null) condition.setStatus(status);
        IPage<SysDictType> result = dictTypeService.selectDictTypeList(p, condition);
        return Result.success(PageResult.of(result));
    }

    @Operation(summary = "所有字典类型(下拉)")
    @GetMapping("/all")
    public Result<List<SysDictType>> all() {
        return Result.success(dictTypeService.selectDictTypeAll());
    }

    @Operation(summary = "字典类型选项(下拉)")
    @GetMapping("/optionselect")
    public Result<List<SysDictType>> optionselect() {
        return Result.success(dictTypeService.selectDictTypeAll());
    }

    @Operation(summary = "字典类型详情")
    @PreAuthorize("@ss.hasPermi('system:dict:query')")
    @GetMapping("/{dictId}")
    public Result<SysDictType> detail(@PathVariable Long dictId) {
        return Result.success(dictTypeService.getById(dictId));
    }

    @Operation(summary = "新增字典类型")
    @Log(title = "字典类型", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('system:dict:add')")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody SysDictType dictType) {
        dictTypeService.insertDictType(dictType);
        return Result.success();
    }

    @Operation(summary = "更新字典类型")
    @Log(title = "字典类型", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('system:dict:edit')")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody SysDictType dictType) {
        dictTypeService.updateDictType(dictType);
        return Result.success();
    }

    @Operation(summary = "批量删除字典类型")
    @Log(title = "字典类型", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('system:dict:delete')")
    @DeleteMapping("/{dictIds}")
    public Result<Void> delete(@PathVariable Long[] dictIds) {
        dictTypeService.deleteDictTypeByIds(dictIds);
        return Result.success();
    }

    @Operation(summary = "刷新字典缓存")
    @Log(title = "字典类型", businessType = BusinessType.OTHER)
    @PreAuthorize("@ss.hasPermi('system:dict:edit')")
    @PostMapping("/cache/refresh")
    public Result<Void> refreshCache() {
        // 当前后端无服务端字典缓存（每次查询直接走 DB），
        // 此端点主要供前端调用以清空 Pinia Store 中的缓存
        return Result.success();
    }
}
