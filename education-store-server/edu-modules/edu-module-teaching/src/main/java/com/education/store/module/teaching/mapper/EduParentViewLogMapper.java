package com.education.store.module.teaching.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.store.module.teaching.entity.EduParentViewLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface EduParentViewLogMapper extends BaseMapper<EduParentViewLog> {

    @Select("SELECT * FROM edu_parent_view_log WHERE parent_user_id = #{parentUserId} AND student_id = #{studentId} AND biz_type = #{bizType} AND biz_id = #{bizId}")
    EduParentViewLog selectByUnique(@Param("parentUserId") Long parentUserId, @Param("studentId") Long studentId,
                                     @Param("bizType") String bizType, @Param("bizId") Long bizId);

    @Update("UPDATE edu_parent_view_log SET last_view_time = NOW(), view_count = view_count + 1 WHERE id = #{id}")
    int incrementView(@Param("id") Long id);

    @Select("SELECT COUNT(*) FROM edu_parent_view_log WHERE biz_type = #{bizType} AND biz_id = #{bizId}")
    int countByBiz(@Param("bizType") String bizType, @Param("bizId") Long bizId);
}
