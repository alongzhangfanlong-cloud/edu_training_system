package com.edu.modules.interaction.controller;

import com.edu.common.core.response.ApiResult;
import com.edu.modules.interaction.dto.PointChangeRequest;
import com.edu.modules.interaction.dto.PointsDTO;
import com.edu.modules.interaction.entity.PointTransactionEntity;
import com.edu.modules.interaction.service.PointsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/points")
@RequiredArgsConstructor
@Tag(name = "积分管理", description = "积分查询、变动、流水")
public class PointsController {

    private final PointsService pointsService;

    @GetMapping
    @Operation(summary = "积分列表(所有学员)")
    public ApiResult<Map<String, List<PointsDTO>>> list() {
        return ApiResult.success(Map.of("list", pointsService.list()));
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "查询学员积分")
    public ApiResult<PointsDTO> getByStudent(@PathVariable Long studentId) {
        return ApiResult.success(pointsService.getByStudentId(studentId));
    }

    @PostMapping("/change")
    @Operation(summary = "积分变动(增加/扣减)")
    public ApiResult<PointsDTO> changePoints(@Valid @RequestBody PointChangeRequest request) {
        return ApiResult.success(pointsService.changePoints(request));
    }

    @GetMapping("/student/{studentId}/transactions")
    @Operation(summary = "积分流水")
    public ApiResult<Map<String, List<PointTransactionEntity>>> transactions(@PathVariable Long studentId) {
        return ApiResult.success(Map.of("list", pointsService.getTransactions(studentId)));
    }
}
