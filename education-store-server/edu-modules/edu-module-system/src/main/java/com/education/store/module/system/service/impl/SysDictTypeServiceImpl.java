package com.education.store.module.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.exception.BusinessException;
import com.education.store.module.system.entity.system.SysDictData;
import com.education.store.module.system.entity.system.SysDictType;
import com.education.store.module.system.mapper.SysDictDataMapper;
import com.education.store.module.system.mapper.SysDictTypeMapper;
import com.education.store.module.system.service.ISysDictTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * 字典类型 Service 实现
 */
@Service
@RequiredArgsConstructor
public class SysDictTypeServiceImpl extends ServiceImpl<SysDictTypeMapper, SysDictType>
        implements ISysDictTypeService {

    private final SysDictTypeMapper dictTypeMapper;
    private final SysDictDataMapper dictDataMapper;

    @Override
    public IPage<SysDictType> selectDictTypeList(IPage<SysDictType> page, SysDictType dictType) {
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(dictType.getName())) {
            wrapper.and(w -> w
                    .like(SysDictType::getName, dictType.getName())
                    .or()
                    .like(SysDictType::getCode, dictType.getName()));
        }
        if (StrUtil.isNotBlank(dictType.getCode())) {
            wrapper.like(SysDictType::getCode, dictType.getCode());
        }
        if (dictType.getStatus() != null) {
            wrapper.eq(SysDictType::getStatus, dictType.getStatus());
        }
        wrapper.orderByAsc(SysDictType::getCreateTime);
        return dictTypeMapper.selectPage(page, wrapper);
    }

    @Override
    public List<SysDictType> selectDictTypeAll() {
        return lambdaQuery()
                .orderByAsc(SysDictType::getCreateTime)
                .list();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertDictType(SysDictType type) {
        long count = lambdaQuery().eq(SysDictType::getCode, type.getCode()).count();
        if (count > 0) {
            throw new BusinessException("字典编码 '" + type.getCode() + "' 已存在");
        }
        return dictTypeMapper.insert(type);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDictType(SysDictType type) {
        SysDictType exist = dictTypeMapper.selectById(type.getId());
        if (exist == null) {
            throw new BusinessException("字典类型不存在");
        }
        return dictTypeMapper.updateById(type);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDictTypeByIds(Long[] ids) {
        // 删除关联的字典数据
        for (Long id : ids) {
            SysDictType type = dictTypeMapper.selectById(id);
            if (type != null) {
                LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(SysDictData::getTypeCode, type.getCode());
                dictDataMapper.delete(wrapper);
            }
        }
        return dictTypeMapper.deleteBatchIds(Arrays.asList(ids));
    }
}
