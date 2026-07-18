package com.education.store.module.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.common.model.TreeSelect;
import com.education.store.module.system.dto.RouterVo;
import com.education.store.module.system.entity.system.SysMenu;

import java.util.List;
import java.util.Set;

/**
 * 菜单 Service
 */
public interface SysMenuService extends IService<SysMenu> {

    /** 获取菜单树 */
    List<SysMenu> getMenuTree();

    /** 获取用户菜单（前端路由用） */
    List<SysMenu> getUserMenus(Long userId);

    // ======== 系统管理 CRUD 方法 ========

    /** 查询菜单列表 */
    List<SysMenu> selectMenuList(SysMenu menu, Long userId);

    /** 查询所有菜单树 */
    List<SysMenu> selectMenuTreeAll();

    /** 根据用户ID查询菜单树 */
    List<SysMenu> selectMenuTreeByUserId(Long userId);

    /** 构建菜单树选择器 */
    List<TreeSelect> buildMenuTreeSelect(List<SysMenu> menus);

    /** 构建前端路由 */
    List<RouterVo> buildRouters(List<SysMenu> menus);

    /** 根据菜单ID查询菜单 */
    SysMenu selectMenuById(Long id);

    /** 新增菜单 */
    int insertMenu(SysMenu menu);

    /** 更新菜单 */
    int updateMenu(SysMenu menu);

    /** 根据菜单ID删除菜单 */
    int deleteMenuById(Long id);

    /** 判断是否有子菜单 */
    boolean hasChildByMenuId(Long id);

    /** 校验菜单是否分配角色 */
    boolean checkMenuExistRole(Long id);

    /** 校验菜单名称是否唯一 */
    boolean checkMenuNameUnique(SysMenu menu);

    /** 根据用户ID查询权限标识集合 */
    Set<String> selectMenuPermsByUserId(Long userId);

    /** 根据角色ID查询菜单ID列表 */
    List<Long> selectMenuListByRoleId(Long roleId);
}
