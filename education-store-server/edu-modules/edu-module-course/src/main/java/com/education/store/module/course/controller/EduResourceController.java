package com.education.store.module.course.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.store.common.annotation.Log;
import com.education.store.common.enums.BusinessType;
import com.education.store.common.model.PageResult;
import com.education.store.common.model.Result;
import com.education.store.module.course.entity.EduResource;
import com.education.store.module.course.service.IEduResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 课件资料管理控制器
 */
@Tag(name = "课件管理")
@RestController
@RequestMapping("/api/v1/resources")
@RequiredArgsConstructor
public class EduResourceController {

    private final IEduResourceService resourceService;

    @Operation(summary = "课件列表(分页)")
    @PreAuthorize("@ss.hasPermi('course:resource:list')")
    @GetMapping("/list")
    public Result<PageResult<EduResource>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) String resourceType,
            @RequestParam(required = false) String grade) {
        Page<EduResource> p = new Page<>(page, pageSize);
        EduResource condition = new EduResource();
        condition.setName(name);
        condition.setSubjectId(subjectId);
        condition.setResourceType(resourceType);
        condition.setGrade(grade);
        IPage<EduResource> result = resourceService.selectResourceList(p, condition);
        return Result.success(PageResult.of(result));
    }

    @Operation(summary = "课件详情")
    @PreAuthorize("@ss.hasPermi('course:resource:query')")
    @GetMapping("/{id}")
    public Result<EduResource> detail(@PathVariable Long id) {
        return Result.success(resourceService.selectResourceById(id));
    }

    @Operation(summary = "上传课件")
    @Log(title = "课件管理", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('course:resource:add')")
    @PostMapping
    public Result<Void> create(
            @Valid @RequestPart("metadata") EduResource resource,
            @RequestPart("file") MultipartFile file) {
        resourceService.insertResource(resource, file);
        return Result.success();
    }

    @Operation(summary = "更新课件")
    @Log(title = "课件管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('course:resource:edit')")
    @PutMapping("/{id}")
    public Result<Void> update(
            @PathVariable Long id,
            @Valid @RequestPart("metadata") EduResource resource,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        resource.setId(id);
        resourceService.updateResource(resource, file);
        return Result.success();
    }

    @Operation(summary = "批量删除课件")
    @Log(title = "课件管理", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('course:resource:delete')")
    @DeleteMapping("/{ids}")
    public Result<Void> delete(@PathVariable Long[] ids) {
        resourceService.deleteResourceByIds(ids);
        return Result.success();
    }

    @Operation(summary = "记录下载（原子累加计数）")
    @PostMapping("/{id}/download")
    public Result<Void> download(@PathVariable Long id) {
        resourceService.incrementDownloadCount(id);
        return Result.success();
    }
}
