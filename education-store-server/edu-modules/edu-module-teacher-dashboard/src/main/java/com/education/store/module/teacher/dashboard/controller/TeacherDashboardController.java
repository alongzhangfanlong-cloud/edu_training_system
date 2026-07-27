package com.education.store.module.teacher.dashboard.controller;

import com.education.store.common.model.Result;
import com.education.store.module.teacher.dashboard.model.*;
import com.education.store.module.teacher.dashboard.service.ITeacherDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 教师工作台 Controller
 */
@Tag(name = "教师工作台")
@RestController
@RequestMapping("/api/v1/teacher/dashboard")
@RequiredArgsConstructor
public class TeacherDashboardController {

    private final ITeacherDashboardService teacherDashboardService;

    @Operation(summary = "获取欢迎区信息")
    @GetMapping("/user-info")
    public Result<TeacherUserInfoVO> getUserInfo() {
        return Result.success(teacherDashboardService.getUserInfo());
    }

    @Operation(summary = "获取统计卡片数据")
    @GetMapping("/statistics")
    public Result<TeacherStatisticsVO> getStatistics() {
        return Result.success(teacherDashboardService.getStatistics());
    }

    @Operation(summary = "获取今日课表")
    @GetMapping("/schedule")
    public Result<List<TeacherScheduleVO>> getSchedule() {
        return Result.success(teacherDashboardService.getSchedule());
    }

    @Operation(summary = "获取指定日期范围课表（周/月视图）")
    @GetMapping("/schedule/range")
    public Result<List<TeacherScheduleVO>> getScheduleByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        return Result.success(teacherDashboardService.getScheduleByDateRange(startDate, endDate));
    }

    @Operation(summary = "获取某排课的班级学生名单+签到状态")
    @GetMapping("/schedule/{scheduleId}/students")
    public Result<List<TeacherScheduleStudentVO>> getScheduleStudents(
            @Parameter(description = "排课记录ID") @PathVariable Long scheduleId) {
        return Result.success(teacherDashboardService.getScheduleStudents(scheduleId));
    }

    @Operation(summary = "获取某排课签到统计")
    @GetMapping("/schedule/{scheduleId}/stats")
    public Result<Map<String, Long>> getScheduleStats(
            @Parameter(description = "排课记录ID") @PathVariable Long scheduleId) {
        return Result.success(teacherDashboardService.getScheduleSigninStats(scheduleId));
    }

    @Operation(summary = "批量签到")
    @PostMapping("/signin/batch")
    public Result<Void> batchSignin(
            @RequestParam Long scheduleId,
            @RequestBody List<Map<String, Object>> records) {
        teacherDashboardService.batchSignin(scheduleId, records);
        return Result.success();
    }

    @Operation(summary = "获取待办数据")
    @GetMapping("/todos")
    public Result<TeacherTodosVO> getTodos() {
        return Result.success(teacherDashboardService.getTodos());
    }

    @Operation(summary = "获取我教的学生列表")
    @GetMapping("/my-students")
    public Result<List<TeacherStudentVO>> getMyStudents() {
        return Result.success(teacherDashboardService.getMyStudents());
    }
}
