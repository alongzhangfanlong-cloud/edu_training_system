package com.education.store.module.system.controller.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.store.common.annotation.Log;
import com.education.store.common.enums.BusinessType;
import com.education.store.common.model.PageResult;
import com.education.store.common.model.Result;
import com.education.store.module.system.entity.system.SysLogininfor;
import com.education.store.module.system.mapper.SysLogininforMapper;
import com.education.store.module.system.service.ISysLogininforService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * 登录日志控制器
 */
@Tag(name = "登录日志")
@RestController
@RequestMapping("/api/v1/system/log/logininfor")
@RequiredArgsConstructor
public class SysLogininforController {

    private final ISysLogininforService logininforService;
    private final SysLogininforMapper logininforMapper;

    @Operation(summary = "登录日志列表(分页)")
    @PreAuthorize("@ss.hasPermi('system:log:list')")
    @GetMapping("/list")
    public Result<PageResult<SysLogininfor>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String ipaddr,
            @RequestParam(required = false) Integer status) {
        Page<SysLogininfor> p = new Page<>(page, pageSize);
        SysLogininfor condition = new SysLogininfor();
        if (username != null && !username.isEmpty()) condition.setUsername(username);
        if (ipaddr != null && !ipaddr.isEmpty()) condition.setIpaddr(ipaddr);
        if (status != null) condition.setStatus(status);
        IPage<SysLogininfor> result = logininforService.selectLogininforList(p, condition);
        return Result.success(PageResult.of(result));
    }

    @Operation(summary = "登录日志详情")
    @PreAuthorize("@ss.hasPermi('system:log:list')")
    @GetMapping("/{infoId}")
    public Result<SysLogininfor> detail(@PathVariable Long infoId) {
        return Result.success(logininforService.getById(infoId));
    }

    @Operation(summary = "批量删除登录日志")
    @Log(title = "登录日志", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('system:log:delete')")
    @DeleteMapping("/{infoIds}")
    public Result<Void> delete(@PathVariable Long[] infoIds) {
        logininforMapper.deleteBatchIds(Arrays.asList(infoIds));
        return Result.success();
    }

    @Operation(summary = "清空登录日志")
    @Log(title = "登录日志", businessType = BusinessType.CLEAN)
    @PreAuthorize("@ss.hasPermi('system:log:delete')")
    @DeleteMapping("/clean")
    public Result<Void> clean() {
        logininforService.cleanLogininfor();
        return Result.success();
    }
}
