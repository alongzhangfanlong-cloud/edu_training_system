package com.education.store.module.dashboard.controller;

import com.education.store.common.model.PageResult;
import com.education.store.common.model.Result;
import com.education.store.module.dashboard.model.*;
import com.education.store.module.dashboard.service.IDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * 工作台 Controller
 */
@Tag(name = "工作台")
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final IDashboardService dashboardService;

    @Operation(summary = "获取欢迎区信息")
    @PreAuthorize("@ss.hasPermi('dashboard:dashboard:view')")
    @GetMapping("/user-info")
    public Result<UserInfoVO> getUserInfo() {
        return Result.success(dashboardService.getUserInfo());
    }

    @Operation(summary = "获取统计卡片数据")
    @PreAuthorize("@ss.hasPermi('dashboard:dashboard:view')")
    @GetMapping("/statistics")
    public Result<StatisticsVO> getStatistics() {
        return Result.success(dashboardService.getStatistics());
    }

    @Operation(summary = "获取排课日历数据")
    @PreAuthorize("@ss.hasPermi('dashboard:dashboard:view')")
    @GetMapping("/calendar")
    public Result<CalendarVO> getCalendar(
            @Parameter(description = "视图类型：MONTH/WEEK/DAY") @RequestParam(defaultValue = "MONTH") String viewType,
            @Parameter(description = "参考日期 yyyy-MM-dd") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return Result.success(dashboardService.getCalendar(viewType, date));
    }

    @Operation(summary = "获取某排课的学生签到详情")
    @PreAuthorize("@ss.hasPermi('dashboard:dashboard:view')")
    @GetMapping("/schedule/{scheduleId}/students")
    public Result<ScheduleStudentVO> getScheduleStudents(
            @Parameter(description = "排课记录ID") @PathVariable Long scheduleId) {
        return Result.success(dashboardService.getScheduleStudents(scheduleId));
    }

    @Operation(summary = "获取待办提醒数据")
    @PreAuthorize("@ss.hasPermi('dashboard:dashboard:view')")
    @GetMapping("/todos")
    public Result<TodosVO> getTodos() {
        return Result.success(dashboardService.getTodos());
    }

    @Operation(summary = "获取本月报名统计")
    @PreAuthorize("@ss.hasPermi('dashboard:dashboard:view')")
    @GetMapping("/enrollment-stats")
    public Result<EnrollmentStatsVO> getEnrollmentStats() {
        return Result.success(dashboardService.getEnrollmentStats());
    }

    @Operation(summary = "获取周课次分布（备用）")
    @PreAuthorize("@ss.hasPermi('dashboard:dashboard:view')")
    @GetMapping("/weekly")
    public Result<String> getWeekly(
            @Parameter(description = "周偏移") @RequestParam(defaultValue = "0") int weekOffset) {
        return Result.success("{}");
    }

    @Operation(summary = "获取月消课课时+趋势（备用）")
    @PreAuthorize("@ss.hasPermi('dashboard:dashboard:view')")
    @GetMapping("/monthly-hours")
    public Result<String> getMonthlyHours() {
        return Result.success("{}");
    }
}
