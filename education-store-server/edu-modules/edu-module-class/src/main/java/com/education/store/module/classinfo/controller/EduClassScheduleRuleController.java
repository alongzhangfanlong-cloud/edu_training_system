package com.education.store.module.classinfo.controller;

import com.education.store.common.annotation.Log;
import com.education.store.common.enums.BusinessType;
import com.education.store.common.model.Result;
import com.education.store.module.classinfo.entity.EduClassScheduleRule;
import com.education.store.module.classinfo.service.IEduClassScheduleRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 班级排课规则控制器
 */
@Tag(name = "排课规则")
@RestController
@RequestMapping("/api/v1/classes/{classId}/schedule-rule")
@RequiredArgsConstructor
public class EduClassScheduleRuleController {

    private final IEduClassScheduleRuleService scheduleRuleService;

    @Operation(summary = "查询排课规则")
    @PreAuthorize("@ss.hasPermi('class:class:query')")
    @GetMapping
    public Result<EduClassScheduleRule> get(@PathVariable Long classId) {
        return Result.success(scheduleRuleService.selectByClassId(classId));
    }

    @Operation(summary = "设置排课规则")
    @Log(title = "排课规则", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('class:class:edit')")
    @PostMapping
    public Result<Void> create(@PathVariable Long classId,
                               @Valid @RequestBody EduClassScheduleRule rule) {
        rule.setClassId(classId);
        scheduleRuleService.insertRule(rule);
        return Result.success();
    }

    @Operation(summary = "更新排课规则")
    @Log(title = "排课规则", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('class:class:edit')")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long classId, @PathVariable Long id,
                               @Valid @RequestBody EduClassScheduleRule rule) {
        rule.setId(id);
        rule.setClassId(classId);
        scheduleRuleService.updateRule(rule);
        return Result.success();
    }

    @Operation(summary = "删除排课规则")
    @Log(title = "排课规则", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('class:class:edit')")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long classId, @PathVariable Long id) {
        scheduleRuleService.deleteRuleById(id);
        return Result.success();
    }
}
