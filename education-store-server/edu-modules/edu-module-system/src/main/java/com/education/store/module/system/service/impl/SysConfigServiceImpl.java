package com.education.store.module.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.exception.BusinessException;
import com.education.store.module.system.entity.system.SysConfig;
import com.education.store.module.system.mapper.SysConfigMapper;
import com.education.store.module.system.service.ISysConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

/**
 * 系统参数配置 Service 实现
 */
@Service
@RequiredArgsConstructor
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig>
        implements ISysConfigService {

    private final SysConfigMapper configMapper;

    @Override
    public IPage<SysConfig> selectConfigList(IPage<SysConfig> page, SysConfig config) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(config.getName())) {
            wrapper.like(SysConfig::getName, config.getName());
        }
        if (StrUtil.isNotBlank(config.getKey())) {
            wrapper.like(SysConfig::getKey, config.getKey());
        }
        if (StrUtil.isNotBlank(config.getType())) {
            wrapper.eq(SysConfig::getType, config.getType());
        }
        wrapper.orderByAsc(SysConfig::getCreateTime);
        return configMapper.selectPage(page, wrapper);
    }

    @Override
    public SysConfig selectConfigById(Long id) {
        return configMapper.selectById(id);
    }

    @Override
    public String selectConfigByKey(String key) {
        SysConfig config = configMapper.selectConfigByKey(key);
        return config != null ? config.getValue() : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConfig(SysConfig config) {
        return configMapper.insert(config);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConfig(SysConfig config) {
        SysConfig exist = configMapper.selectById(config.getId());
        if (exist == null) {
            throw new BusinessException("参数配置不存在");
        }
        return configMapper.updateById(config);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConfigByIds(Long[] ids) {
        return configMapper.deleteBatchIds(Arrays.asList(ids));
    }

    @Override
    public boolean checkConfigKeyUnique(SysConfig config) {
        Long id = config.getId() != null ? config.getId() : -1L;
        SysConfig exist = lambdaQuery()
                .eq(SysConfig::getKey, config.getKey())
                .one();
        return exist == null || exist.getId().equals(id);
    }

    @Override
    public void resetConfigCache() {
        // 清除配置缓存（后续可对接Redis缓存）
        // 当前实现留空，需要时可扩展
    }
}
