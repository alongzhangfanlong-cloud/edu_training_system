package com.education.store.module.system.controller.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.store.common.annotation.Log;
import com.education.store.common.enums.BusinessType;
import com.education.store.common.model.PageResult;
import com.education.store.common.model.Result;
import com.education.store.common.utils.SecurityUtils;
import com.education.store.module.system.entity.system.SysNotice;
import com.education.store.module.system.service.SysNoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 通知公告控制器
 */
@Tag(name = "通知公告")
@RestController
@RequestMapping("/api/v1/system/notice")
@RequiredArgsConstructor
public class SysNoticeController {

    private final SysNoticeService noticeService;

    @Operation(summary = "通知公告列表(分页)")
    @PreAuthorize("@ss.hasPermi('system:notice:list')")
    @GetMapping("/list")
    public Result<PageResult<SysNotice>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer status) {
        Page<SysNotice> p = new Page<>(page, pageSize);
        SysNotice condition = new SysNotice();
        if (keyword != null) condition.setTitle(keyword);
        if (type != null) condition.setType(type);
        if (status != null) condition.setStatus(status);
        IPage<SysNotice> result = noticeService.selectNoticeList(p, condition);
        return Result.success(PageResult.of(result));
    }

    @Operation(summary = "通知公告详情")
    @PreAuthorize("@ss.hasPermi('system:notice:query')")
    @GetMapping("/{noticeId}")
    public Result<SysNotice> detail(@PathVariable Long noticeId) {
        return Result.success(noticeService.selectNoticeById(noticeId));
    }

    @Operation(summary = "新增通知公告")
    @Log(title = "通知公告", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('system:notice:add')")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody SysNotice notice) {
        Long userId = SecurityUtils.getUserId();
        if (notice.getStatus() != null && notice.getStatus() == 1) {
            notice.setPublishBy(userId);
            notice.setPublishTime(LocalDateTime.now());
        }
        notice.setCreateBy(userId != null ? userId.toString() : "");
        noticeService.insertNotice(notice);
        return Result.success();
    }

    @Operation(summary = "更新通知公告")
    @Log(title = "通知公告", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('system:notice:edit')")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody SysNotice notice) {
        Long userId = SecurityUtils.getUserId();
        if (userId != null) {
            notice.setUpdateBy(userId.toString());
        }
        // 发布时设置发布人
        if (notice.getStatus() != null && notice.getStatus() == 1) {
            SysNotice exist = noticeService.selectNoticeById(notice.getId());
            if (exist != null && (exist.getStatus() == null || exist.getStatus() == 0)) {
                notice.setPublishBy(userId);
                notice.setPublishTime(LocalDateTime.now());
            }
        }
        noticeService.updateNotice(notice);
        return Result.success();
    }

    @Operation(summary = "批量删除通知公告")
    @Log(title = "通知公告", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('system:notice:delete')")
    @DeleteMapping("/{noticeIds}")
    public Result<Void> delete(@PathVariable Long[] noticeIds) {
        noticeService.deleteNoticeByIds(noticeIds);
        return Result.success();
    }
}
