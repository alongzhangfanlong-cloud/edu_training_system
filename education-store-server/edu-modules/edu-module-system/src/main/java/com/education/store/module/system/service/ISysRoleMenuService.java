package com.education.store.module.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.system.entity.system.SysRoleMenu;

import java.util.List;

/**
 * 角色菜单关联 Service 接口
 */
public interface ISysRoleMenuService extends IService<SysRoleMenu> {

    /** 批量插入角色菜单关联 */
    void insertRoleMenus(Long roleId, List<Long> menuIds);

    /** 根据角色ID删除所有菜单关联 */
    void deleteByRoleId(Long roleId);

    /** 根据角色ID查询菜单ID列表 */
    List<Long> selectMenuIdsByRoleId(Long roleId);
}
