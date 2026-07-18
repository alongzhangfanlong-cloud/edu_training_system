package com.education.store.module.student.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.store.common.annotation.Log;
import com.education.store.common.enums.BusinessType;
import com.education.store.common.model.PageResult;
import com.education.store.common.model.Result;
import com.education.store.module.student.entity.EduStudentHoursLog;
import com.education.store.module.student.service.IEduStudentHoursLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 课时变动日志控制器（只追加，不可修改/删除）
 */
@Tag(name = "课时日志")
@RestController
@RequestMapping("/api/v1/students/{studentId}/hours-logs")
@RequiredArgsConstructor
public class EduStudentHoursLogController {

    private final IEduStudentHoursLogService hoursLogService;

    @Operation(summary = "课时变动记录(分页)")
    @PreAuthorize("@ss.hasPermi('student:hours:list')")
    @GetMapping("/list")
    public Result<PageResult<EduStudentHoursLog>> list(
            @PathVariable Long studentId,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long pageSize,
            @RequestParam(required = false) Long courseId) {
        Page<EduStudentHoursLog> p = new Page<>(page, pageSize);
        IPage<EduStudentHoursLog> result = hoursLogService.selectHoursLogList(p, studentId, courseId);
        return Result.success(PageResult.of(result));
    }

    @Operation(summary = "新增课时变动（手动调整）")
    @Log(title = "课时管理", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('student:hours:add')")
    @PostMapping
    public Result<Void> create(@PathVariable Long studentId,
                               @Valid @RequestBody EduStudentHoursLog log) {
        log.setStudentId(studentId);
        // 仅允许手动调整类型
        if (log.getChangeType() == null || !"MANUAL_ADJUST".equals(log.getChangeType())) {
            return Result.badRequest("仅支持手动调整类型(MANUAL_ADJUST)");
        }
        hoursLogService.insertHoursLog(log);
        return Result.success();
    }

    @Operation(summary = "查询课时余额")
    @PreAuthorize("@ss.hasPermi('student:hours:query')")
    @GetMapping("/balance")
    public Result<Map<String, BigDecimal>> balance(@PathVariable Long studentId) {
        BigDecimal remaining = hoursLogService.getRemainingHours(studentId);
        BigDecimal total = hoursLogService.getTotalGainedHours(studentId);
        return Result.success(Map.of(
                "remainingHours", remaining,
                "totalHours", total
        ));
    }
}
