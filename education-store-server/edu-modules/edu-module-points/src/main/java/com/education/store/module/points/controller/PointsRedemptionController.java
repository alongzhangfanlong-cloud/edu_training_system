package com.education.store.module.points.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.store.common.annotation.Log;
import com.education.store.common.enums.BusinessType;
import com.education.store.common.model.PageResult;
import com.education.store.common.model.Result;
import com.education.store.module.points.entity.EduPointsRedemption;
import com.education.store.module.points.service.IEduPointsRedemptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 兑换记录 Controller
 */
@Tag(name = "兑换记录管理")
@RestController
@RequestMapping("/api/v1/points/redemption")
@RequiredArgsConstructor
public class PointsRedemptionController {

    private final IEduPointsRedemptionService redemptionService;

    @Operation(summary = "分页查询兑换记录")
    @PreAuthorize("@ss.hasPermi('points:redemption:list')")
    @GetMapping("/list")
    public Result<PageResult<EduPointsRedemption>> list(@RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long giftId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Page<EduPointsRedemption> p = new Page<>(page, pageSize);
        IPage<EduPointsRedemption> result = redemptionService.selectRedemptionList(
                p, studentId, giftId, status, startDate, endDate);
        return Result.success(PageResult.of(result));
    }

    @Operation(summary = "查询兑换记录详情")
    @PreAuthorize("@ss.hasPermi('points:redemption:list')")
    @GetMapping("/{id}")
    public Result<EduPointsRedemption> detail(@PathVariable Long id) {
        EduPointsRedemption redemption = redemptionService.getById(id);
        if (redemption == null) {
            return Result.notFound("兑换记录不存在");
        }
        return Result.success(redemption);
    }

    @Operation(summary = "兑换礼品")
    @Log(title = "兑换记录管理", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('points:redemption:list')")
    @PostMapping
    public Result<EduPointsRedemption> redeem(@RequestParam Long studentId,
            @RequestParam Long giftId,
            @RequestParam(required = false) Long operatorId) {
        EduPointsRedemption redemption = redemptionService.redeem(studentId, giftId, operatorId);
        return Result.success(redemption);
    }

    @Operation(summary = "标记已领取（核销）")
    @Log(title = "兑换记录管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('points:redemption:claim')")
    @PostMapping("/{id}/claim")
    public Result<Void> claim(@PathVariable Long id, @RequestParam(required = false) Long operatorId) {
        redemptionService.claim(id, operatorId);
        return Result.success();
    }

    @Operation(summary = "取消兑换")
    @Log(title = "兑换记录管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('points:redemption:cancel')")
    @PostMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id, @RequestParam(required = false) Long operatorId) {
        redemptionService.cancel(id, operatorId);
        return Result.success();
    }
}
