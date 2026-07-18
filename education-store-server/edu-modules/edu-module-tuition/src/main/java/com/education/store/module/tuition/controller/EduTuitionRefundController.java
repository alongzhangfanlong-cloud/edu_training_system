package com.education.store.module.tuition.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.store.common.annotation.Log;
import com.education.store.common.enums.BusinessType;
import com.education.store.common.model.PageResult;
import com.education.store.common.model.Result;
import com.education.store.module.tuition.entity.EduTuitionRefund;
import com.education.store.module.tuition.service.IEduTuitionRefundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "退费管理")
@RestController
@RequestMapping("/api/v1/tuition/refunds")
@RequiredArgsConstructor
public class EduTuitionRefundController {

    private final IEduTuitionRefundService refundService;

    @GetMapping("/list") @PreAuthorize("@ss.hasPermi('tuition:refund:list')")
    public Result<PageResult<EduTuitionRefund>> list(@RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize, @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) String status) {
        Page<EduTuitionRefund> p = new Page<>(page, pageSize);
        EduTuitionRefund c = new EduTuitionRefund(); c.setStudentId(studentId); c.setStatus(status);
        return Result.success(PageResult.of(refundService.selectRefundList(p, c)));
    }

    @GetMapping("/{id}") @PreAuthorize("@ss.hasPermi('tuition:refund:list')")
    public Result<EduTuitionRefund> detail(@PathVariable Long id) { return Result.success(refundService.selectRefundById(id)); }

    @PostMapping @Log(title = "退费管理", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('tuition:refund:apply')")
    public Result<Void> create(@Valid @RequestBody EduTuitionRefund refund) { refundService.insertRefund(refund); return Result.success(); }

    @PutMapping("/{id}/approve") @Log(title = "退费管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('tuition:refund:approve')")
    public Result<Void> approve(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        refundService.approveRefund(id, body.get("approverId")); return Result.success(); }

    @PutMapping("/{id}/reject") @Log(title = "退费管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('tuition:refund:approve')")
    public Result<Void> reject(@PathVariable Long id, @RequestBody Map<String, String> body) {
        refundService.rejectRefund(id, Long.valueOf(body.get("approverId")), body.getOrDefault("reason", "")); return Result.success(); }

    @DeleteMapping("/{ids}") @Log(title = "退费管理", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('tuition:refund:delete')")
    public Result<Void> delete(@PathVariable Long[] ids) { refundService.deleteRefundByIds(ids); return Result.success(); }
}
