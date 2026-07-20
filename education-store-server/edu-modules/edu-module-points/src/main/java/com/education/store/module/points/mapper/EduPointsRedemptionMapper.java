package com.education.store.module.points.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.store.module.points.entity.EduPointsRedemption;
import java.util.List;
import java.util.Map;

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
    @Select("SELECT COUNT(*) FROM edu_points_redemption WHERE gift_id = #{giftId} AND status = 'PENDING_CLAIM' AND deleted = 0")
    int countPendingByGiftId(@Param("giftId") Long giftId);

    /**
     * 批量统计每个礼品的兑换总数
     */
    @Select("<script>SELECT gift_id, COUNT(*) AS cnt FROM edu_points_redemption WHERE gift_id IN " +
            "<foreach item='id' collection='giftIds' open='(' separator=',' close=')'>#{id}</foreach>" +
            " AND deleted = 0 GROUP BY gift_id</script>")
    List<Map<String, Object>> countRedemptionByGiftIds(@Param("giftIds") List<Long> giftIds);
}
