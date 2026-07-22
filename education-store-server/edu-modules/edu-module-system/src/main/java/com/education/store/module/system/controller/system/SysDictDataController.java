package com.education.store.module.system.controller.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.store.common.annotation.Log;
import com.education.store.common.enums.BusinessType;
import com.education.store.common.model.PageResult;
import com.education.store.common.model.Result;
import com.education.store.module.system.entity.system.SysDictData;
import com.education.store.module.system.service.ISysDictDataService;
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
 * 字典数据控制器
 */
@Tag(name = "字典数据")
@RestController
@RequestMapping("/api/v1/system/dict/data")
@RequiredArgsConstructor
public class SysDictDataController {

    private final ISysDictDataService dictDataService;

    @Operation(summary = "字典数据列表(按类型)")
    @PreAuthorize("@ss.hasPermi('system:dict:query')")
    @GetMapping("/type/{dictType}")
    public Result<List<SysDictData>> listByType(@PathVariable String dictType) {
        return Result.success(dictDataService.selectDictDataByType(dictType));
    }

    @Operation(summary = "字典数据列表(分页)")
    @PreAuthorize("@ss.hasPermi('system:dict:list')")
    @GetMapping("/list")
    public Result<PageResult<SysDictData>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String typeCode,
            @RequestParam(required = false) Integer status) {
        Page<SysDictData> p = new Page<>(page, pageSize);
        SysDictData condition = new SysDictData();
        if (keyword != null) condition.setLabel(keyword);
        if (typeCode != null) condition.setTypeCode(typeCode);
        if (status != null) condition.setStatus(status);
        IPage<SysDictData> result = dictDataService.selectDictDataList(p, condition);
        return Result.success(PageResult.of(result));
    }

    @Operation(summary = "全部字典数据(分组)")
    @PreAuthorize("@ss.hasPermi('system:dict:list')")
    @GetMapping("/all")
    public Result<Map<String, List<SysDictData>>> all() {
        List<SysDictData> allData = dictDataService.list();
        Map<String, List<SysDictData>> grouped = allData.stream()
                .collect(Collectors.groupingBy(SysDictData::getTypeCode));
        return Result.success(grouped);
    }

    @Operation(summary = "字典数据详情")
    @PreAuthorize("@ss.hasPermi('system:dict:query')")
    @GetMapping("/{dictCode}")
    public Result<SysDictData> detail(@PathVariable Long dictCode) {
        return Result.success(dictDataService.getById(dictCode));
    }

    @Operation(summary = "新增字典数据")
    @Log(title = "字典数据", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('system:dict:add')")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody SysDictData dictData) {
        dictDataService.insertDictData(dictData);
        return Result.success();
    }

    @Operation(summary = "更新字典数据")
    @Log(title = "字典数据", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('system:dict:edit')")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody SysDictData dictData) {
        dictDataService.updateDictData(dictData);
        return Result.success();
    }

    @Operation(summary = "批量删除字典数据")
    @Log(title = "字典数据", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('system:dict:delete')")
    @DeleteMapping("/{dictCodes}")
    public Result<Void> delete(@PathVariable Long[] dictCodes) {
        dictDataService.deleteDictDataByIds(dictCodes);
        return Result.success();
    }
}
