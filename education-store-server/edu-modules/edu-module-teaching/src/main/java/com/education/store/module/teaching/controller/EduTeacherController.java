package com.education.store.module.teaching.controller;

import com.education.store.common.annotation.Log;
import com.education.store.common.model.Result;
import com.education.store.common.enums.BusinessType;
import com.education.store.module.teaching.entity.EduTeacher;
import com.education.store.module.teaching.service.IEduTeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教师档案 Controller
 */
@Tag(name = "教师档案管理")
@RestController
@RequestMapping("/api/v1/teaching/teacher")
@RequiredArgsConstructor
public class EduTeacherController {

    private final IEduTeacherService teacherService;

    @GetMapping("/list")
    @Operation(summary = "查询教师列表")
    @PreAuthorize("@ss.hasPermi('teaching:teacher:list')")
    public Result<List<EduTeacher>> list() {
        return Result.success(teacherService.list());
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询教师详情")
    @PreAuthorize("@ss.hasPermi('teaching:teacher:query')")
    public Result<EduTeacher> getById(@PathVariable Long id) {
        return Result.success(teacherService.getById(id));
    }

    @PostMapping
    @Operation(summary = "新增教师")
    @PreAuthorize("@ss.hasPermi('teaching:teacher:add')")
    @Log(title = "教师管理", businessType = BusinessType.INSERT)
    public Result<?> add(@RequestBody EduTeacher teacher) {
        return Result.success(teacherService.save(teacher));
    }

    @PutMapping
    @Operation(summary = "编辑教师")
    @PreAuthorize("@ss.hasPermi('teaching:teacher:edit')")
    @Log(title = "教师管理", businessType = BusinessType.UPDATE)
    public Result<?> update(@RequestBody EduTeacher teacher) {
        return Result.success(teacherService.updateById(teacher));
    }

    @DeleteMapping("/{ids}")
    @Operation(summary = "删除教师")
    @PreAuthorize("@ss.hasPermi('teaching:teacher:delete')")
    @Log(title = "教师管理", businessType = BusinessType.DELETE)
    public Result<?> delete(@PathVariable Long[] ids) {
        return Result.success(teacherService.removeByIds(java.util.Arrays.asList(ids)));
    }
}
