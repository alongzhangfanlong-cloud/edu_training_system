package com.education.store.module.payroll.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.store.common.annotation.Log;
import com.education.store.common.enums.BusinessType;
import com.education.store.common.model.PageResult;
import com.education.store.common.model.Result;
import com.education.store.module.payroll.entity.EduTeacherPayRate;
import com.education.store.module.payroll.service.IEduTeacherPayRateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "课酬单价")
@RestController
@RequestMapping("/api/v1/pay-rates")
@RequiredArgsConstructor
public class EduTeacherPayRateController {

    private final IEduTeacherPayRateService payRateService;

    @Operation(summary = "课酬单价列表")
    @PreAuthorize("@ss.hasPermi('payroll:pay-rate:list')")
    @GetMapping("/list")
    public Result<PageResult<EduTeacherPayRate>> list(@RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize, @RequestParam(required = false) Long teacherId,
            @RequestParam(required = false) Long courseId) {
        Page<EduTeacherPayRate> p = new Page<>(page, pageSize);
        EduTeacherPayRate condition = new EduTeacherPayRate();
        condition.setTeacherId(teacherId);
        condition.setCourseId(courseId);
        IPage<EduTeacherPayRate> result = payRateService.selectPayRateList(p, condition);
        return Result.success(PageResult.of(result));
    }

    @Operation(summary = "课酬单价详情")
    @PreAuthorize("@ss.hasPermi('payroll:pay-rate:list')")
    @GetMapping("/{id}")
    public Result<EduTeacherPayRate> detail(@PathVariable Long id) {
        return Result.success(payRateService.selectPayRateById(id));
    }

    @Operation(summary = "新增课酬单价")
    @Log(title = "课酬单价", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('payroll:pay-rate:add')")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody EduTeacherPayRate rate) {
        if (!payRateService.checkPeriodOverlap(rate)) return Result.error("该教师在该课程下已有重叠的有效期配置");
        payRateService.insertPayRate(rate);
        return Result.success();
    }

    @Operation(summary = "更新课酬单价")
    @Log(title = "课酬单价", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('payroll:pay-rate:edit')")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody EduTeacherPayRate rate) {
        payRateService.updatePayRate(rate);
        return Result.success();
    }

    @Operation(summary = "删除课酬单价")
    @Log(title = "课酬单价", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('payroll:pay-rate:delete')")
    @DeleteMapping("/{ids}")
    public Result<Void> delete(@PathVariable Long[] ids) {
        payRateService.deletePayRateByIds(ids);
        return Result.success();
    }
}
