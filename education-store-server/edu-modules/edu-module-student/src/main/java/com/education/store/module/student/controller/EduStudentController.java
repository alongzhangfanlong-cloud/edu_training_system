package com.education.store.module.student.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.store.common.annotation.Log;
import com.education.store.common.enums.BusinessType;
import com.education.store.common.model.PageResult;
import com.education.store.common.model.Result;
import com.education.store.module.student.entity.EduStudent;
import com.education.store.module.student.service.IEduStudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 在学学员管理控制器
 */
@Tag(name = "学员管理")
@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class EduStudentController {

    private final IEduStudentService studentService;

    @Operation(summary = "学员列表(分页)")
    @PreAuthorize("@ss.hasPermi('student:student:list')")
    @GetMapping("/list")
    public Result<PageResult<EduStudent>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String mobile,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long counselorId,
            @RequestParam(required = false) String source) {
        Page<EduStudent> p = new Page<>(page, pageSize);
        EduStudent condition = new EduStudent();
        condition.setName(name);
        condition.setMobile(mobile);
        condition.setGrade(grade);
        condition.setStatus(status);
        condition.setCounselorId(counselorId);
        condition.setSource(source);
        IPage<EduStudent> result = studentService.selectStudentList(p, condition);
        return Result.success(PageResult.of(result));
    }

    @Operation(summary = "学员详情")
    @PreAuthorize("@ss.hasPermi('student:student:query')")
    @GetMapping("/{id}")
    public Result<EduStudent> detail(@PathVariable Long id) {
        return Result.success(studentService.selectStudentById(id));
    }

    @Operation(summary = "新增学员")
    @Log(title = "学员管理", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('student:student:add')")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody EduStudent student) {
        if (!studentService.checkMobileUnique(student)) {
            return Result.error("手机号已被其他学员使用");
        }
        studentService.insertStudent(student);
        return Result.success();
    }

    @Operation(summary = "更新学员信息")
    @Log(title = "学员管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('student:student:edit')")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody EduStudent student) {
        if (student.getId() == null) {
            return Result.badRequest("学员ID不能为空");
        }
        if (!studentService.checkMobileUnique(student)) {
            return Result.error("手机号已被其他学员使用");
        }
        studentService.updateStudent(student);
        return Result.success();
    }

    @Operation(summary = "批量删除学员")
    @Log(title = "学员管理", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('student:student:delete')")
    @DeleteMapping("/{ids}")
    public Result<Void> delete(@PathVariable Long[] ids) {
        studentService.deleteStudentByIds(ids);
        return Result.success();
    }

    @Operation(summary = "更新学员状态")
    @Log(title = "学员管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('student:student:edit')")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        studentService.updateStudentStatus(id, status);
        return Result.success();
    }
}
