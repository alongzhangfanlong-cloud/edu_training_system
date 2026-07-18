package com.education.store.module.course.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.store.common.annotation.Log;
import com.education.store.common.enums.BusinessType;
import com.education.store.common.model.PageResult;
import com.education.store.common.model.Result;
import com.education.store.module.course.entity.EduClassroom;
import com.education.store.module.course.service.IEduClassroomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 教室管理控制器
 */
@Tag(name = "教室管理")
@RestController
@RequestMapping("/api/v1/classrooms")
@RequiredArgsConstructor
public class EduClassroomController {

    private final IEduClassroomService classroomService;

    @Operation(summary = "教室列表(分页)")
    @PreAuthorize("@ss.hasPermi('course:classroom:list')")
    @GetMapping("/list")
    public Result<PageResult<EduClassroom>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status) {
        Page<EduClassroom> p = new Page<>(page, pageSize);
        EduClassroom condition = new EduClassroom();
        condition.setName(name);
        condition.setStatus(status);
        IPage<EduClassroom> result = classroomService.selectClassroomList(p, condition);
        return Result.success(PageResult.of(result));
    }

    @Operation(summary = "教室详情")
    @PreAuthorize("@ss.hasPermi('course:classroom:query')")
    @GetMapping("/{id}")
    public Result<EduClassroom> detail(@PathVariable Long id) {
        return Result.success(classroomService.selectClassroomById(id));
    }

    @Operation(summary = "新增教室")
    @Log(title = "教室管理", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('course:classroom:add')")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody EduClassroom classroom) {
        if (!classroomService.checkNameUnique(classroom)) {
            return Result.error("教室名称已存在");
        }
        classroomService.insertClassroom(classroom);
        return Result.success();
    }

    @Operation(summary = "更新教室")
    @Log(title = "教室管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('course:classroom:edit')")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody EduClassroom classroom) {
        if (classroom.getId() == null) {
            return Result.badRequest("教室ID不能为空");
        }
        if (!classroomService.checkNameUnique(classroom)) {
            return Result.error("教室名称已存在");
        }
        classroomService.updateClassroom(classroom);
        return Result.success();
    }

    @Operation(summary = "批量删除教室")
    @Log(title = "教室管理", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('course:classroom:delete')")
    @DeleteMapping("/{ids}")
    public Result<Void> delete(@PathVariable Long[] ids) {
        classroomService.deleteClassroomByIds(ids);
        return Result.success();
    }
}
