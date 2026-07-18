package com.education.store.module.points.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.points.entity.EduPointsGift;

/**
 * 积分礼品 Service
 */
public interface IEduPointsGiftService extends IService<EduPointsGift> {

    /**
     * 分页查询礼品列表（支持关键词+状态筛选）
     */
    IPage<EduPointsGift> selectGiftList(IPage<EduPointsGift> page, String keyword, Integer status);

    /**
     * 新增礼品
     */
    boolean addGift(EduPointsGift gift);

    /**
     * 编辑礼品（库存不可通过此接口修改）
     */
    boolean updateGift(EduPointsGift gift);

    /**
     * 启用/禁用礼品
     */
    boolean updateStatus(Long id, Integer status);

    /**
     * 删除礼品（校验待领取兑换记录）
     */
    boolean deleteGift(Long id);
}
