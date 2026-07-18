package com.education.store.module.attendance.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.store.common.annotation.Log;
import com.education.store.common.enums.BusinessType;
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

import java.util.Map;

@Tag(name = "学生请假")
@RestController
@RequestMapping("/api/v1/attendance/leaves/student")
@RequiredArgsConstructor
public class EduLeaveStudentController {

    private final IEduLeaveStudentService leaveService;

    @GetMapping("/list") @PreAuthorize("@ss.hasPermi('attendance:leave:student:list')")
    public Result<PageResult<EduLeaveStudent>> list(@RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize, @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Integer status) {
        Page<EduLeaveStudent> p = new Page<>(page, pageSize);
        EduLeaveStudent c = new EduLeaveStudent(); c.setStudentId(studentId); c.setStatus(status);
        return Result.success(PageResult.of(leaveService.selectLeaveList(p, c)));
    }

    @GetMapping("/{id}") @PreAuthorize("@ss.hasPermi('attendance:leave:student:query')")
    public Result<EduLeaveStudent> detail(@PathVariable Long id) { return Result.success(leaveService.selectLeaveById(id)); }

    @PostMapping @Log(title = "学生请假", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('attendance:leave:student:add')")
    public Result<Void> create(@Valid @RequestBody EduLeaveStudent leave) { leaveService.insertLeave(leave); return Result.success(); }

    @PutMapping("/{id}/approve") @Log(title = "学生请假", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('attendance:leave:student:approve')")
    public Result<Void> approve(@PathVariable Long id, @RequestBody Map<String, Long> b) {
        leaveService.approveLeave(id, b.get("teacherId")); return Result.success(); }

    @PutMapping("/{id}/reject") @Log(title = "学生请假", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('attendance:leave:student:approve')")
    public Result<Void> reject(@PathVariable Long id, @RequestBody Map<String, String> b) {
        leaveService.rejectLeave(id, Long.valueOf(b.get("teacherId")), b.getOrDefault("reason", "")); return Result.success(); }
}
