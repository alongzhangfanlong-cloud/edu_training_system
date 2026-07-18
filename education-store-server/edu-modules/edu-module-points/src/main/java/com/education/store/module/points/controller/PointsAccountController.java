package com.education.store.module.points.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.store.common.annotation.Log;
import com.education.store.common.enums.BusinessType;
import com.education.store.common.exception.BusinessException;
import com.education.store.common.model.PageResult;
import com.education.store.common.model.Result;
import com.education.store.module.points.entity.EduPointsAccount;
import com.education.store.module.points.entity.EduPointsLog;
import com.education.store.module.points.service.IEduPointsAccountService;
import com.education.store.module.points.service.IEduPointsLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 积分账户 Controller
 */
@Tag(name = "积分账户管理")
@RestController
@RequestMapping("/api/v1/points/account")
@RequiredArgsConstructor
public class PointsAccountController {

    private final IEduPointsAccountService accountService;
    private final IEduPointsLogService logService;

    @Operation(summary = "查询学生积分余额")
    @PreAuthorize("@ss.hasPermi('points:account:query')")
    @GetMapping("/{studentId}")
    public Result<EduPointsAccount> balance(@PathVariable Long studentId) {
        EduPointsAccount account = accountService.getByStudentId(studentId);
        if (account == null) {
            // 不存在则初始化
            account = accountService.initAccount(studentId);
        }
        return Result.success(account);
    }

    @Operation(summary = "查询学生积分日志")
    @PreAuthorize("@ss.hasPermi('points:account:query')")
    @GetMapping("/log/{studentId}")
    public Result<PageResult<EduPointsLog>> logList(@PathVariable Long studentId,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) String changeType) {
        Page<EduPointsLog> p = new Page<>(page, pageSize);
        IPage<EduPointsLog> result = logService.selectLogList(p, studentId, changeType);
        return Result.success(PageResult.of(result));
    }

    @Operation(summary = "管理员手动调整积分")
    @Log(title = "积分账户管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('points:account:adjust')")
    @PostMapping("/admin/adjust")
    public Result<EduPointsAccount> adjust(@RequestParam Long studentId,
            @RequestParam Integer amount,
            @RequestParam String remark) {
        // 校验参数
        if (amount == null || amount == 0) {
            throw new BusinessException("400", "调整积分不能为0");
        }
        if (remark == null || remark.trim().isEmpty()) {
            throw new BusinessException("POINTS_ADJUST_NO_REASON", "必须填写调整原因");
        }
        if (amount < -9999 || amount > 9999) {
            throw new BusinessException("400", "调整积分范围为 -9999 ~ 9999");
        }

        // 确保账户存在
        EduPointsAccount account = accountService.getByStudentId(studentId);
        if (account == null) {
            account = accountService.initAccount(studentId);
        }

        // 扣减时校验余额
        if (amount < 0 && account.getBalance() + amount < 0) {
            throw new BusinessException("INSUFFICIENT_POINTS", "积分余额不足");
        }

        // 更新账户
        int balanceAfter = account.getBalance() + amount;
        account.setBalance(balanceAfter);
        if (amount > 0) {
            account.setTotalEarned(account.getTotalEarned() + amount);
        } else {
            account.setTotalSpent(account.getTotalSpent() + Math.abs(amount));
        }
        accountService.updateById(account);

        // 写入日志
        logService.writeLog(studentId, "ADJUST", amount, balanceAfter,
                "ADJUST", null, remark);

        return Result.success(account);
    }
}
