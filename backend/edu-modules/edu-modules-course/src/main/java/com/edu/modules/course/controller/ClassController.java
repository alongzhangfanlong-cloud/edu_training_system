package com.edu.modules.course.controller;

import com.edu.common.core.response.ApiResult;
import com.edu.common.mybatis.PageParam;
import com.edu.common.mybatis.PageResult;
import com.edu.modules.course.dto.ClassDTO;
import com.edu.modules.course.service.ClassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/classes")
@RequiredArgsConstructor
@Tag(name = "班级管理", description = "班级CRUD")
public class ClassController {

    private final ClassService classService;

    @GetMapping
    @Operation(summary = "班级列表")
    public ApiResult<PageResult<ClassDTO>> list(@Valid PageParam param,
                                                 @RequestParam(required = false) Long courseId,
                                                 @RequestParam(required = false) Long teacherId,
                                                 @RequestParam(required = false) Integer status) {
        return ApiResult.success(classService.list(param, courseId, teacherId, status));
    }

    @GetMapping("/{id}")
    @Operation(summary = "班级详情")
    public ApiResult<ClassDTO> detail(@PathVariable Long id) {
        return ApiResult.success(classService.getById(id));
    }

    @PostMapping
    @Operation(summary = "新增班级")
    public ApiResult<Long> create(@Valid @RequestBody ClassDTO dto) {
        return ApiResult.success("创建成功", classService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新班级")
    public ApiResult<Void> update(@PathVariable Long id, @Valid @RequestBody ClassDTO dto) {
        classService.update(id, dto);
        return ApiResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除班级")
    public ApiResult<Void> delete(@PathVariable Long id) {
        classService.delete(id);
        return ApiResult.success();
    }
}
