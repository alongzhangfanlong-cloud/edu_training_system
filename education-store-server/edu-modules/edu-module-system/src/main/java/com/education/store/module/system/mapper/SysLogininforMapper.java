package com.education.store.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.store.module.system.entity.system.SysLogininfor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统登录日志 Mapper
 */
@Mapper
public interface SysLogininforMapper extends BaseMapper<SysLogininfor> {

    /** 清空登录日志 */
    @Delete("TRUNCATE TABLE sys_login_log")
    int cleanLogininfor();
}
