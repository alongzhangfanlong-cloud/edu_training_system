package com.education.store.module.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.system.entity.system.SysDictType;

import java.util.List;

/**
 * 字典类型 Service 接口
 */
public interface ISysDictTypeService extends IService<SysDictType> {

    /** 分页查询字典类型列表 */
    IPage<SysDictType> selectDictTypeList(IPage<SysDictType> page, SysDictType dictType);

    /** 查询所有字典类型 */
    List<SysDictType> selectDictTypeAll();

    /** 新增字典类型 */
    int insertDictType(SysDictType type);

    /** 更新字典类型 */
    int updateDictType(SysDictType type);

    /** 根据ID数组删除字典类型 */
    int deleteDictTypeByIds(Long[] ids);
}
