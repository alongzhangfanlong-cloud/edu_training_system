package com.education.store.module.schedule.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.store.common.annotation.Log;
import com.education.store.common.enums.BusinessType;
import com.education.store.common.model.PageResult;
import com.education.store.common.model.Result;
import com.education.store.module.schedule.dto.SchedulePlanCreateDTO;
import com.education.store.module.schedule.dto.SchedulePlanSaveDTO;
import com.education.store.module.schedule.dto.SchedulePreviewVO;
import com.education.store.module.schedule.entity.EduSchedulePlan;
import com.education.store.module.schedule.service.IEduSchedulePlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "排课计划")
@RestController
@RequestMapping("/api/v1/schedule/plans")
@RequiredArgsConstructor
public class EduSchedulePlanController {

    private final IEduSchedulePlanService planService;

    @Operation(summary = "排课计划列表")
    @PreAuthorize("@ss.hasPermi('schedule:plan:list')")
    @GetMapping("/list")
    public Result<PageResult<EduSchedulePlan>> list(@RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long classId) {
        Page<EduSchedulePlan> p = new Page<>(page, pageSize);
        EduSchedulePlan condition = new EduSchedulePlan();
        condition.setStatus(status);
        IPage<EduSchedulePlan> result = planService.selectPlanList(p, condition, classId);
        return Result.success(PageResult.of(result));
    }

    @Operation(summary = "排课计划详情")
    @PreAuthorize("@ss.hasPermi('schedule:plan:query')")
    @GetMapping("/{id}")
    public Result<EduSchedulePlan> detail(@PathVariable Long id) {
        return Result.success(planService.selectPlanById(id));
    }

    @Operation(summary = "排课计划预览 — 三维冲突检测，返回每个槽位的冲突信息")
    @PreAuthorize("@ss.hasPermi('schedule:plan:add')")
    @PostMapping("/preview")
    public Result<SchedulePreviewVO> preview(@Valid @RequestBody SchedulePlanCreateDTO dto) {
        return Result.success(planService.previewPlan(dto));
    }

    @Operation(summary = "新增排课方案")
    @Log(title = "排课方案", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('schedule:plan:add')")
    @PostMapping
    public Result<EduSchedulePlan> create(@Valid @RequestBody SchedulePlanSaveDTO dto) {
        EduSchedulePlan plan = new EduSchedulePlan();
        plan.setClassId(dto.getClassId());
        plan.setRoomId(dto.getRoomId());
        plan.setStartDate(dto.getStartDate());
        plan.setEndDate(dto.getEndDate());
        plan.setRemark(dto.getRemark());
        planService.insertPlan(plan);
        return Result.success(plan);
    }

    @Operation(summary = "更新排课计划")
    @Log(title = "排课计划", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('schedule:plan:edit')")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody EduSchedulePlan plan) {
        planService.updatePlan(plan);
        return Result.success();
    }

    @Operation(summary = "重新生成排课 — 删除旧排课记录后重新生成")
    @Log(title = "排课计划", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('schedule:plan:edit')")
    @PutMapping("/{id}/regenerate")
    public Result<Void> regenerate(@PathVariable Long id) {
        planService.regeneratePlan(id);
        return Result.success();
    }

    @Operation(summary = "删除排课计划")
    @Log(title = "排课计划", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('schedule:plan:delete')")
    @DeleteMapping("/{ids}")
    public Result<Void> delete(@PathVariable Long[] ids) {
        planService.deletePlanByIds(ids);
        return Result.success();
    }
}
