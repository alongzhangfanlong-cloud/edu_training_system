package com.edu.modules.course.controller;

import com.edu.common.core.response.ApiResult;
import com.edu.common.mybatis.PageParam;
import com.edu.common.mybatis.PageResult;
import com.edu.modules.course.dto.ClassroomDTO;
import com.edu.modules.course.service.ClassroomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/classrooms")
@RequiredArgsConstructor
@Tag(name = "教室管理", description = "教室CRUD")
public class ClassroomController {

    private final ClassroomService classroomService;

    @GetMapping
    @Operation(summary = "教室列表")
    public ApiResult<PageResult<ClassroomDTO>> list(@Valid PageParam param,
                                                     @RequestParam(required = false) String keyword) {
        return ApiResult.success(classroomService.list(param, keyword));
    }

    @GetMapping("/{id}")
    @Operation(summary = "教室详情")
    public ApiResult<ClassroomDTO> detail(@PathVariable Long id) {
        return ApiResult.success(classroomService.getById(id));
    }

    @PostMapping
    @Operation(summary = "新增教室")
    public ApiResult<Long> create(@Valid @RequestBody ClassroomDTO dto) {
        return ApiResult.success("创建成功", classroomService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新教室")
    public ApiResult<Void> update(@PathVariable Long id, @Valid @RequestBody ClassroomDTO dto) {
        classroomService.update(id, dto);
        return ApiResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除教室")
    public ApiResult<Void> delete(@PathVariable Long id) {
        classroomService.delete(id);
        return ApiResult.success();
    }
}
