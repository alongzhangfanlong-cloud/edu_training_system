package com.education.store.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.store.module.system.entity.system.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

/**
 * 用户 Mapper
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /** 查询用户角色编码列表 */
    @Select("SELECT DISTINCT r.code FROM sys_role r " +
            "INNER JOIN sys_user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.deleted = 0")
    List<String> selectRoleCodesByUserId(@Param("userId") Long userId);

    /** 查询用户权限标识列表 */
    @Select("SELECT DISTINCT m.permission FROM sys_menu m " +
            "INNER JOIN sys_role_menu rm ON m.id = rm.menu_id " +
            "INNER JOIN sys_user_role ur ON rm.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND m.permission IS NOT NULL " +
            "AND m.permission != '' AND m.deleted = 0")
    Set<String> selectPermissionsByUserId(@Param("userId") Long userId);

    /** 分页查询用户列表（含角色名称） */
    @Select("<script>" +
            "SELECT u.*, " +
            "GROUP_CONCAT(r.name SEPARATOR ',') AS role_names " +
            "FROM sys_user u " +
            "LEFT JOIN sys_user_role ur ON u.id = ur.user_id " +
            "LEFT JOIN sys_role r ON ur.role_id = r.id AND r.deleted = 0 " +
            "WHERE u.deleted = 0 " +
            "<if test='keyword != null and keyword != \"\"'>" +
            "AND (u.username LIKE CONCAT('%',#{keyword},'%') " +
            "OR u.real_name LIKE CONCAT('%',#{keyword},'%') " +
            "OR u.mobile LIKE CONCAT('%',#{keyword},'%')) " +
            "</if>" +
            "<if test='status != null'>AND u.status = #{status}</if>" +
            "GROUP BY u.id ORDER BY u.create_time DESC " +
            "</script>")
    Page<SysUser> selectUserPage(Page<SysUser> page,
                                  @Param("keyword") String keyword,
                                  @Param("status") Integer status);

    /** 根据ID查询用户 */
    @Select("SELECT u.* FROM sys_user u " +
            "WHERE u.id = #{userId} AND u.deleted = 0")
    SysUser selectUserById(@Param("userId") Long userId);

    /** 根据用户名查询用户 */
    @Select("SELECT * FROM sys_user WHERE username = #{username} AND deleted = 0")
    SysUser selectUserByUsername(@Param("username") String username);
}
