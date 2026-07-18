package com.education.store.module.system.controller.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.store.common.annotation.Log;
import com.education.store.common.enums.BusinessType;
import com.education.store.common.model.PageResult;
import com.education.store.common.model.Result;
import com.education.store.module.system.entity.system.SysOperLog;
import com.education.store.module.system.mapper.SysOperLogMapper;
import com.education.store.module.system.service.ISysOperLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * 操作日志控制器
 */
@Tag(name = "操作日志")
@RestController
@RequestMapping("/api/v1/system/log/operlog")
@RequiredArgsConstructor
public class SysOperLogController {

    private final ISysOperLogService operLogService;
    private final SysOperLogMapper operLogMapper;

    @Operation(summary = "操作日志列表(分页)")
    @PreAuthorize("@ss.hasPermi('system:log:list')")
    @GetMapping("/list")
    public Result<PageResult<SysOperLog>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String operType,
            @RequestParam(required = false) Integer status) {
        Page<SysOperLog> p = new Page<>(page, pageSize);
        SysOperLog condition = new SysOperLog();
        if (keyword != null && !keyword.isEmpty()) condition.setOperName(keyword);
        if (operType != null && !operType.isEmpty()) condition.setBusinessType(operType);
        if (status != null) condition.setStatus(status);
        IPage<SysOperLog> result = operLogService.selectOperLogList(p, condition);
        return Result.success(PageResult.of(result));
    }

    @Operation(summary = "操作日志详情")
    @GetMapping("/{operId}")
    public Result<SysOperLog> detail(@PathVariable Long operId) {
        return Result.success(operLogService.getById(operId));
    }

    @Operation(summary = "批量删除操作日志")
    @Log(title = "操作日志", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('system:log:delete')")
    @DeleteMapping("/{operIds}")
    public Result<Void> delete(@PathVariable Long[] operIds) {
        operLogMapper.deleteBatchIds(Arrays.asList(operIds));
        return Result.success();
    }

    @Operation(summary = "清空操作日志")
    @Log(title = "操作日志", businessType = BusinessType.CLEAN)
    @PreAuthorize("@ss.hasPermi('system:log:delete')")
    @DeleteMapping("/clean")
    public Result<Void> clean() {
        operLogService.cleanOperLog();
        return Result.success();
    }
}
