package com.education.store.module.points.service.impl;

import com.education.store.module.points.entity.EduPointsAccount;
import com.education.store.module.points.mapper.EduPointsAccountMapper;
import com.education.store.module.points.service.IEduPointsAccountService;
import com.education.store.module.points.service.IEduPointsLogService;
import com.education.store.module.points.service.PointsInternalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 积分模块内部服务实现 — 供其他模块调用
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PointsInternalServiceImpl implements PointsInternalService {

    private final IEduPointsAccountService accountService;
    private final IEduPointsLogService logService;
    private final EduPointsAccountMapper accountMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void earnPoints(Long studentId, Long teacherId, String sourceType,
                           Long sourceId, int amount, String remark) {
        if (amount <= 0) {
            log.warn("积分发放金额无效：studentId={}, amount={}", studentId, amount);
            return;
        }

        // 确保账户存在（若已存在则忽略）
        EduPointsAccount account = accountService.initAccount(studentId);

        // 先原子写入日志（利用 source_type + source_id 的防重逻辑），再原子增加积分
        // 日志写入内部做防重检查，此处减少 TOCTOU 窗口
        if (!logService.tryWriteLog(studentId, "EARN", amount, sourceType, sourceId, remark)) {
            log.info("积分已发放，跳过：studentId={}, sourceType={}, sourceId={}", studentId, sourceType, sourceId);
            return;
        }

        // 原子增加积分
        accountMapper.addPoints(studentId, amount);

        // 重新查询余额用于返回值日志
        account = accountService.getByStudentId(studentId);
        int balanceAfter = account != null ? account.getBalance() : amount;

        log.info("积分发放成功：studentId={}, amount={}, balanceAfter={}, sourceType={}, sourceId={}",
                studentId, amount, balanceAfter, sourceType, sourceId);
    }
}
