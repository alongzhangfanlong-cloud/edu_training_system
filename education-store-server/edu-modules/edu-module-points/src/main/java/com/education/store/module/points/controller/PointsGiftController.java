package com.education.store.module.points.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.store.common.annotation.Log;
import com.education.store.common.enums.BusinessType;
import com.education.store.common.model.PageResult;
import com.education.store.common.model.Result;
import com.education.store.module.points.entity.EduPointsGift;
import com.education.store.module.points.service.IEduPointsGiftService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 积分礼品 Controller
 */
@Tag(name = "积分礼品管理")
@RestController
@RequestMapping("/api/v1/points/gift")
@RequiredArgsConstructor
public class PointsGiftController {

    private final IEduPointsGiftService giftService;

    @Operation(summary = "分页查询礼品列表")
    @PreAuthorize("@ss.hasPermi('points:gift:list')")
    @GetMapping("/list")
    public Result<PageResult<EduPointsGift>> list(@RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        Page<EduPointsGift> p = new Page<>(page, pageSize);
        IPage<EduPointsGift> result = giftService.selectGiftList(p, keyword, status);
        return Result.success(PageResult.of(result));
    }

    @Operation(summary = "查询礼品详情")
    @PreAuthorize("@ss.hasPermi('points:gift:list')")
    @GetMapping("/{id}")
    public Result<EduPointsGift> detail(@PathVariable Long id) {
        EduPointsGift gift = giftService.getById(id);
        if (gift == null) {
            return Result.notFound("礼品不存在");
        }
        return Result.success(gift);
    }

    @Operation(summary = "新增礼品")
    @Log(title = "积分礼品管理", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('points:gift:add')")
    @PostMapping
    public Result<EduPointsGift> add(@RequestBody EduPointsGift gift) {
        giftService.addGift(gift);
        return Result.success(gift);
    }

    @Operation(summary = "编辑礼品")
    @Log(title = "积分礼品管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('points:gift:edit')")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody EduPointsGift gift) {
        gift.setId(id);
        giftService.updateGift(gift);
        return Result.success();
    }

    @Operation(summary = "启用/禁用礼品")
    @Log(title = "积分礼品管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('points:gift:edit')")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody EduPointsGift gift) {
        giftService.updateStatus(id, gift.getStatus());
        return Result.success();
    }

    @Operation(summary = "删除礼品")
    @Log(title = "积分礼品管理", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('points:gift:delete')")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        giftService.deleteGift(id);
        return Result.success();
    }
}
