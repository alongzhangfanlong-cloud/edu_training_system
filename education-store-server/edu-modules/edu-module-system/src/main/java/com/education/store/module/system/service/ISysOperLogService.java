package com.education.store.module.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.system.entity.system.SysOperLog;

/**
 * 操作日志 Service 接口
 */
public interface ISysOperLogService extends IService<SysOperLog> {

    /** 分页查询操作日志列表 */
    IPage<SysOperLog> selectOperLogList(IPage<SysOperLog> page, SysOperLog log);

    /** 清空操作日志 */
    int cleanOperLog();
}
