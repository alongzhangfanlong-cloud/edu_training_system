package com.education.store.module.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.exception.BusinessException;
import com.education.store.module.system.entity.system.SysRole;
import com.education.store.module.system.entity.system.SysRoleMenu;
import com.education.store.module.system.mapper.SysMenuMapper;
import com.education.store.module.system.mapper.SysRoleMapper;
import com.education.store.module.system.mapper.SysRoleMenuMapper;
import com.education.store.module.system.mapper.SysUserRoleMapper;
import com.education.store.module.system.entity.system.SysUserRole;
import com.education.store.module.system.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * 角色 Service 实现
 */
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole>
        implements SysRoleService {

    private final SysRoleMapper roleMapper;
    private final SysRoleMenuMapper roleMenuMapper;
    private final SysMenuMapper menuMapper;
    private final SysUserRoleMapper userRoleMapper;

    // ======== 系统管理 CRUD 方法 ========

    @Override
    public IPage<SysRole> selectRoleList(
            IPage<SysRole> page,
            SysRole role) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        // 关键字搜索：使用 OR 连接 name 和 code（修复 AND 逻辑导致搜索失效的问题）
        if (StrUtil.isNotBlank(role.getName())) {
            wrapper.and(w -> w
                .like(SysRole::getName, role.getName())
                .or()
                .like(SysRole::getCode, role.getName()));
        }
        if (role.getStatus() != null) {
            wrapper.eq(SysRole::getStatus, role.getStatus());
        }
        // 创建时间范围筛选
        if (role.getBeginTime() != null) {
            wrapper.ge(SysRole::getCreateTime, role.getBeginTime());
        }
        if (role.getEndTime() != null) {
            wrapper.le(SysRole::getCreateTime, role.getEndTime());
        }
        wrapper.orderByAsc(SysRole::getCreateTime);
        return roleMapper.selectPage(page, wrapper);
    }

    @Override
    public List<SysRole> selectRoleAll() {
        return roleMapper.selectList(new LambdaQueryWrapper<SysRole>()
                .orderByAsc(SysRole::getCreateTime));
    }

    @Override
    public List<SysRole> selectRolesByUserId(Long userId) {
        return roleMapper.selectRolesByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRole(SysRole role) {
        int rows = roleMapper.insert(role);

        // 批量构建菜单关联，再逐条插入
        if (role.getMenuIds() != null && !role.getMenuIds().isEmpty()) {
            List<SysRoleMenu> menuList = role.getMenuIds().stream().map(menuId -> {
                SysRoleMenu rm = new SysRoleMenu();
                rm.setRoleId(role.getId());
                rm.setMenuId(menuId);
                return rm;
            }).toList();
            for (SysRoleMenu rm : menuList) {
                roleMenuMapper.insert(rm);
            }
        }

        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateRole(SysRole role) {
        int rows = roleMapper.updateById(role);

        roleMenuMapper.deleteByRoleId(role.getId());

        if (role.getMenuIds() != null && !role.getMenuIds().isEmpty()) {
            List<SysRoleMenu> menuList = role.getMenuIds().stream().map(menuId -> {
                SysRoleMenu rm = new SysRoleMenu();
                rm.setRoleId(role.getId());
                rm.setMenuId(menuId);
                return rm;
            }).toList();
            for (SysRoleMenu rm : menuList) {
                roleMenuMapper.insert(rm);
            }
        }

        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRoleByIds(Long[] ids) {
        // 批量查询角色，校验 admin 保护和关联用户（在事务内原子完成，避免 TOCTOU 竞态）
        List<SysRole> roles = roleMapper.selectBatchIds(Arrays.asList(ids));
        for (SysRole role : roles) {
            checkRoleAllowed(role);
            int userCount = countUserByRoleId(role.getId());
            if (userCount > 0) {
                throw new BusinessException("角色【" + role.getName() + "】下存在" + userCount + "个关联用户，无法删除");
            }
        }

        // 清除关联数据后删除角色
        for (Long id : ids) {
            roleMenuMapper.deleteByRoleId(id);
        }
        return roleMapper.deleteBatchIds(Arrays.asList(ids));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateRoleStatus(SysRole role) {
        return roleMapper.updateById(role);
    }

    @Override
    public List<Long> selectMenuIdsByRoleId(Long roleId) {
        return menuMapper.selectMenuIdsByRoleId(roleId);
    }

    @Override
    public boolean checkRoleCodeUnique(SysRole role) {
        Long id = role.getId() != null ? role.getId() : -1L;
        SysRole exist = lambdaQuery()
                .eq(SysRole::getCode, role.getCode())
                .one();
        return exist == null || exist.getId().equals(id);
    }

    @Override
    public void checkRoleAllowed(SysRole role) {
        if (role != null && "admin".equals(role.getCode())) {
            throw new BusinessException("不允许操作超级管理员角色");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertAuthUsers(Long roleId, Long[] userIds) {
        if (userIds != null && userIds.length > 0) {
            for (Long userId : userIds) {
                SysUserRole ur = new SysUserRole();
                ur.setRoleId(roleId);
                ur.setUserId(userId);
                userRoleMapper.insert(ur);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAuthUsers(Long roleId, Long[] userIds) {
        if (userIds != null && userIds.length > 0) {
            LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysUserRole::getRoleId, roleId)
                    .in(SysUserRole::getUserId, Arrays.asList(userIds));
            userRoleMapper.delete(wrapper);
        }
    }

    @Override
    public int countUserByRoleId(Long roleId) {
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getRoleId, roleId);
        return userRoleMapper.selectCount(wrapper).intValue();
    }

    @Override
    public List<Long> selectUserIdsByRoleId(Long roleId) {
        return userRoleMapper.selectUserIdsByRoleId(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void authUserAllot(Long roleId, Long[] userIds) {
        // 清空该角色下所有用户关联
        userRoleMapper.deleteByRoleId(roleId);
        // 插入新的用户关联
        if (userIds != null && userIds.length > 0) {
            for (Long userId : userIds) {
                SysUserRole ur = new SysUserRole();
                ur.setRoleId(roleId);
                ur.setUserId(userId);
                userRoleMapper.insert(ur);
            }
        }
    }
}
