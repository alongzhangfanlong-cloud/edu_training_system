package com.education.store.module.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.module.system.entity.system.SysOperLog;
import com.education.store.module.system.mapper.SysOperLogMapper;
import com.education.store.module.system.service.ISysOperLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 操作日志 Service 实现
 */
@Service
@RequiredArgsConstructor
public class SysOperLogServiceImpl extends ServiceImpl<SysOperLogMapper, SysOperLog>
        implements ISysOperLogService {

    private final SysOperLogMapper operLogMapper;

    @Override
    public IPage<SysOperLog> selectOperLogList(IPage<SysOperLog> page, SysOperLog log) {
        LambdaQueryWrapper<SysOperLog> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(log.getTitle())) {
            wrapper.like(SysOperLog::getTitle, log.getTitle());
        }
        if (StrUtil.isNotBlank(log.getBusinessType())) {
            wrapper.eq(SysOperLog::getBusinessType, log.getBusinessType());
        }
        if (log.getStatus() != null) {
            wrapper.eq(SysOperLog::getStatus, log.getStatus());
        }
        if (StrUtil.isNotBlank(log.getOperName())) {
            wrapper.like(SysOperLog::getOperName, log.getOperName());
        }
        wrapper.orderByDesc(SysOperLog::getOperTime);
        return operLogMapper.selectPage(page, wrapper);
    }

    @Override
    public int cleanOperLog() {
        return operLogMapper.cleanOperLog();
    }
}
