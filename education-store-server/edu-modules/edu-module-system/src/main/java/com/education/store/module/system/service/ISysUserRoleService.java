package com.education.store.module.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.system.entity.system.SysUserRole;

import java.util.List;

/**
 * 用户角色关联 Service 接口
 */
public interface ISysUserRoleService extends IService<SysUserRole> {

    /** 批量插入用户角色关联 */
    void insertUserRoles(Long userId, List<Long> roleIds);

    /** 根据用户ID删除所有角色关联 */
    void deleteByUserId(Long userId);

    /** 根据用户ID查询角色ID列表 */
    List<Long> selectRoleIdsByUserId(Long userId);
}
