package com.education.store.module.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.system.entity.system.SysRole;

import java.util.List;

/**
 * 角色 Service
 */
public interface SysRoleService extends IService<SysRole> {

    // ======== 系统管理 CRUD 方法 ========

    /** 分页查询角色列表 */
    IPage<SysRole> selectRoleList(IPage<SysRole> page, SysRole role);

    /** 查询所有角色 */
    List<SysRole> selectRoleAll();

    /** 根据用户ID查询角色列表 */
    List<SysRole> selectRolesByUserId(Long userId);

    /** 新增角色（含角色-菜单） */
    int insertRole(SysRole role);

    /** 更新角色（含角色-菜单） */
    int updateRole(SysRole role);

    /** 根据ID数组批量删除角色 */
    int deleteRoleByIds(Long[] ids);

    /** 更新角色状态 */
    int updateRoleStatus(SysRole role);

    /** 根据角色ID查询菜单ID列表 */
    List<Long> selectMenuIdsByRoleId(Long roleId);

    /** 校验角色编码是否唯一 */
    boolean checkRoleCodeUnique(SysRole role);

    /** 校验角色是否允许操作（不允许操作admin角色） */
    void checkRoleAllowed(SysRole role);

    /** 批量授权用户（新增角色-用户关联） */
    void insertAuthUsers(Long roleId, Long[] userIds);

    /** 批量取消授权用户（删除角色-用户关联） */
    void deleteAuthUsers(Long roleId, Long[] userIds);

    /** 查询角色分配的用户数量 */
    int countUserByRoleId(Long roleId);

    /** 查询角色已分配的用户ID列表 */
    List<Long> selectUserIdsByRoleId(Long roleId);

    /** 全量替换角色下的用户 */
    void authUserAllot(Long roleId, Long[] userIds);
}
