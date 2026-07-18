package com.education.store.module.schedule.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.store.common.annotation.Log;
import com.education.store.common.enums.BusinessType;
import com.education.store.common.model.PageResult;
import com.education.store.common.model.Result;
import com.education.store.module.schedule.entity.EduSchedule;
import com.education.store.module.schedule.service.IEduScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "课程表/排课记录")
@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
public class EduScheduleController {

    private final IEduScheduleService scheduleService;

    @Operation(summary = "排课记录列表")
    @PreAuthorize("@ss.hasPermi('schedule:calendar:list')")
    @GetMapping("/list")
    public Result<PageResult<EduSchedule>> list(@RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long pageSize, @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Long teacherId, @RequestParam(required = false) String status) {
        Page<EduSchedule> p = new Page<>(page, pageSize);
        EduSchedule condition = new EduSchedule();
        condition.setClassId(classId);
        condition.setTeacherId(teacherId);
        condition.setStatus(status);
        IPage<EduSchedule> result = scheduleService.selectScheduleList(p, condition);
        return Result.success(PageResult.of(result));
    }

    @Operation(summary = "排课详情")
    @PreAuthorize("@ss.hasPermi('schedule:calendar:list')")
    @GetMapping("/{id}")
    public Result<EduSchedule> detail(@PathVariable Long id) {
        return Result.success(scheduleService.selectScheduleById(id));
    }

    @Operation(summary = "新增排课")
    @Log(title = "排课管理", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('schedule:plan:add')")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody EduSchedule schedule) {
        scheduleService.insertSchedule(schedule);
        return Result.success();
    }

    @Operation(summary = "停课")
    @Log(title = "排课管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('schedule:cancel')")
    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id, @RequestBody Map<String, String> body) {
        scheduleService.cancelSchedule(id, body.getOrDefault("reason", ""));
        return Result.success();
    }

    @Operation(summary = "调课")
    @Log(title = "排课管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('schedule:reschedule')")
    @PutMapping("/{id}/reschedule")
    public Result<Void> reschedule(@PathVariable Long id, @Valid @RequestBody EduSchedule schedule) {
        schedule.setId(id);
        scheduleService.reschedule(schedule);
        return Result.success();
    }

    @Operation(summary = "手动消课")
    @Log(title = "排课管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('schedule:consume')")
    @PutMapping("/{id}/consume")
    public Result<Void> consume(@PathVariable Long id) {
        scheduleService.consumeSchedule(id);
        return Result.success();
    }

    @Operation(summary = "删除排课记录")
    @Log(title = "排课管理", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('schedule:plan:delete')")
    @DeleteMapping("/{ids}")
    public Result<Void> delete(@PathVariable Long[] ids) {
        scheduleService.deleteScheduleByIds(ids);
        return Result.success();
    }
}
