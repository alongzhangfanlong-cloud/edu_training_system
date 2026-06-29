package com.edu.modules.interaction.controller;

import com.edu.common.core.response.ApiResult;
import com.edu.modules.interaction.dto.HomeworkDTO;
import com.edu.modules.interaction.dto.HomeworkRequest;
import com.edu.modules.interaction.service.HomeworkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/homework")
@RequiredArgsConstructor
@Tag(name = "作业管理", description = "作业布置、查看、批改")
public class HomeworkController {

    private final HomeworkService homeworkService;

    @PostMapping
    @Operation(summary = "布置作业")
    public ApiResult<Long> create(@Valid @RequestBody HomeworkRequest request) {
        Long id = homeworkService.create(request);
        return ApiResult.success("布置成功", id);
    }

    @GetMapping
    @Operation(summary = "作业列表")
    public ApiResult<Map<String, List<HomeworkDTO>>> list(
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Long teacherId) {
        return ApiResult.success(Map.of("list", homeworkService.list(classId, teacherId)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "作业详情")
    public ApiResult<HomeworkDTO> detail(@PathVariable Long id) {
        return ApiResult.success(homeworkService.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改作业")
    public ApiResult<Void> update(@PathVariable Long id,
                                   @Valid @RequestBody HomeworkRequest request) {
        homeworkService.update(id, request);
        return ApiResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除作业")
    public ApiResult<Void> delete(@PathVariable Long id) {
        homeworkService.delete(id);
        return ApiResult.success();
    }
}
