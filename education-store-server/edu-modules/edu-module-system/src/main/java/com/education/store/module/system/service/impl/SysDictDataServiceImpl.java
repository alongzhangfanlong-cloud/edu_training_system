package com.education.store.module.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.exception.BusinessException;
import com.education.store.module.system.entity.system.SysDictData;
import com.education.store.module.system.mapper.SysDictDataMapper;
import com.education.store.module.system.service.ISysDictDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * 字典数据 Service 实现
 */
@Service
@RequiredArgsConstructor
public class SysDictDataServiceImpl extends ServiceImpl<SysDictDataMapper, SysDictData>
        implements ISysDictDataService {

    private final SysDictDataMapper dictDataMapper;

    @Override
    public IPage<SysDictData> selectDictDataList(IPage<SysDictData> page, SysDictData data) {
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(data.getTypeCode())) {
            wrapper.eq(SysDictData::getTypeCode, data.getTypeCode());
        }
        if (StrUtil.isNotBlank(data.getLabel())) {
            wrapper.like(SysDictData::getLabel, data.getLabel());
        }
        if (data.getStatus() != null) {
            wrapper.eq(SysDictData::getStatus, data.getStatus());
        }
        wrapper.orderByAsc(SysDictData::getSort);
        return dictDataMapper.selectPage(page, wrapper);
    }

    @Override
    public List<SysDictData> selectDictDataByType(String typeCode) {
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictData::getTypeCode, typeCode)
                .eq(SysDictData::getStatus, 1)
                .orderByAsc(SysDictData::getSort);
        return dictDataMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertDictData(SysDictData data) {
        return dictDataMapper.insert(data);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDictData(SysDictData data) {
        SysDictData exist = dictDataMapper.selectById(data.getId());
        if (exist == null) {
            throw new BusinessException("字典数据不存在");
        }
        return dictDataMapper.updateById(data);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDictDataByIds(Long[] ids) {
        return dictDataMapper.deleteBatchIds(Arrays.asList(ids));
    }
}
