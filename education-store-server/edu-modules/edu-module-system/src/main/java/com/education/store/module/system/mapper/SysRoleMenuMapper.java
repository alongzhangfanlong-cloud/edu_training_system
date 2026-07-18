package com.education.store.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.store.module.system.entity.system.SysRoleMenu;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色菜单关联 Mapper
 */
@Mapper
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {

    /** 根据角色ID删除所有菜单关联 */
    @Delete("DELETE FROM sys_role_menu WHERE role_id = #{roleId}")
    int deleteByRoleId(@Param("roleId") Long roleId);

    /** 根据角色ID查询菜单ID列表 */
    @Select("SELECT menu_id FROM sys_role_menu WHERE role_id = #{roleId}")
    List<Long> selectMenuIdsByRoleId(@Param("roleId") Long roleId);

    /** 检查菜单是否存在角色关联 */
    @Select("SELECT COUNT(*) FROM sys_role_menu WHERE menu_id = #{menuId}")
    int checkMenuExistRole(@Param("menuId") Long menuId);
}
