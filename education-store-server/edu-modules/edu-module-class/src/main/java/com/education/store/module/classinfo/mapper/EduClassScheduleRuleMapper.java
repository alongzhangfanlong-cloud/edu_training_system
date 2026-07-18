package com.education.store.module.classinfo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.store.module.classinfo.entity.EduClassScheduleRule;
import org.apache.ibatis.annotations.Mapper;

/**
 * 班级排课规则 Mapper（1:1 班级关联）
 */
@Mapper
public interface EduClassScheduleRuleMapper extends BaseMapper<EduClassScheduleRule> {
}
