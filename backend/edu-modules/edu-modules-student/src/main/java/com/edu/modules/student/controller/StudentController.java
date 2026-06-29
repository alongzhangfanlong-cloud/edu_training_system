package com.edu.modules.student.controller;

import com.edu.common.core.response.ApiResult;
import com.edu.common.mybatis.PageResult;
import com.edu.modules.student.dto.*;
import com.edu.modules.student.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@Tag(name = "学员管理", description = "学员CRUD及课时查询")
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    @Operation(summary = "学员列表")
    public ApiResult<PageResult<StudentListItemDTO>> list(@Valid StudentQueryParam param) {
        return ApiResult.success(studentService.list(param));
    }

    @PostMapping
    @Operation(summary = "新增学员")
    public ApiResult<Long> create(@Valid @RequestBody CreateStudentRequest request) {
        Long id = studentService.create(request);
        return ApiResult.success("创建成功", id);
    }

    @GetMapping("/{id}")
    @Operation(summary = "学员详情")
    public ApiResult<StudentDetailDTO> detail(@PathVariable Long id) {
        return ApiResult.success(studentService.getStudentDetail(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新学员信息")
    public ApiResult<Void> update(@PathVariable Long id,
                                   @Valid @RequestBody UpdateStudentRequest request) {
        studentService.update(id, request);
        return ApiResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除学员")
    public ApiResult<Void> delete(@PathVariable Long id) {
        studentService.delete(id);
        return ApiResult.success();
    }

    @GetMapping("/{id}/hours")
    @Operation(summary = "学员课时明细")
    public ApiResult<PageResult<HourTransactionDTO>> hourDetails(
            @PathVariable Long id,
            @RequestParam(required = false) Long classId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return ApiResult.success(studentService.hourDetails(id, classId, page, pageSize));
    }
}
