package com.education.store.module.attendance.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.store.common.annotation.Log;
import com.education.store.common.enums.BusinessType;
import com.education.store.common.model.PageResult;
import com.education.store.common.model.Result;
import com.education.store.module.attendance.entity.EduLeaveTeacher;
import com.education.store.module.attendance.service.IEduLeaveTeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "教师请假")
@RestController
@RequestMapping("/api/v1/attendance/leaves/teacher")
@RequiredArgsConstructor
public class EduLeaveTeacherController {

    private final IEduLeaveTeacherService leaveService;

    @Operation(summary = "教师请假列表")
    @GetMapping("/list")
    @PreAuthorize("isAuthenticated()")
    public Result<PageResult<EduLeaveTeacher>> list(@RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize, @RequestParam(required = false) Long teacherId,
            @RequestParam(required = false) Integer status) {
        Page<EduLeaveTeacher> p = new Page<>(page, pageSize);
        EduLeaveTeacher c = new EduLeaveTeacher(); c.setTeacherId(teacherId); c.setStatus(status);
        return Result.success(PageResult.of(leaveService.selectLeaveList(p, c)));
    }

    @Operation(summary = "教师请假详情")
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public Result<EduLeaveTeacher> detail(@PathVariable Long id) { return Result.success(leaveService.selectLeaveById(id)); }

    @Operation(summary = "新增教师请假")
    @PostMapping
    @Log(title = "教师请假", businessType = BusinessType.INSERT)
    @PreAuthorize("isAuthenticated()")
    public Result<Void> create(@Valid @RequestBody EduLeaveTeacher leave) { leaveService.insertLeave(leave); return Result.success(); }

    @Operation(summary = "审批通过教师请假")
    @PutMapping("/{id}/approve")
    @Log(title = "教师请假", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('attendance:leave:teacher:approve')")
    public Result<Void> approve(@PathVariable Long id, @RequestBody Map<String, Long> b) {
        leaveService.approveLeave(id, b.get("approverId")); return Result.success(); }

    @Operation(summary = "驳回教师请假")
    @PutMapping("/{id}/reject")
    @Log(title = "教师请假", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('attendance:leave:teacher:approve')")
    public Result<Void> reject(@PathVariable Long id, @RequestBody Map<String, String> b) {
        leaveService.rejectLeave(id, Long.valueOf(b.get("approverId")), b.getOrDefault("reason", "")); return Result.success(); }
}
