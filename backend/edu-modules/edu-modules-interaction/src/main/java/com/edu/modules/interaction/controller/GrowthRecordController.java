package com.edu.modules.interaction.controller;

import com.edu.common.core.response.ApiResult;
import com.edu.modules.interaction.dto.GrowthRecordDTO;
import com.edu.modules.interaction.dto.GrowthRecordRequest;
import com.edu.modules.interaction.service.GrowthRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/growth-records")
@RequiredArgsConstructor
@Tag(name = "成长档案", description = "学员评价、成长记录")
public class GrowthRecordController {

    private final GrowthRecordService growthRecordService;

    @PostMapping
    @Operation(summary = "新增成长记录")
    public ApiResult<Long> create(@Valid @RequestBody GrowthRecordRequest request) {
        Long id = growthRecordService.create(request);
        return ApiResult.success("添加成功", id);
    }

    @GetMapping
    @Operation(summary = "成长记录列表")
    public ApiResult<Map<String, List<GrowthRecordDTO>>> list(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long teacherId) {
        return ApiResult.success(Map.of("list", growthRecordService.list(studentId, teacherId)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "成长记录详情")
    public ApiResult<GrowthRecordDTO> detail(@PathVariable Long id) {
        return ApiResult.success(growthRecordService.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改成长记录")
    public ApiResult<Void> update(@PathVariable Long id,
                                   @Valid @RequestBody GrowthRecordRequest request) {
        growthRecordService.update(id, request);
        return ApiResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除成长记录")
    public ApiResult<Void> delete(@PathVariable Long id) {
        growthRecordService.delete(id);
        return ApiResult.success();
    }
}
