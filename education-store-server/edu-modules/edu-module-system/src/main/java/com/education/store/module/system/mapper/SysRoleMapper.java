package com.education.store.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.store.module.system.entity.system.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

/**
 * 角色 Mapper
 * <p>
 * 注意：本 Mapper 中的自定义 SQL 直接硬编码了表名（如 sys_role、sys_user_role 等）。
 * 如果表名通过 @TableName 变更，这些 SQL 需同步更新。
 * </p>
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /** 查询用户的角色列表 */
    @Select("SELECT r.* FROM sys_role r " +
            "INNER JOIN sys_user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.deleted = 0")
    List<SysRole> selectRolesByUserId(@Param("userId") Long userId);

    /** 查询用户的角色权限标识列表 */
    @Select("SELECT DISTINCT m.permission FROM sys_menu m " +
            "INNER JOIN sys_role_menu rm ON m.id = rm.menu_id " +
            "INNER JOIN sys_user_role ur ON rm.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND m.permission IS NOT NULL " +
            "AND m.permission != '' AND m.deleted = 0")
    Set<String> selectRolePermissionByUserId(@Param("userId") Long userId);
}
