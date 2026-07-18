package com.education.store.module.payroll.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.store.common.annotation.Log;
import com.education.store.common.enums.BusinessType;
import com.education.store.common.model.PageResult;
import com.education.store.common.model.Result;
import com.education.store.module.payroll.entity.EduTeacherPayroll;
import com.education.store.module.payroll.service.IEduTeacherPayrollService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "教师课酬")
@RestController
@RequestMapping("/api/v1/payrolls")
@RequiredArgsConstructor
public class EduTeacherPayrollController {

    private final IEduTeacherPayrollService payrollService;

    @Operation(summary = "课酬列表")
    @PreAuthorize("@ss.hasPermi('payroll:payroll:list')")
    @GetMapping("/list")
    public Result<PageResult<EduTeacherPayroll>> list(@RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize, @RequestParam(required = false) Long teacherId,
            @RequestParam(required = false) String month, @RequestParam(required = false) String status) {
        Page<EduTeacherPayroll> p = new Page<>(page, pageSize);
        EduTeacherPayroll condition = new EduTeacherPayroll();
        condition.setTeacherId(teacherId);
        condition.setMonth(month);
        condition.setStatus(status);
        IPage<EduTeacherPayroll> result = payrollService.selectPayrollList(p, condition);
        return Result.success(PageResult.of(result));
    }

    @Operation(summary = "课酬详情")
    @PreAuthorize("@ss.hasPermi('payroll:payroll:query')")
    @GetMapping("/{id}")
    public Result<EduTeacherPayroll> detail(@PathVariable Long id) {
        return Result.success(payrollService.selectPayrollById(id));
    }

    @Operation(summary = "新增课酬记录")
    @Log(title = "教师课酬", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('payroll:payroll:add')")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody EduTeacherPayroll payroll) {
        payrollService.insertPayroll(payroll);
        return Result.success();
    }

    @Operation(summary = "确认课酬")
    @Log(title = "教师课酬", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('payroll:payroll:confirm')")
    @PutMapping("/{id}/confirm")
    public Result<Void> confirm(@PathVariable Long id) {
        payrollService.confirmPayroll(id);
        return Result.success();
    }

    @Operation(summary = "取消确认")
    @Log(title = "教师课酬", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('payroll:payroll:confirm')")
    @PutMapping("/{id}/cancel-confirm")
    public Result<Void> cancelConfirm(@PathVariable Long id) {
        payrollService.cancelConfirmPayroll(id);
        return Result.success();
    }

    @Operation(summary = "发放课酬")
    @Log(title = "教师课酬", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('payroll:payroll:pay')")
    @PutMapping("/{id}/pay")
    public Result<Void> pay(@PathVariable Long id) {
        payrollService.markPaid(id);
        return Result.success();
    }

    @Operation(summary = "删除课酬记录")
    @Log(title = "教师课酬", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('payroll:payroll:delete')")
    @DeleteMapping("/{ids}")
    public Result<Void> delete(@PathVariable Long[] ids) {
        payrollService.deletePayrollByIds(ids);
        return Result.success();
    }
}
