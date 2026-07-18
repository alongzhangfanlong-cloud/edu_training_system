package com.education.store.module.points.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.store.module.points.entity.EduPointsGift;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 积分礼品 Mapper
 */
@Mapper
public interface EduPointsGiftMapper extends BaseMapper<EduPointsGift> {

    /**
     * 原子扣减库存（条件更新，利用受影响行数防止超卖）
     */
    @Update("UPDATE edu_points_gift SET stock_remaining = stock_remaining - 1 WHERE id = #{id} AND stock_remaining > 0 AND deleted = 0")
    int deductStock(@Param("id") Long id);

    /**
     * 恢复库存
     */
    @Update("UPDATE edu_points_gift SET stock_remaining = stock_remaining + 1 WHERE id = #{id}")
    int restoreStock(@Param("id") Long id);
}
