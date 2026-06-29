package com.edu.modules.attendance.controller;

import com.edu.common.core.response.ApiResult;
import com.edu.modules.attendance.dto.BatchCheckInRequest;
import com.edu.modules.attendance.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/attendances")
@RequiredArgsConstructor
@Tag(name = "考勤签到", description = "教师点名、考勤查询")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/batch-check")
    @Operation(summary = "教师批量点名")
    public ApiResult<Map<String, Object>> batchCheckIn(@Valid @RequestBody BatchCheckInRequest request) {
        return ApiResult.success(attendanceService.batchCheckIn(request));
    }

    @GetMapping("/schedule/{scheduleId}")
    @Operation(summary = "查询某次排课的考勤")
    public ApiResult<?> getBySchedule(@PathVariable Long scheduleId) {
        return ApiResult.success(attendanceService.getByScheduleId(scheduleId));
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "学员考勤记录")
    public ApiResult<?> getByStudent(@PathVariable Long studentId) {
        return ApiResult.success(attendanceService.getByStudentId(studentId));
    }

    @GetMapping("/student/{studentId}/stats")
    @Operation(summary = "学员考勤统计")
    public ApiResult<Map<String, Object>> getStudentStats(@PathVariable Long studentId) {
        return ApiResult.success(attendanceService.getStudentStats(studentId));
    }
}
