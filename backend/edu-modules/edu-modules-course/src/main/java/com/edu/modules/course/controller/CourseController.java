package com.edu.modules.course.controller;

import com.edu.common.core.response.ApiResult;
import com.edu.common.mybatis.PageParam;
import com.edu.common.mybatis.PageResult;
import com.edu.modules.course.dto.CourseDTO;
import com.edu.modules.course.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
@Tag(name = "课程管理", description = "课程CRUD")
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    @Operation(summary = "课程列表")
    public ApiResult<PageResult<CourseDTO>> list(
            @Valid PageParam param,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status) {
        return ApiResult.success(courseService.list(param, keyword, type, status));
    }

    @GetMapping("/{id}")
    @Operation(summary = "课程详情")
    public ApiResult<CourseDTO> detail(@PathVariable Long id) {
        return ApiResult.success(courseService.getById(id));
    }

    @PostMapping
    @Operation(summary = "新增课程")
    public ApiResult<Long> create(@Valid @RequestBody CourseDTO dto) {
        return ApiResult.success("创建成功", courseService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新课程")
    public ApiResult<Void> update(@PathVariable Long id, @Valid @RequestBody CourseDTO dto) {
        courseService.update(id, dto);
        return ApiResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "下架课程")
    public ApiResult<Void> delete(@PathVariable Long id) {
        courseService.delete(id);
        return ApiResult.success();
    }
}
