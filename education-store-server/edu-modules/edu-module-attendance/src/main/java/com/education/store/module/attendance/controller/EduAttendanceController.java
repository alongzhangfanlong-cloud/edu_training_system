package com.education.store.module.attendance.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.store.common.annotation.Log;
import com.education.store.common.enums.BusinessType;
import com.education.store.common.model.PageResult;
import com.education.store.common.model.Result;
import com.education.store.module.attendance.entity.EduAttendance;
import com.education.store.module.attendance.service.IEduAttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "签到管理")
@RestController
@RequestMapping("/api/v1/attendance/signin")
@RequiredArgsConstructor
public class EduAttendanceController {

    private final IEduAttendanceService attendanceService;

    @Operation(summary = "签到记录列表")
    @GetMapping("/list")
    public Result<PageResult<EduAttendance>> list(@RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long pageSize, @RequestParam(required = false) Long scheduleId,
            @RequestParam(required = false) Long classId, @RequestParam(required = false) Long studentId) {
        Page<EduAttendance> p = new Page<>(page, pageSize);
        IPage<EduAttendance> result = attendanceService.selectAttendanceList(p, scheduleId, classId, studentId);
        return Result.success(PageResult.of(result));
    }

    @Operation(summary = "批量签到")
    @Log(title = "签到管理", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('attendance:signin:add')")
    @PostMapping("/batch")
    public Result<Void> batchSignin(@RequestParam Long scheduleId, @RequestParam Long classId,
            @RequestBody List<EduAttendance> records, @RequestParam(defaultValue = "0") Long operatorId) {
        attendanceService.batchSignin(scheduleId, classId, records, operatorId, "PC");
        return Result.success();
    }

    @Operation(summary = "签到统计")
    @PreAuthorize("@ss.hasPermi('attendance:signin:list')")
    @GetMapping("/stats/{scheduleId}")
    public Result<Map<String, Long>> stats(@PathVariable Long scheduleId) {
        return Result.success(attendanceService.getStatsBySchedule(scheduleId));
    }
}
