package com.education.store.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.store.module.system.entity.system.SysConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 系统参数配置 Mapper
 */
@Mapper
public interface SysConfigMapper extends BaseMapper<SysConfig> {

    /** 根据参数键名查询配置值 */
    @Select("SELECT * FROM sys_config WHERE `key` = #{key}")
    SysConfig selectConfigByKey(@Param("key") String key);
}
