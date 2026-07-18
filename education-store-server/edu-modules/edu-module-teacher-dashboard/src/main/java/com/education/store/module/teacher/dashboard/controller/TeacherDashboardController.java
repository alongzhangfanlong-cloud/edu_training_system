package com.education.store.module.teacher.dashboard.controller;

import com.education.store.common.model.Result;
import com.education.store.module.teacher.dashboard.model.*;
import com.education.store.module.teacher.dashboard.service.ITeacherDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @PreAuthorize("@ss.hasPermi('teacher:dashboard:view')")
    @GetMapping("/user-info")
    public Result<TeacherUserInfoVO> getUserInfo() {
        return Result.success(teacherDashboardService.getUserInfo());
    }

    @Operation(summary = "获取统计卡片数据")
    @PreAuthorize("@ss.hasPermi('teacher:dashboard:view')")
    @GetMapping("/statistics")
    public Result<TeacherStatisticsVO> getStatistics() {
        return Result.success(teacherDashboardService.getStatistics());
    }

    @Operation(summary = "获取今日课表")
    @PreAuthorize("@ss.hasPermi('teacher:dashboard:view')")
    @GetMapping("/schedule")
    public Result<List<TeacherScheduleVO>> getSchedule() {
        return Result.success(teacherDashboardService.getSchedule());
    }

    @Operation(summary = "获取某排课的班级学生名单+签到状态")
    @PreAuthorize("@ss.hasPermi('teacher:dashboard:view')")
    @GetMapping("/schedule/{scheduleId}/students")
    public Result<List<TeacherScheduleStudentVO>> getScheduleStudents(
            @Parameter(description = "排课记录ID") @PathVariable Long scheduleId) {
        return Result.success(teacherDashboardService.getScheduleStudents(scheduleId));
    }

    @Operation(summary = "获取待办数据")
    @PreAuthorize("@ss.hasPermi('teacher:dashboard:view')")
    @GetMapping("/todos")
    public Result<TeacherTodosVO> getTodos() {
        return Result.success(teacherDashboardService.getTodos());
    }
}
