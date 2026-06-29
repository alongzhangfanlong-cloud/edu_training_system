package com.edu.modules.statistics.controller;

import com.edu.common.core.response.ApiResult;
import com.edu.modules.statistics.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
@Tag(name = "数据统计", description = "管理端数据看板、教师统计")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/dashboard")
    @Operation(summary = "管理端数据看板")
    public ApiResult<?> dashboard(@RequestParam(defaultValue = "today") String dateRange) {
        return ApiResult.success(statisticsService.dashboard(dateRange));
    }

    @GetMapping("/teacher/{id}")
    @Operation(summary = "教师课时统计")
    public ApiResult<?> teacherStats(@PathVariable Long id,
                                      @RequestParam(required = false) LocalDate startDate,
                                      @RequestParam(required = false) LocalDate endDate) {
        return ApiResult.success(statisticsService.teacherStats(id, startDate, endDate));
    }
}
