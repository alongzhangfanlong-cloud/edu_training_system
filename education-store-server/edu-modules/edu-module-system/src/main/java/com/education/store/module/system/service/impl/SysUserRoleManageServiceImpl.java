package com.education.store.module.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.module.system.entity.system.SysUserRole;
import com.education.store.module.system.mapper.SysUserRoleMapper;
import com.education.store.module.system.service.ISysUserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户角色关联 Service 实现
 */
@Service
@RequiredArgsConstructor
public class SysUserRoleManageServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole>
        implements ISysUserRoleService {

    private final SysUserRoleMapper userRoleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertUserRoles(Long userId, List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return;
        }
        for (Long roleId : roleIds) {
            SysUserRole ur = new SysUserRole();
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            userRoleMapper.insert(ur);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByUserId(Long userId) {
        userRoleMapper.deleteByUserId(userId);
    }

    @Override
    public List<Long> selectRoleIdsByUserId(Long userId) {
        return userRoleMapper.selectRoleIdsByUserId(userId);
    }
}
