package com.education.store.module.points.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.store.module.points.entity.EduPointsLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 积分日志 Mapper（只追加）
 */
@Mapper
public interface EduPointsLogMapper extends BaseMapper<EduPointsLog> {

    /**
     * 查重：同一来源是否已有日志记录
     */
    @Select("SELECT COUNT(*) FROM edu_points_log WHERE source_type = #{sourceType} AND source_id = #{sourceId}")
    int countBySource(@Param("sourceType") String sourceType, @Param("sourceId") Long sourceId);
}
