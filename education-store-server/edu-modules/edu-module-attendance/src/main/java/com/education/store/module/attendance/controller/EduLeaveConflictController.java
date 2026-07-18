package com.education.store.module.attendance.controller;

import com.education.store.common.annotation.Log;
import com.education.store.common.enums.BusinessType;
import com.education.store.common.model.Result;
import com.education.store.module.attendance.entity.EduLeaveConflict;
import com.education.store.module.attendance.service.IEduLeaveConflictService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "冲突处理")
@RestController
@RequestMapping("/api/v1/attendance/conflicts")
@RequiredArgsConstructor
public class EduLeaveConflictController {

    private final IEduLeaveConflictService conflictService;

    @GetMapping("/leave/{leaveId}") @PreAuthorize("@ss.hasPermi('attendance:leave:teacher:query')")
    public Result<?> listByLeave(@PathVariable Long leaveId) { return Result.success(conflictService.selectByLeaveId(leaveId)); }

    @PutMapping("/{id}/resolve") @Log(title = "冲突处理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('attendance:leave:teacher:resolve')")
    public Result<Void> resolve(@PathVariable Long id, @RequestBody EduLeaveConflict resolution) {
        conflictService.resolveConflict(id, resolution); return Result.success(); }
}
