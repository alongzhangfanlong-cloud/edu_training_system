package com.education.store.module.attendance.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.store.common.annotation.Log;
import com.education.store.common.enums.BusinessType;
import com.education.store.common.exception.BusinessException;
import com.education.store.common.model.PageResult;
import com.education.store.common.model.Result;
import com.education.store.module.attendance.entity.EduLeaveStudent;
import com.education.store.module.attendance.service.IEduLeaveStudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@Tag(name = "学生请假")
@RestController
@RequestMapping("/api/v1/attendance/leaves/student")
@RequiredArgsConstructor
public class EduLeaveStudentController {

    private static final long MAX_PAGE_SIZE = 100;

    private final IEduLeaveStudentService leaveService;

    @Operation(summary = "学员请假列表")
    @GetMapping("/list")
    @PreAuthorize("isAuthenticated()")
    public Result<PageResult<EduLeaveStudent>> list(@RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize, @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Integer status) {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 10;
        if (pageSize > MAX_PAGE_SIZE) pageSize = MAX_PAGE_SIZE;
        Page<EduLeaveStudent> p = new Page<>(page, pageSize);
        EduLeaveStudent c = new EduLeaveStudent(); c.setStudentId(studentId); c.setStatus(status);
        IPage<EduLeaveStudent> result = leaveService.selectLeaveList(p, c);
        if (result == null) {
            return Result.success(new PageResult<>(Collections.emptyList(), 0, page, pageSize));
        }
        return Result.success(PageResult.of(result));
    }

    @Operation(summary = "学员请假详情")
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public Result<EduLeaveStudent> detail(@PathVariable Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException("请假ID不能为空");
        }
        EduLeaveStudent leave = leaveService.selectLeaveById(id);
        if (leave == null) {
            return Result.notFound("请假记录不存在");
        }
        return Result.success(leave);
    }

    @Operation(summary = "新增学员请假")
    @PostMapping
    @Log(title = "学生请假", businessType = BusinessType.INSERT)
    @PreAuthorize("isAuthenticated()")
    public Result<Void> create(@Valid @RequestBody EduLeaveStudent leave) {
        if (leave == null) {
            throw new BusinessException("请假信息不能为空");
        }
        if (leave.getStudentId() == null || leave.getStudentId() <= 0) {
            throw new BusinessException("学生ID不能为空");
        }
        leaveService.insertLeave(leave);
        return Result.success();
    }

    @Operation(summary = "审批通过学员请假")
    @PutMapping("/{id}/approve")
    @Log(title = "学生请假", businessType = BusinessType.UPDATE)
    @PreAuthorize("isAuthenticated()")
    public Result<Void> approve(@PathVariable Long id, @RequestBody(required = false) Map<String, Object> b) {
        if (id == null || id <= 0) {
            throw new BusinessException("请假ID不能为空");
        }
        Long teacherId = null;
        if (b != null) {
            Object tid = b.get("teacherId");
            if (tid != null) {
                try {
                    teacherId = Long.valueOf(String.valueOf(tid));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        leaveService.approveLeave(id, teacherId);
        return Result.success();
    }

    @Operation(summary = "驳回学员请假")
    @PutMapping("/{id}/reject")
    @Log(title = "学生请假", businessType = BusinessType.UPDATE)
    @PreAuthorize("isAuthenticated()")
    public Result<Void> reject(@PathVariable Long id, @RequestBody(required = false) Map<String, Object> b) {
        if (id == null || id <= 0) {
            throw new BusinessException("请假ID不能为空");
        }
        Long teacherId = null;
        String reason = "";
        if (b != null) {
            Object tid = b.get("teacherId");
            if (tid != null) {
                try {
                    teacherId = Long.valueOf(String.valueOf(tid));
                } catch (NumberFormatException ignored) {
                }
            }
            Object r = b.get("reason");
            if (r != null) {
                reason = String.valueOf(r);
                if (reason.length() > 200) reason = reason.substring(0, 200);
            }
        }
        leaveService.rejectLeave(id, teacherId, reason);
        return Result.success();
    }
}
