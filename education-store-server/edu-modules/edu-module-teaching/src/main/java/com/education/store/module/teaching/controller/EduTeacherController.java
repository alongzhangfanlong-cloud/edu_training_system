package com.education.store.module.teaching.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.store.common.annotation.Log;
import com.education.store.common.enums.BusinessType;
import com.education.store.common.model.PageResult;
import com.education.store.common.model.Result;
import com.education.store.module.teaching.entity.EduTeacher;
import com.education.store.module.teaching.service.IEduTeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    @Operation(summary = "查询教师列表(分页)")
    @PreAuthorize("@ss.hasPermi('teaching:teacher:list')")
    public Result<PageResult<EduTeacher>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10", name = "size") long pageSize,
            @RequestParam(required = false) String keyword) {
        Page<EduTeacher> p = new Page<>(page, pageSize);
        LambdaQueryWrapper<EduTeacher> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(EduTeacher::getRealName, keyword)
                    .or().like(EduTeacher::getPhone, keyword));
        }
        wrapper.orderByDesc(EduTeacher::getCreateTime);
        IPage<EduTeacher> result = teacherService.page(p, wrapper);
        return Result.success(PageResult.of(result));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询教师详情")
    @PreAuthorize("@ss.hasPermi('teaching:teacher:query')")
    public Result<EduTeacher> getById(@PathVariable Long id) {
        return Result.success(teacherService.getById(id));
    }

    @PostMapping
    @Operation(summary = "新增教师档案")
    @PreAuthorize("@ss.hasPermi('teaching:teacher:add')")
    @Log(title = "教师管理", businessType = BusinessType.INSERT)
    public Result<?> add(@Valid @RequestBody EduTeacher teacher) {
        return Result.success(teacherService.save(teacher));
    }

    @PutMapping
    @Operation(summary = "编辑教师档案")
    @PreAuthorize("@ss.hasPermi('teaching:teacher:edit')")
    @Log(title = "教师管理", businessType = BusinessType.UPDATE)
    public Result<?> update(@Valid @RequestBody EduTeacher teacher) {
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
