package com.education.store.module.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.system.entity.system.SysDictData;

import java.util.List;

/**
 * 字典数据 Service 接口
 */
public interface ISysDictDataService extends IService<SysDictData> {

    /** 分页查询字典数据列表 */
    IPage<SysDictData> selectDictDataList(IPage<SysDictData> page, SysDictData data);

    /** 根据字典类型编码查询字典数据 */
    List<SysDictData> selectDictDataByType(String typeCode);

    /** 新增字典数据 */
    int insertDictData(SysDictData data);

    /** 更新字典数据 */
    int updateDictData(SysDictData data);

    /** 根据ID数组删除字典数据 */
    int deleteDictDataByIds(Long[] ids);
}
