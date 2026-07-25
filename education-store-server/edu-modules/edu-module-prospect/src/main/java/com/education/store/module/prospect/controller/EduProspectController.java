package com.education.store.module.prospect.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.store.common.annotation.Log;
import com.education.store.common.enums.BusinessType;
import com.education.store.common.model.PageResult;
import com.education.store.common.model.Result;
import com.education.store.module.prospect.entity.EduProspect;
import com.education.store.module.prospect.service.IEduProspectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 意向学员管理控制器
 */
@Tag(name = "意向学员管理")
@RestController
@RequestMapping("/api/v1/prospects")
@RequiredArgsConstructor
public class EduProspectController {

    private final IEduProspectService prospectService;

    @Operation(summary = "意向学员列表(分页)")
    @PreAuthorize("@ss.hasPermi('student:prospect:list')")
    @GetMapping("/list")
    public Result<PageResult<EduProspect>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String mobile,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String followStatus,
            @RequestParam(required = false) Long counselorId,
            @RequestParam(required = false) String interestLevel) {
        Page<EduProspect> p = new Page<>(page, pageSize);
        EduProspect condition = new EduProspect();
        // keyword 同时模糊匹配姓名和手机号
        if (keyword != null && !keyword.isBlank()) {
            condition.setName(keyword);
            condition.setMobile(keyword);
        } else {
            condition.setName(name);
            condition.setMobile(mobile);
        }
        condition.setGrade(grade);
        condition.setFollowStatus(followStatus);
        condition.setCounselorId(counselorId);
        condition.setInterestLevel(interestLevel);
        IPage<EduProspect> result = prospectService.selectProspectList(p, condition);
        return Result.success(PageResult.of(result));
    }

    @Operation(summary = "意向学员详情")
    @PreAuthorize("@ss.hasPermi('student:prospect:query')")
    @GetMapping("/{id}")
    public Result<EduProspect> detail(@PathVariable Long id) {
        return Result.success(prospectService.selectProspectById(id));
    }

    @Operation(summary = "新增意向学员")
    @Log(title = "意向学员管理", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('student:prospect:add')")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody EduProspect prospect) {
        if (prospect.getMobile() != null && !prospect.getMobile().isBlank()
                && !prospectService.checkMobileUnique(prospect)) {
            return Result.error("手机号已被使用");
        }
        prospectService.insertProspect(prospect);
        return Result.success();
    }

    @Operation(summary = "更新意向学员")
    @Log(title = "意向学员管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('student:prospect:edit')")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody EduProspect prospect) {
        if (prospect.getId() == null) {
            return Result.badRequest("ID不能为空");
        }
        if (prospect.getMobile() != null && !prospect.getMobile().isBlank()
                && !prospectService.checkMobileUnique(prospect)) {
            return Result.error("手机号已被使用");
        }
        prospectService.updateProspect(prospect);
        return Result.success();
    }

    @Operation(summary = "批量删除意向学员")
    @Log(title = "意向学员管理", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('student:prospect:delete')")
    @DeleteMapping("/{ids}")
    public Result<Void> delete(@PathVariable Long[] ids) {
        prospectService.deleteProspectByIds(ids);
        return Result.success();
    }

    @Operation(summary = "转化意向学员为在学学员")
    @Log(title = "意向学员转化", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('student:prospect:convert')")
    @PostMapping("/{id}/convert")
    public Result<Map<String, Long>> convert(@PathVariable Long id) {
        Long studentId = prospectService.convertToStudent(id);
        return Result.success(Map.of("studentId", studentId));
    }
}
