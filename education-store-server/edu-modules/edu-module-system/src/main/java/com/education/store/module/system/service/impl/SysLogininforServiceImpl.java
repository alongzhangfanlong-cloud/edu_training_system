package com.education.store.module.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.module.system.entity.system.SysLogininfor;
import com.education.store.module.system.mapper.SysLogininforMapper;
import com.education.store.module.system.service.ISysLogininforService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 系统登录日志 Service 实现
 */
@Service
@RequiredArgsConstructor
public class SysLogininforServiceImpl extends ServiceImpl<SysLogininforMapper, SysLogininfor>
        implements ISysLogininforService {

    private final SysLogininforMapper logininforMapper;

    @Override
    public IPage<SysLogininfor> selectLogininforList(IPage<SysLogininfor> page, SysLogininfor info) {
        LambdaQueryWrapper<SysLogininfor> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(info.getUsername())) {
            wrapper.like(SysLogininfor::getUsername, info.getUsername());
        }
        if (StrUtil.isNotBlank(info.getIpaddr())) {
            wrapper.eq(SysLogininfor::getIpaddr, info.getIpaddr());
        }
        if (info.getStatus() != null) {
            wrapper.eq(SysLogininfor::getStatus, info.getStatus());
        }
        wrapper.orderByDesc(SysLogininfor::getLoginTime);
        return logininforMapper.selectPage(page, wrapper);
    }

    @Override
    public int cleanLogininfor() {
        return logininforMapper.cleanLogininfor();
    }

    @Override
    public void insertLogininfor(SysLogininfor info) {
        logininforMapper.insert(info);
    }
}
