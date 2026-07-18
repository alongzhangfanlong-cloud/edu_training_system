package com.education.store.module.student.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.store.common.annotation.Log;
import com.education.store.common.enums.BusinessType;
import com.education.store.common.model.PageResult;
import com.education.store.common.model.Result;
import com.education.store.module.student.entity.EduStudentParent;
import com.education.store.module.student.service.IEduStudentParentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 家长关联管理控制器
 */
@Tag(name = "家长管理")
@RestController
@RequestMapping("/api/v1/students/{studentId}/parents")
@RequiredArgsConstructor
public class EduStudentParentController {

    private final IEduStudentParentService parentService;

    @Operation(summary = "家长列表")
    @PreAuthorize("@ss.hasPermi('student:parent:list')")
    @GetMapping("/list")
    public Result<PageResult<EduStudentParent>> list(
            @PathVariable Long studentId,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize) {
        Page<EduStudentParent> p = new Page<>(page, pageSize);
        IPage<EduStudentParent> result = parentService.selectParentList(p, studentId);
        return Result.success(PageResult.of(result));
    }

    @Operation(summary = "添加家长关联")
    @Log(title = "家长管理", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('student:parent:add')")
    @PostMapping
    public Result<Void> create(@PathVariable Long studentId, @Valid @RequestBody EduStudentParent parent) {
        parent.setStudentId(studentId);
        if (!parentService.checkMobileUnique(parent)) {
            return Result.error("该手机号已与该学员关联");
        }
        parentService.insertParent(parent);
        return Result.success();
    }

    @Operation(summary = "更新家长信息")
    @Log(title = "家长管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('student:parent:edit')")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long studentId, @PathVariable Long id,
                               @Valid @RequestBody EduStudentParent parent) {
        parent.setId(id);
        parent.setStudentId(studentId);
        if (!parentService.checkMobileUnique(parent)) {
            return Result.error("该手机号已与该学员关联");
        }
        parentService.updateParent(parent);
        return Result.success();
    }

    @Operation(summary = "解绑家长（仅删关联，不删用户账号）")
    @Log(title = "家长管理", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('student:parent:delete')")
    @DeleteMapping("/{ids}")
    public Result<Void> delete(@PathVariable Long studentId, @PathVariable Long[] ids) {
        parentService.deleteParentByIds(ids);
        return Result.success();
    }
}
