package com.education.store.module.tuition.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.store.common.annotation.Log;
import com.education.store.common.enums.BusinessType;
import com.education.store.common.model.PageResult;
import com.education.store.common.model.Result;
import com.education.store.module.tuition.entity.EduTuitionFee;
import com.education.store.module.tuition.service.IEduTuitionFeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "收费管理")
@RestController
@RequestMapping("/api/v1/tuition/fees")
@RequiredArgsConstructor
public class EduTuitionFeeController {

    private final IEduTuitionFeeService feeService;

    @Operation(summary = "收费记录列表")
    @PreAuthorize("@ss.hasPermi('tuition:fee:list')")
    @GetMapping("/list")
    public Result<PageResult<EduTuitionFee>> list(@RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize, @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long enrollmentId, @RequestParam(required = false) String status) {
        Page<EduTuitionFee> p = new Page<>(page, pageSize);
        EduTuitionFee condition = new EduTuitionFee();
        condition.setStudentId(studentId); condition.setEnrollmentId(enrollmentId); condition.setStatus(status);
        IPage<EduTuitionFee> result = feeService.selectFeeList(p, condition);
        return Result.success(PageResult.of(result));
    }

    @GetMapping("/{id}") @PreAuthorize("@ss.hasPermi('tuition:fee:list')")
    public Result<EduTuitionFee> detail(@PathVariable Long id) { return Result.success(feeService.selectFeeById(id)); }

    @PostMapping @Log(title = "收费管理", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('tuition:fee:add')")
    public Result<Void> create(@Valid @RequestBody EduTuitionFee fee) { feeService.insertFee(fee); return Result.success(); }

    @PutMapping @Log(title = "收费管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('tuition:fee:edit')")
    public Result<Void> update(@Valid @RequestBody EduTuitionFee fee) { feeService.updateFee(fee); return Result.success(); }

    @DeleteMapping("/{ids}") @Log(title = "收费管理", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('tuition:fee:delete')")
    public Result<Void> delete(@PathVariable Long[] ids) { feeService.deleteFeeByIds(ids); return Result.success(); }
}
