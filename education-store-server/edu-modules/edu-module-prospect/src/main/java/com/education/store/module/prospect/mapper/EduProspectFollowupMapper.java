package com.education.store.module.prospect.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.store.module.prospect.entity.EduProspectFollowup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

/**
 * 跟进记录 Mapper
 */
@Mapper
public interface EduProspectFollowupMapper extends BaseMapper<EduProspectFollowup> {

    /** 查询某意向学员最近跟进日期（排除指定记录，用于删除时回退） */
    @Select("SELECT MAX(followup_date) FROM edu_prospect_followup WHERE prospect_id = #{prospectId} AND id != #{excludeId} AND deleted = 0")
    LocalDateTime selectLatestFollowupDate(@Param("prospectId") Long prospectId, @Param("excludeId") Long excludeId);
}
