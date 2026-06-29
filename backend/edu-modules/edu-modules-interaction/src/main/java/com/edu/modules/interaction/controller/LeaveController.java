package com.edu.modules.interaction.controller;

import com.edu.common.core.response.ApiResult;
import com.edu.modules.interaction.dto.LeaveApproveRequest;
import com.edu.modules.interaction.dto.LeaveDTO;
import com.edu.modules.interaction.dto.LeaveRequest;
import com.edu.modules.interaction.service.LeaveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/leaves")
@RequiredArgsConstructor
@Tag(name = "请假管理", description = "请假申请、审批")
public class LeaveController {

    private final LeaveService leaveService;

    @PostMapping
    @Operation(summary = "提交请假申请")
    public ApiResult<Long> apply(@Valid @RequestBody LeaveRequest request) {
        Long id = leaveService.apply(request);
        return ApiResult.success("申请已提交", id);
    }

    @GetMapping
    @Operation(summary = "请假列表")
    public ApiResult<Map<String, List<LeaveDTO>>> list(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long studentId) {
        return ApiResult.success(Map.of("list", leaveService.list(status, studentId)));
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "审批请假")
    public ApiResult<Void> approve(@PathVariable Long id,
                                    @Valid @RequestBody LeaveApproveRequest request) {
        leaveService.approve(id, request.getApproved(), request.getRemark());
        return ApiResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除请假记录")
    public ApiResult<Void> delete(@PathVariable Long id) {
        leaveService.delete(id);
        return ApiResult.success();
    }
}
