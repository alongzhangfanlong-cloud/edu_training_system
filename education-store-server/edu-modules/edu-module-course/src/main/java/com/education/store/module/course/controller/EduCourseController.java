package com.education.store.module.course.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.store.common.annotation.Log;
import com.education.store.common.enums.BusinessType;
import com.education.store.common.model.PageResult;
import com.education.store.common.model.Result;
import com.education.store.module.course.entity.EduCourse;
import com.education.store.module.course.service.IEduCourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 课程管理控制器
 */
@Tag(name = "课程管理")
@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class EduCourseController {

    private final IEduCourseService courseService;

    @Operation(summary = "课程列表(分页)")
    @PreAuthorize("@ss.hasPermi('course:course:list')")
    @GetMapping("/list")
    public Result<PageResult<EduCourse>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) String courseType,
            @RequestParam(required = false) Integer status) {
        Page<EduCourse> p = new Page<>(page, pageSize);
        EduCourse condition = new EduCourse();
        condition.setName(name);
        condition.setCode(code);
        condition.setSubjectId(subjectId);
        condition.setCourseType(courseType);
        condition.setStatus(status);
        IPage<EduCourse> result = courseService.selectCourseList(p, condition);
        return Result.success(PageResult.of(result));
    }

    @Operation(summary = "课程详情")
    @PreAuthorize("@ss.hasPermi('course:course:query')")
    @GetMapping("/{id}")
    public Result<EduCourse> detail(@PathVariable Long id) {
        return Result.success(courseService.selectCourseById(id));
    }

    @Operation(summary = "新增课程")
    @Log(title = "课程管理", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('course:course:add')")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody EduCourse course) {
        if (!courseService.checkCodeUnique(course)) {
            return Result.error("课程编号已存在");
        }
        if (!courseService.checkNameUniqueWithinSubject(course)) {
            return Result.error("同一科目下课程名称已存在");
        }
        courseService.insertCourse(course);
        return Result.success();
    }

    @Operation(summary = "更新课程")
    @Log(title = "课程管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('course:course:edit')")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody EduCourse course) {
        if (course.getId() == null) {
            return Result.badRequest("课程ID不能为空");
        }
        if (!courseService.checkCodeUnique(course)) {
            return Result.error("课程编号已存在");
        }
        if (!courseService.checkNameUniqueWithinSubject(course)) {
            return Result.error("同一科目下课程名称已存在");
        }
        courseService.updateCourse(course);
        return Result.success();
    }

    @Operation(summary = "批量删除课程")
    @Log(title = "课程管理", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('course:course:delete')")
    @DeleteMapping("/{ids}")
    public Result<Void> delete(@PathVariable Long[] ids) {
        courseService.deleteCourseByIds(ids);
        return Result.success();
    }
}
