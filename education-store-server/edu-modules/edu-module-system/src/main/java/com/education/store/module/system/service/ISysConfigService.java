package com.education.store.module.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.system.entity.system.SysConfig;

/**
 * 系统参数配置 Service 接口
 */
public interface ISysConfigService extends IService<SysConfig> {

    /** 分页查询参数配置列表 */
    IPage<SysConfig> selectConfigList(IPage<SysConfig> page, SysConfig config);

    /** 根据ID查询参数配置 */
    SysConfig selectConfigById(Long id);

    /** 根据参数键名查询参数值 */
    String selectConfigByKey(String key);

    /** 新增参数配置 */
    int insertConfig(SysConfig config);

    /** 更新参数配置 */
    int updateConfig(SysConfig config);

    /** 根据ID数组删除参数配置 */
    int deleteConfigByIds(Long[] ids);

    /** 校验参数键名是否唯一 */
    boolean checkConfigKeyUnique(SysConfig config);

    /** 重置参数缓存 */
    void resetConfigCache();
}
