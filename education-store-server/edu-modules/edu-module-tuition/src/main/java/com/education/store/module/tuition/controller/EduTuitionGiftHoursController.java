package com.education.store.module.tuition.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.store.common.annotation.Log;
import com.education.store.common.enums.BusinessType;
import com.education.store.common.model.PageResult;
import com.education.store.common.model.Result;
import com.education.store.module.tuition.entity.EduTuitionGiftHours;
import com.education.store.module.tuition.service.IEduTuitionGiftHoursService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "赠送课时")
@RestController
@RequestMapping("/api/v1/tuition/gifts")
@RequiredArgsConstructor
public class EduTuitionGiftHoursController {

    private final IEduTuitionGiftHoursService giftService;

    @Operation(summary = "赠课记录列表")
    @GetMapping("/list") @PreAuthorize("@ss.hasPermi('tuition:gift:list')")
    public Result<PageResult<EduTuitionGiftHours>> list(@RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize, @RequestParam(required = false) Long studentId) {
        Page<EduTuitionGiftHours> p = new Page<>(page, pageSize);
        EduTuitionGiftHours c = new EduTuitionGiftHours(); c.setStudentId(studentId);
        return Result.success(PageResult.of(giftService.selectGiftList(p, c)));
    }

    @Operation(summary = "赠课记录详情")
    @GetMapping("/{id}") @PreAuthorize("@ss.hasPermi('tuition:gift:list')")
    public Result<EduTuitionGiftHours> detail(@PathVariable Long id) { return Result.success(giftService.selectGiftById(id)); }

    @Operation(summary = "新增赠课记录")
    @PostMapping @Log(title = "赠送课时", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('tuition:gift:add')")
    public Result<Void> create(@Valid @RequestBody EduTuitionGiftHours gift) { giftService.insertGift(gift); return Result.success(); }

    @Operation(summary = "删除赠课记录")
    @DeleteMapping("/{ids}") @Log(title = "赠送课时", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('tuition:gift:delete')")
    public Result<Void> delete(@PathVariable Long[] ids) { giftService.deleteGiftByIds(ids); return Result.success(); }
}
