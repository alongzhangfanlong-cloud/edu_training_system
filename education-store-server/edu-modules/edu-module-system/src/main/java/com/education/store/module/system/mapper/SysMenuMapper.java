package com.education.store.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.store.module.system.entity.system.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

/**
 * 菜单 Mapper
 */
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * 查询用户的权限标识列表。
     * 注意：权限是全局的（基于角色）。
     */
    @Select("SELECT DISTINCT m.permission FROM sys_menu m " +
            "INNER JOIN sys_role_menu rm ON m.id = rm.menu_id " +
            "INNER JOIN sys_user_role ur ON rm.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND m.permission IS NOT NULL " +
            "AND m.permission != '' AND m.deleted = 0")
    Set<String> selectMenuPermsByUserId(@Param("userId") Long userId);

    /** 查询角色的权限标识列表 */
    @Select("SELECT DISTINCT m.permission FROM sys_menu m " +
            "INNER JOIN sys_role_menu rm ON m.id = rm.menu_id " +
            "WHERE rm.role_id = #{roleId} AND m.permission IS NOT NULL " +
            "AND m.permission != '' AND m.deleted = 0")
    Set<String> selectMenuPermsByRoleId(@Param("roleId") Long roleId);

    /**
     * 查询用户的可见菜单列表（仅 M/C 类型）。
     */
    @Select("SELECT DISTINCT m.* FROM sys_menu m " +
            "INNER JOIN sys_role_menu rm ON m.id = rm.menu_id " +
            "INNER JOIN sys_user_role ur ON rm.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND m.type IN ('M', 'C') AND m.visible = 1 " +
            "AND m.deleted = 0 " +
            "ORDER BY m.sort ASC")
    List<SysMenu> selectMenusByUserId(@Param("userId") Long userId);

    /** 查询角色的菜单ID列表 */
    @Select("SELECT menu_id FROM sys_role_menu WHERE role_id = #{roleId}")
    List<Long> selectMenuIdsByRoleId(@Param("roleId") Long roleId);
}
