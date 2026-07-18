package com.education.store.module.teaching.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.store.common.annotation.Log;
import com.education.store.common.enums.BusinessType;
import com.education.store.common.model.PageResult;
import com.education.store.common.model.Result;
import com.education.store.module.teaching.entity.EduHomework;
import com.education.store.module.teaching.service.IEduHomeworkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "课后作业")
@RestController
@RequestMapping("/api/v1/teaching/homework")
@RequiredArgsConstructor
public class HomeworkController {

    private final IEduHomeworkService homeworkService;

    @Operation(summary = "作业列表")
    @PreAuthorize("@ss.hasPermi('teaching:homework:list')")
    @GetMapping("/list")
    public Result<PageResult<EduHomework>> list(@RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Long teacherId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Page<EduHomework> p = new Page<>(page, pageSize);
        IPage<EduHomework> result = homeworkService.selectHomeworkList(p, keyword, classId, teacherId, startDate, endDate);
        return Result.success(PageResult.of(result));
    }

    @Operation(summary = "作业详情")
    @PreAuthorize("@ss.hasPermi('teaching:homework:list')")
    @GetMapping("/{id}")
    public Result<EduHomework> detail(@PathVariable Long id) {
        return Result.success(homeworkService.getById(id));
    }

    @Operation(summary = "布置作业")
    @Log(title = "课后作业", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('teaching:homework:add')")
    @PostMapping
    public Result<EduHomework> add(@RequestBody EduHomework homework) {
        homeworkService.addHomework(homework, null);
        return Result.success(homework);
    }

    @Operation(summary = "编辑作业")
    @Log(title = "课后作业", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('teaching:homework:edit')")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody EduHomework homework) {
        homework.setId(id);
        homeworkService.updateHomework(homework);
        return Result.success();
    }

    @Operation(summary = "删除作业")
    @Log(title = "课后作业", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('teaching:homework:remove')")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, @RequestParam(required = false) Long currentUserId) {
        homeworkService.deleteHomework(id, currentUserId);
        return Result.success();
    }
}
