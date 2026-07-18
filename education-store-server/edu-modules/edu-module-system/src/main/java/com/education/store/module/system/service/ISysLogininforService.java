package com.education.store.module.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.system.entity.system.SysLogininfor;

/**
 * 系统登录日志 Service 接口
 */
public interface ISysLogininforService extends IService<SysLogininfor> {

    /** 分页查询登录日志列表 */
    IPage<SysLogininfor> selectLogininforList(IPage<SysLogininfor> page, SysLogininfor info);

    /** 清空登录日志 */
    int cleanLogininfor();

    /** 新增登录日志 */
    void insertLogininfor(SysLogininfor info);
}
