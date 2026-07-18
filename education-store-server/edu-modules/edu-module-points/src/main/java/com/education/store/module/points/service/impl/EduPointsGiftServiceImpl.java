package com.education.store.module.points.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.exception.BusinessException;
import com.education.store.module.points.entity.EduPointsGift;
import com.education.store.module.points.mapper.EduPointsGiftMapper;
import com.education.store.module.points.mapper.EduPointsRedemptionMapper;
import com.education.store.module.points.service.IEduPointsGiftService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 积分礼品 Service 实现
 */
@Service
@RequiredArgsConstructor
public class EduPointsGiftServiceImpl extends ServiceImpl<EduPointsGiftMapper, EduPointsGift>
        implements IEduPointsGiftService {

    private final EduPointsGiftMapper giftMapper;
    private final EduPointsRedemptionMapper redemptionMapper;

    @Override
    public IPage<EduPointsGift> selectGiftList(IPage<EduPointsGift> page, String keyword, Integer status) {
        LambdaQueryWrapper<EduPointsGift> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(EduPointsGift::getName, keyword);
        }
        if (status != null) {
            wrapper.eq(EduPointsGift::getStatus, status);
        }
        wrapper.orderByDesc(EduPointsGift::getCreateTime);
        return giftMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addGift(EduPointsGift gift) {
        // 礼品名称唯一校验
        if (existsByName(gift.getName())) {
            throw new BusinessException("GIFT_NAME_DUPLICATE", "礼品名称已存在");
        }
        // 新赠品库存初始化
        gift.setStockRemaining(gift.getStockTotal());
        return save(gift);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateGift(EduPointsGift gift) {
        EduPointsGift existing = giftMapper.selectById(gift.getId());
        if (existing == null) {
            throw new BusinessException("404", "礼品不存在");
        }
        // 名称唯一校验（排除自身）
        if (!existing.getName().equals(gift.getName()) && existsByName(gift.getName())) {
            throw new BusinessException("GIFT_NAME_DUPLICATE", "礼品名称已存在");
        }
        // 库存不可通过编辑修改，保持原值
        gift.setStockTotal(existing.getStockTotal());
        gift.setStockRemaining(existing.getStockRemaining());
        return updateById(gift);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, Integer status) {
        EduPointsGift gift = new EduPointsGift();
        gift.setId(id);
        gift.setStatus(status);
        return updateById(gift);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteGift(Long id) {
        // 校验是否存在待领取兑换记录
        int pendingCount = redemptionMapper.countPendingByGiftId(id);
        if (pendingCount > 0) {
            throw new BusinessException("GIFT_HAS_PENDING_REDEMPTION",
                    String.format("该礼品有 %d 条待领取兑换记录，无法删除", pendingCount));
        }
        return removeById(id);
    }

    /**
     * 检查礼品名称是否已存在
     */
    private boolean existsByName(String name) {
        LambdaQueryWrapper<EduPointsGift> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduPointsGift::getName, name);
        return giftMapper.selectCount(wrapper) > 0;
    }
}
