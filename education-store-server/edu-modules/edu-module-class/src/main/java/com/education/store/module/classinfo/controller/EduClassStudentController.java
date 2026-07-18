package com.education.store.module.classinfo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.store.common.annotation.Log;
import com.education.store.common.enums.BusinessType;
import com.education.store.common.model.PageResult;
import com.education.store.common.model.Result;
import com.education.store.module.classinfo.entity.EduClassStudent;
import com.education.store.module.classinfo.service.IEduClassStudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 班级学员管理控制器（N02↔N03 跨模块桥接）
 */
@Tag(name = "班级学员管理")
@RestController
@RequestMapping("/api/v1/classes/{classId}/students")
@RequiredArgsConstructor
public class EduClassStudentController {

    private final IEduClassStudentService classStudentService;

    @Operation(summary = "班级学员列表")
    @PreAuthorize("@ss.hasPermi('class:student:list')")
    @GetMapping("/list")
    public Result<PageResult<EduClassStudent>> list(
            @PathVariable Long classId,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize) {
        Page<EduClassStudent> p = new Page<>(page, pageSize);
        IPage<EduClassStudent> result = classStudentService.selectClassStudentList(p, classId);
        return Result.success(PageResult.of(result));
    }

    @Operation(summary = "学员加入班级")
    @Log(title = "班级学员管理", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('class:student:add')")
    @PostMapping
    public Result<Void> enroll(@PathVariable Long classId,
                               @Valid @RequestBody EduClassStudent classStudent) {
        classStudent.setClassId(classId);
        classStudentService.enrollStudent(classStudent);
        return Result.success();
    }

    @Operation(summary = "更新在班状态")
    @Log(title = "班级学员管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('class:student:edit')")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long classId,
                                     @PathVariable Long id,
                                     @RequestParam String status) {
        classStudentService.updateStatus(id, status);
        return Result.success();
    }

    @Operation(summary = "退班（移除学员）")
    @Log(title = "班级学员管理", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('class:student:delete')")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long classId, @PathVariable Long id) {
        classStudentService.removeStudent(id);
        return Result.success();
    }
}
