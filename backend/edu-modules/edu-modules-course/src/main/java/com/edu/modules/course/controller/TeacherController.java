package com.edu.modules.course.controller;

import com.edu.common.core.response.ApiResult;
import com.edu.common.mybatis.PageParam;
import com.edu.common.mybatis.PageResult;
import com.edu.modules.course.dto.TeacherDTO;
import com.edu.modules.course.service.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/teachers")
@RequiredArgsConstructor
@Tag(name = "教师管理", description = "教师CRUD")
public class TeacherController {

    private final TeacherService teacherService;

    @GetMapping
    @Operation(summary = "教师列表")
    public ApiResult<PageResult<TeacherDTO>> list(@Valid PageParam param,
                                                   @RequestParam(required = false) String keyword,
                                                   @RequestParam(required = false) Integer status) {
        return ApiResult.success(teacherService.list(param, keyword, status));
    }

    @GetMapping("/{id}")
    @Operation(summary = "教师详情")
    public ApiResult<TeacherDTO> detail(@PathVariable Long id) {
        return ApiResult.success(teacherService.getById(id));
    }

    @PostMapping
    @Operation(summary = "新增教师")
    public ApiResult<Long> create(@Valid @RequestBody TeacherDTO dto) {
        return ApiResult.success("创建成功", teacherService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新教师")
    public ApiResult<Void> update(@PathVariable Long id, @Valid @RequestBody TeacherDTO dto) {
        teacherService.update(id, dto);
        return ApiResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除教师")
    public ApiResult<Void> delete(@PathVariable Long id) {
        teacherService.delete(id);
        return ApiResult.success();
    }
}
