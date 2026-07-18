package com.education.store.module.points.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.points.entity.EduPointsRedemption;

/**
 * 兑换记录 Service
 */
public interface IEduPointsRedemptionService extends IService<EduPointsRedemption> {

    /**
     * 分页查询兑换记录（支持多条件筛选）
     */
    IPage<EduPointsRedemption> selectRedemptionList(IPage<EduPointsRedemption> page,
                                                     Long studentId, Long giftId,
                                                     String status, String startDate, String endDate);

    /**
     * 兑换礼品（库存扣减 + 积分扣减 + 日志写入）
     */
    EduPointsRedemption redeem(Long studentId, Long giftId, Long operatorId);

    /**
     * 标记已领取（核销）
     */
    void claim(Long id, Long operatorId);

    /**
     * 取消兑换（退积分+恢复库存，事务操作）
     */
    void cancel(Long id, Long operatorId);
}
