package com.education.store.module.prospect.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.store.common.annotation.Log;
import com.education.store.common.enums.BusinessType;
import com.education.store.common.model.PageResult;
import com.education.store.common.model.Result;
import com.education.store.module.prospect.entity.EduProspectFollowup;
import com.education.store.module.prospect.service.IEduProspectFollowupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 跟进记录管理控制器
 */
@Tag(name = "跟进记录")
@RestController
@RequestMapping("/api/v1/prospects/{prospectId}/followups")
@RequiredArgsConstructor
public class EduProspectFollowupController {

    private final IEduProspectFollowupService followupService;

    @Operation(summary = "跟进记录列表")
    @PreAuthorize("@ss.hasPermi('student:prospect:list')")
    @GetMapping("/list")
    public Result<PageResult<EduProspectFollowup>> list(
            @PathVariable Long prospectId,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize) {
        Page<EduProspectFollowup> p = new Page<>(page, pageSize);
        IPage<EduProspectFollowup> result = followupService.selectFollowupList(p, prospectId);
        return Result.success(PageResult.of(result));
    }

    @Operation(summary = "新增跟进记录")
    @Log(title = "跟进记录", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('student:prospect:followup')")
    @PostMapping
    public Result<Void> create(@PathVariable Long prospectId,
                               @Valid @RequestBody EduProspectFollowup followup) {
        followup.setProspectId(prospectId);
        followupService.insertFollowup(followup);
        return Result.success();
    }

    @Operation(summary = "更新跟进记录")
    @Log(title = "跟进记录", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('student:prospect:followup')")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long prospectId, @PathVariable Long id,
                               @Valid @RequestBody EduProspectFollowup followup) {
        followup.setId(id);
        followup.setProspectId(prospectId);
        followupService.updateFollowup(followup);
        return Result.success();
    }

    @Operation(summary = "删除跟进记录")
    @Log(title = "跟进记录", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('student:prospect:followup')")
    @DeleteMapping("/{ids}")
    public Result<Void> delete(@PathVariable Long prospectId, @PathVariable Long[] ids) {
        followupService.deleteFollowupByIds(ids);
        return Result.success();
    }
}
