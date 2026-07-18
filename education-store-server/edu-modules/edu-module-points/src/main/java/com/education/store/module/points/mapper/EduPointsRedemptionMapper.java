package com.education.store.module.points.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.store.module.points.entity.EduPointsRedemption;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 兑换记录 Mapper
 */
@Mapper
public interface EduPointsRedemptionMapper extends BaseMapper<EduPointsRedemption> {

    /**
     * 统计礼品未完成的兑换记录数
     */
    @Select("SELECT COUNT(*) FROM edu_points_redemption WHERE gift_id = #{giftId} AND status = 'PENDING_CLAIM' AND is_deleted = 0")
    int countPendingByGiftId(@Param("giftId") Long giftId);
}
