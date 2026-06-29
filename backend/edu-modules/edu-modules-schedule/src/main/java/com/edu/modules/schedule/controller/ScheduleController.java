package com.edu.modules.schedule.controller;

import com.edu.common.core.response.ApiResult;
import com.edu.modules.schedule.dto.*;
import com.edu.modules.schedule.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
@Tag(name = "排课管理", description = "排课CRUD、批量排课、冲突检测")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping
    @Operation(summary = "查询课表")
    public ApiResult<Map<String, List<ScheduleDTO>>> list(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) Long teacherId,
            @RequestParam(required = false) Long classroomId,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Long studentId) {
        List<ScheduleDTO> schedules = scheduleService.query(
                startDate, endDate, teacherId, classroomId, classId, studentId);
        return ApiResult.success(Map.of("schedules", schedules));
    }

    @PostMapping
    @Operation(summary = "新增排课")
    public ApiResult<ScheduleDTO> create(@Valid @RequestBody CreateScheduleRequest request) {
        return ApiResult.success(scheduleService.create(request));
    }

    @PostMapping("/batch")
    @Operation(summary = "批量排课")
    public ApiResult<Map<String, Object>> batchCreate(@Valid @RequestBody BatchScheduleRequest request) {
        return ApiResult.success(scheduleService.batchCreate(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改排课")
    public ApiResult<ScheduleDTO> update(@PathVariable Long id,
                                          @Valid @RequestBody CreateScheduleRequest request) {
        return ApiResult.success(scheduleService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除排课")
    public ApiResult<Void> delete(@PathVariable Long id) {
        scheduleService.delete(id);
        return ApiResult.success();
    }

    @PostMapping("/conflict-check")
    @Operation(summary = "排课冲突检测")
    public ApiResult<Map<String, Object>> conflictCheck(@RequestBody ConflictCheckRequest request) {
        return ApiResult.success(scheduleService.checkConflicts(request));
    }
}
