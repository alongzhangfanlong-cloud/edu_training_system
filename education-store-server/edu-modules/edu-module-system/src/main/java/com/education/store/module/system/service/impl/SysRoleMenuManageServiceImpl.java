package com.education.store.module.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.module.system.entity.system.SysRoleMenu;
import com.education.store.module.system.mapper.SysRoleMenuMapper;
import com.education.store.module.system.service.ISysRoleMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 角色菜单关联 Service 实现
 */
@Service
@RequiredArgsConstructor
public class SysRoleMenuManageServiceImpl extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu>
        implements ISysRoleMenuService {

    private final SysRoleMenuMapper roleMenuMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertRoleMenus(Long roleId, List<Long> menuIds) {
        if (menuIds == null || menuIds.isEmpty()) {
            return;
        }
        for (Long menuId : menuIds) {
            SysRoleMenu rm = new SysRoleMenu();
            rm.setRoleId(roleId);
            rm.setMenuId(menuId);
            roleMenuMapper.insert(rm);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByRoleId(Long roleId) {
        roleMenuMapper.deleteByRoleId(roleId);
    }

    @Override
    public List<Long> selectMenuIdsByRoleId(Long roleId) {
        return roleMenuMapper.selectMenuIdsByRoleId(roleId);
    }
}
