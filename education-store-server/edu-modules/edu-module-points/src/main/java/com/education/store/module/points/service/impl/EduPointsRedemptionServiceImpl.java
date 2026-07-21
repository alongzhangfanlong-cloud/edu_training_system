package com.education.store.module.points.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.event.PointsRedemptionEvent;
import com.education.store.common.exception.BusinessException;
import com.education.store.module.points.entity.EduPointsAccount;
import com.education.store.module.points.entity.EduPointsGift;
import com.education.store.module.points.entity.EduPointsRedemption;
import com.education.store.module.points.mapper.EduPointsAccountMapper;
import com.education.store.module.points.mapper.EduPointsGiftMapper;
import com.education.store.module.points.mapper.EduPointsRedemptionMapper;
import com.education.store.module.points.service.IEduPointsLogService;
import com.education.store.module.points.service.IEduPointsRedemptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 兑换记录 Service 实现
 */
@Service
@RequiredArgsConstructor
public class EduPointsRedemptionServiceImpl extends ServiceImpl<EduPointsRedemptionMapper, EduPointsRedemption>
        implements IEduPointsRedemptionService {

    private final EduPointsRedemptionMapper redemptionMapper;
    private final EduPointsGiftMapper giftMapper;
    private final EduPointsAccountMapper accountMapper;
    private final IEduPointsLogService pointsLogService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public IPage<EduPointsRedemption> selectRedemptionList(IPage<EduPointsRedemption> page,
                                                            Long studentId, Long giftId,
                                                            String status, String startDate, String endDate) {
        LambdaQueryWrapper<EduPointsRedemption> wrapper = new LambdaQueryWrapper<>();
        if (studentId != null) {
            wrapper.eq(EduPointsRedemption::getStudentId, studentId);
        }
        if (giftId != null) {
            wrapper.eq(EduPointsRedemption::getGiftId, giftId);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(EduPointsRedemption::getStatus, status);
        }
        if (startDate != null && !startDate.isEmpty()) {
            wrapper.ge(EduPointsRedemption::getRedeemTime, startDate + " 00:00:00");
        }
        if (endDate != null && !endDate.isEmpty()) {
            wrapper.le(EduPointsRedemption::getRedeemTime, endDate + " 23:59:59");
        }
        wrapper.orderByDesc(EduPointsRedemption::getRedeemTime);
        return redemptionMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EduPointsRedemption redeem(Long studentId, Long giftId, Long operatorId) {
        // 1. 行锁锁定礼品记录
        EduPointsGift gift = giftMapper.selectById(giftId);
        if (gift == null || gift.getStockRemaining() <= 0) {
            throw new BusinessException("INSUFFICIENT_STOCK", "库存不足");
        }
        if (gift.getStatus() == 0) {
            throw new BusinessException("GIFT_DISABLED", "该礼品已下架");
        }

        // 2. 行锁锁定积分账户
        EduPointsAccount account = accountMapper.selectByStudentIdForUpdate(studentId);
        if (account == null || account.getBalance() < gift.getPointsRequired()) {
            throw new BusinessException("INSUFFICIENT_POINTS", "积分余额不足");
        }

        // 3. 扣减库存（条件更新，受影响行数=0 则超卖）
        int rows = giftMapper.deductStock(giftId);
        if (rows == 0) {
            throw new BusinessException("INSUFFICIENT_STOCK", "库存已被抢完");
        }

        // 4. 扣减积分
        accountMapper.deductPoints(studentId, gift.getPointsRequired());

        // 5. 创建兑换记录
        EduPointsRedemption redemption = new EduPointsRedemption();
        redemption.setStudentId(studentId);
        redemption.setGiftId(giftId);
        redemption.setPointsSpent(gift.getPointsRequired());
        redemption.setStatus("PENDING_CLAIM");
        redemption.setRedeemTime(LocalDateTime.now());
        redemption.setOperatorId(operatorId);
        redemptionMapper.insert(redemption);

        // 6. 写入积分日志
        int balanceAfter = account.getBalance() - gift.getPointsRequired();
        pointsLogService.writeLog(studentId, "REDEEM", -gift.getPointsRequired(), balanceAfter,
                "REDEEM", redemption.getId(), "兑换礼品：" + gift.getName());

        return redemption;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void claim(Long id, Long operatorId) {
        EduPointsRedemption redemption = redemptionMapper.selectById(id);
        if (redemption == null) {
            throw new BusinessException("404", "兑换记录不存在");
        }
        if (!"PENDING_CLAIM".equals(redemption.getStatus())) {
            throw new BusinessException("STATUS_NOT_ALLOWED", "当前状态不允许此操作，请刷新后重试");
        }
        redemption.setStatus("CLAIMED");
        redemption.setClaimTime(LocalDateTime.now());
        redemption.setOperatorId(operatorId);
        redemptionMapper.updateById(redemption);

        // 发布兑换通知事件
        EduPointsGift gift = giftMapper.selectById(redemption.getGiftId());
        String giftName = gift != null ? gift.getName() : "礼品";
        eventPublisher.publishEvent(new PointsRedemptionEvent(
            this, id, redemption.getStudentId(), giftName, "CLAIMED",
            redemption.getPointsSpent()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long id, Long operatorId) {
        EduPointsRedemption redemption = redemptionMapper.selectById(id);
        if (redemption == null) {
            throw new BusinessException("404", "兑换记录不存在");
        }
        if (!"PENDING_CLAIM".equals(redemption.getStatus())) {
            throw new BusinessException("STATUS_NOT_ALLOWED", "当前状态不允许此操作，请刷新后重试");
        }

        // 1. 更新兑换记录状态
        redemption.setStatus("CANCELLED");
        redemption.setCancelTime(LocalDateTime.now());
        redemption.setOperatorId(operatorId);
        redemptionMapper.updateById(redemption);

        // 2. 退还积分
        accountMapper.refundPoints(redemption.getStudentId(), redemption.getPointsSpent());

        // 3. 写入积分日志（balance 已在 refundPoints 后，重新查询）
        EduPointsAccount account = accountMapper.selectByStudentIdForUpdate(redemption.getStudentId());
        int balanceAfter = account != null ? account.getBalance() : redemption.getPointsSpent();
        pointsLogService.writeLog(redemption.getStudentId(), "ADJUST", redemption.getPointsSpent(), balanceAfter,
                "CANCEL_REDEEM", redemption.getId(), "取消兑换，退还积分");

        // 4. 恢复库存
        giftMapper.restoreStock(redemption.getGiftId());

        // 发布兑换取消通知
        EduPointsGift gift = giftMapper.selectById(redemption.getGiftId());
        String giftName = gift != null ? gift.getName() : "礼品";
        eventPublisher.publishEvent(new PointsRedemptionEvent(
            this, id, redemption.getStudentId(), giftName, "CANCELLED",
            redemption.getPointsSpent()));
    }
}
