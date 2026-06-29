package com.edu.modules.interaction.controller;

import com.edu.common.core.response.ApiResult;
import com.edu.modules.interaction.dto.GrowthRecordDTO;
import com.edu.modules.interaction.dto.LeaveDTO;
import com.edu.modules.interaction.dto.PointsDTO;
import com.edu.modules.interaction.service.GrowthRecordService;
import com.edu.modules.interaction.service.LeaveService;
import com.edu.modules.interaction.service.PointsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 学员/家长端聚合接口 - 减少前端多次请求
 */
@RestController
@RequestMapping("/api/v1/student-portal")
@RequiredArgsConstructor
@Tag(name = "学员/家长端", description = "学员/家长端聚合查询接口")
public class StudentPortalController {

    private final PointsService pointsService;
    private final GrowthRecordService growthRecordService;
    private final LeaveService leaveService;

    /**
     * 学员首页数据聚合
     */
    @GetMapping("/{studentId}/dashboard")
    @Operation(summary = "学员首页聚合数据")
    public ApiResult<Map<String, Object>> dashboard(@PathVariable Long studentId) {
        Map<String, Object> result = new HashMap<>();

        // 积分
        PointsDTO points = pointsService.getByStudentId(studentId);
        result.put("points", points != null ? points.getBalance() : 0);

        // 成长档案最近3条
        List<GrowthRecordDTO> growth = growthRecordService.list(studentId, null);
        result.put("recentGrowth", growth.stream().limit(3).collect(Collectors.toList()));

        // 待审批请假
        List<LeaveDTO> leaves = leaveService.list(0, studentId);
        result.put("pendingLeaves", leaves.size());

        return ApiResult.success(result);
    }
}
