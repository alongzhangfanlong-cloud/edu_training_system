package com.education.store.module.enrollment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.store.common.annotation.Log;
import com.education.store.common.enums.BusinessType;
import com.education.store.common.model.PageResult;
import com.education.store.common.model.Result;
import com.education.store.module.enrollment.entity.EduEnrollment;
import com.education.store.module.enrollment.service.IEduEnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "报名管理")
@RestController
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
public class EduEnrollmentController {

    private final IEduEnrollmentService enrollmentService;

    @Operation(summary = "报名列表")
    @PreAuthorize("@ss.hasPermi('enrollment:enrollment:list')")
    @GetMapping("/list")
    public Result<PageResult<EduEnrollment>> list(@RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize, @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long courseId, @RequestParam(required = false) String paymentStatus) {
        Page<EduEnrollment> p = new Page<>(page, pageSize);
        EduEnrollment condition = new EduEnrollment();
        condition.setStudentId(studentId);
        condition.setCourseId(courseId);
        condition.setPaymentStatus(paymentStatus);
        IPage<EduEnrollment> result = enrollmentService.selectEnrollmentList(p, condition);
        return Result.success(PageResult.of(result));
    }

    @Operation(summary = "报名详情")
    @PreAuthorize("@ss.hasPermi('enrollment:enrollment:query')")
    @GetMapping("/{id}")
    public Result<EduEnrollment> detail(@PathVariable Long id) {
        return Result.success(enrollmentService.selectEnrollmentById(id));
    }

    @Operation(summary = "新增报名")
    @Log(title = "报名管理", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('enrollment:enrollment:add')")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody EduEnrollment enrollment) {
        enrollmentService.insertEnrollment(enrollment);
        return Result.success();
    }

    @Operation(summary = "更新报名")
    @Log(title = "报名管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('enrollment:enrollment:edit')")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody EduEnrollment enrollment) {
        enrollmentService.updateEnrollment(enrollment);
        return Result.success();
    }

    @Operation(summary = "取消报名")
    @Log(title = "报名管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('enrollment:enrollment:cancel')")
    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        enrollmentService.updatePaymentStatus(id, "CANCELLED");
        return Result.success();
    }

    @Operation(summary = "删除报名")
    @Log(title = "报名管理", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('enrollment:enrollment:cancel')")
    @DeleteMapping("/{ids}")
    public Result<Void> delete(@PathVariable Long[] ids) {
        enrollmentService.deleteEnrollmentByIds(ids);
        return Result.success();
    }
}
