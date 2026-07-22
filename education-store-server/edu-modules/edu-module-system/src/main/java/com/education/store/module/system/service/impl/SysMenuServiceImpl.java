package com.education.store.module.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.exception.BusinessException;
import com.education.store.common.model.TreeSelect;
import com.education.store.module.system.dto.MetaVo;
import com.education.store.module.system.dto.RouterVo;
import com.education.store.module.system.entity.system.SysMenu;
import com.education.store.module.system.entity.system.SysRoleMenu;
import com.education.store.module.system.mapper.SysMenuMapper;
import com.education.store.module.system.mapper.SysRoleMenuMapper;
import com.education.store.module.system.service.SysMenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 菜单 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu>
        implements SysMenuService {

    private final SysMenuMapper menuMapper;
    private final SysRoleMenuMapper roleMenuMapper;

    // ======== 原有方法 ========

    @Override
    public List<SysMenu> getMenuTree() {
        return lambdaQuery()
                .orderByAsc(SysMenu::getSort).list();
    }

    @Override
    public List<SysMenu> getUserMenus(Long userId) {
        return baseMapper.selectMenusByUserId(userId);
    }


    // ======== 系统管理 CRUD 方法 ========

    @Override
    public List<SysMenu> selectMenuList(
            SysMenu menu, Long userId) {
        List<SysMenu> list;
        if (userId != null) {
            list = menuMapper.selectMenusByUserId(userId);
        } else {
            list = menuMapper.selectList(new LambdaQueryWrapper<SysMenu>()
                    .like(StrUtil.isNotBlank(menu.getName()), SysMenu::getName, menu.getName())
                    .eq(menu.getStatus() != null, SysMenu::getStatus, menu.getStatus())
                    .orderByAsc(SysMenu::getSort));
        }
        return list;
    }

    @Override
    public List<SysMenu> selectMenuTreeAll() {
        List<SysMenu> menus = menuMapper.selectList(new LambdaQueryWrapper<SysMenu>()
                .orderByAsc(SysMenu::getSort));
        return buildMenuTree(menus);
    }

    @Override
    public List<SysMenu> selectMenuTreeByUserId(Long userId) {
        List<SysMenu> menus = menuMapper.selectMenusByUserId(userId);
        return buildMenuTree(menus);
    }

    @Override
    public List<TreeSelect> buildMenuTreeSelect(List<SysMenu> menus) {
        List<SysMenu> menuTree = buildMenuTree(menus);
        return menuTree.stream().map(this::convertMenuToTreeSelect).collect(Collectors.toList());
    }

    private List<SysMenu> buildMenuTree(
            List<SysMenu> menus) {
        List<SysMenu> returnList = new ArrayList<>();
        List<Long> tempList = menus.stream()
                .map(SysMenu::getId)
                .collect(Collectors.toList());
        for (SysMenu menu : menus) {
            if (!tempList.contains(menu.getParentId())) {
                recursionFn(menus, menu);
                returnList.add(menu);
            }
        }
        if (returnList.isEmpty()) {
            log.warn("菜单树构建异常：未找到根节点，返回扁平列表。菜单总数: {}", menus.size());
            returnList = menus;
        }
        return returnList;
    }

    private void recursionFn(List<SysMenu> list,
                              SysMenu t) {
        List<SysMenu> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysMenu child : childList) {
            if (hasChild(list, child)) {
                recursionFn(list, child);
            }
        }
    }

    private List<SysMenu> getChildList(
            List<SysMenu> list,
            SysMenu t) {
        return list.stream()
                .filter(m -> m.getParentId() != null && m.getParentId().equals(t.getId()))
                .collect(Collectors.toList());
    }

    private boolean hasChild(List<SysMenu> list,
                             SysMenu t) {
        return list.stream().anyMatch(m -> m.getParentId() != null && m.getParentId().equals(t.getId()));
    }

    private TreeSelect convertMenuToTreeSelect(SysMenu menu) {
        TreeSelect node = new TreeSelect();
        node.setId(menu.getId());
        node.setLabel(menu.getName());
        node.setParentId(menu.getParentId());
        node.setWeight(menu.getSort());
        if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
            node.setChildren(menu.getChildren().stream()
                    .map(this::convertMenuToTreeSelect)
                    .collect(Collectors.toList()));
        }
        return node;
    }

    @Override
    public List<RouterVo> buildRouters(List<SysMenu> menus) {
        List<RouterVo> routers = new LinkedList<>();
        for (SysMenu menu : menus) {
            RouterVo router = new RouterVo();
            router.setHidden(isHidden(menu));
            router.setName(getRouteName(menu));
            router.setPath(getRouterPath(menu));
            router.setComponent(getComponent(menu));
            router.setQuery(menu.getQuery());
            router.setMeta(new MetaVo(menu.getName(), menu.getIcon(),
                    isCached(menu), menu.getPath()));
            List<SysMenu> cMenus = menu.getChildren();
            if (cMenus != null && !cMenus.isEmpty() && "M".equals(menu.getType())) {
                router.setAlwaysShow(true);
                router.setRedirect("noRedirect");
                router.setChildren(buildRouters(cMenus));
            } else if (isMenuFrame(menu)) {
                router.setMeta(null);
                List<RouterVo> childrenList = new ArrayList<>();
                RouterVo children = new RouterVo();
                children.setPath(menu.getPath());
                children.setComponent(menu.getComponent());
                children.setName(getRouteName(menu) + "Index");
                children.setMeta(new MetaVo(menu.getName(), menu.getIcon(),
                        isCached(menu), menu.getPath()));
                children.setQuery(menu.getQuery());
                childrenList.add(children);
                router.setChildren(childrenList);
            }
            routers.add(router);
        }
        return routers;
    }

    /** visible=0 时为隐藏，默认 visible=1 可见 */
    private boolean isHidden(SysMenu menu) {
        return menu.getVisible() != null && menu.getVisible() == 0;
    }

    /** isCache=1 时缓存，默认不缓存 */
    private boolean isCached(SysMenu menu) {
        return menu.getIsCache() != null && menu.getIsCache() == 1;
    }

    private String getRouteName(SysMenu menu) {
        return StrUtil.isNotBlank(menu.getRouteName()) ? menu.getRouteName() : menu.getPath();
    }

    private String getRouterPath(SysMenu menu) {
        String routerPath = menu.getPath();
        if (menu.getParentId() != null && menu.getParentId() != 0 && isInnerLink(menu)) {
            routerPath = innerLinkReplaceEach(routerPath);
        }
        if (menu.getParentId() != null && menu.getParentId() == 0 && "M".equals(menu.getType())) {
            routerPath = menu.getPath();
        } else if (isMenuFrame(menu)) {
            routerPath = "/";
        }
        return routerPath;
    }

    private String getComponent(SysMenu menu) {
        String component = "Layout";
        if (StrUtil.isNotBlank(menu.getComponent()) && !isMenuFrame(menu)) {
            component = menu.getComponent();
        } else if (StrUtil.isBlank(menu.getComponent()) && menu.getParentId() != null && menu.getParentId() != 0 && isInnerLink(menu)) {
            component = "InnerLink";
        } else if (StrUtil.isBlank(menu.getComponent()) && isParentView(menu)) {
            component = "ParentView";
        }
        return component;
    }

    private boolean isMenuFrame(SysMenu menu) {
        return menu.getParentId() != null && menu.getParentId() == 0
                && "C".equals(menu.getType())
                && menu.getIsFrame() != null && menu.getIsFrame() == 1;
    }

    private boolean isInnerLink(SysMenu menu) {
        return menu.getIsFrame() != null && menu.getIsFrame() == 1
                && StrUtil.isNotBlank(menu.getPath())
                && (menu.getPath().startsWith("http://") || menu.getPath().startsWith("https://"));
    }

    private boolean isParentView(SysMenu menu) {
        return menu.getParentId() != null && menu.getParentId() != 0
                && "C".equals(menu.getType());
    }

    private String innerLinkReplaceEach(String path) {
        return StrUtil.replace(path, "http://", "")
                .replace("https://", "");
    }

    @Override
    public SysMenu selectMenuById(Long id) {
        return menuMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertMenu(SysMenu menu) {
        return menuMapper.insert(menu);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateMenu(SysMenu menu) {
        SysMenu exist = menuMapper.selectById(menu.getId());
        if (exist == null) {
            throw new BusinessException("菜单不存在");
        }

        // 校验父菜单存在性
        if (menu.getParentId() != null && menu.getParentId() != 0) {
            SysMenu parent = menuMapper.selectById(menu.getParentId());
            if (parent == null) {
                throw new BusinessException("父菜单不存在");
            }
            // 防止循环引用：父节点不能是当前菜单的子孙节点
            List<Long> descendantIds = new ArrayList<>();
            collectDescendantIds(menu.getId(), descendantIds, new HashSet<>());
            if (descendantIds.contains(menu.getParentId())) {
                throw new BusinessException("父节点不能设置为当前菜单的子孙节点");
            }
        }

        return menuMapper.updateById(menu);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteMenuById(Long id) {
        // 校验存在性
        SysMenu exist = menuMapper.selectById(id);
        if (exist == null) {
            throw new BusinessException("菜单不存在");
        }

        // 递归收集所有子孙节点ID（含自身），带循环引用检测
        List<Long> allIds = new ArrayList<>();
        collectDescendantIds(id, allIds, new HashSet<>());

        if (allIds.isEmpty()) {
            return 0;
        }

        // 批量清理角色-菜单关联
        roleMenuMapper.delete(
                new LambdaQueryWrapper<SysRoleMenu>().in(SysRoleMenu::getMenuId, allIds));

        // 物理批量删除
        return menuMapper.deleteBatchIds(allIds);
    }

    /** 一次性加载所有菜单，在内存中递归收集指定菜单的所有子孙节点ID（含循环引用保护） */
    private void collectDescendantIds(Long parentId, List<Long> result, Set<Long> visited) {
        if (visited.contains(parentId)) {
            return;
        }
        visited.add(parentId);
        result.add(parentId);

        List<SysMenu> allMenus = lambdaQuery().list();
        Map<Long, List<SysMenu>> parentMap = allMenus.stream()
                .collect(Collectors.groupingBy(m -> m.getParentId() != null ? m.getParentId() : 0L));

        collectDescendantIdsFromMap(parentId, result, visited, parentMap);
    }

    private void collectDescendantIdsFromMap(Long parentId, List<Long> result,
                                              Set<Long> visited, Map<Long, List<SysMenu>> parentMap) {
        // visited 和 result 已在 collectDescendantIds 中添加了 parentId
        List<SysMenu> children = parentMap.getOrDefault(parentId, Collections.emptyList());
        for (SysMenu child : children) {
            if (!visited.contains(child.getId())) {
                visited.add(child.getId());
                result.add(child.getId());
                collectDescendantIdsFromMap(child.getId(), result, visited, parentMap);
            }
        }
    }

    @Override
    public boolean hasChildByMenuId(Long id) {
        return lambdaQuery().eq(SysMenu::getParentId, id).count() > 0;
    }

    @Override
    public boolean checkMenuExistRole(Long id) {
        return roleMenuMapper.selectCount(
                new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getMenuId, id)) > 0;
    }

    @Override
    public boolean checkMenuNameUnique(SysMenu menu) {
        Long id = menu.getId() != null ? menu.getId() : -1L;
        return lambdaQuery()
                .eq(SysMenu::getName, menu.getName())
                .eq(SysMenu::getParentId,
                        menu.getParentId() != null ? menu.getParentId() : 0L)
                .ne(SysMenu::getId, id)
                .count() == 0;
    }

    @Override
    public Set<String> selectMenuPermsByUserId(Long userId) {
        // 权限是全局的（基于角色），不按公司过滤
        Set<String> rawPerms = menuMapper.selectMenuPermsByUserId(userId);
        Set<String> perms = new HashSet<>();
        for (String perm : rawPerms) {
            if (StrUtil.isNotBlank(perm)) {
                perms.addAll(Arrays.asList(perm.trim().split(",")));
            }
        }
        return perms;
    }

    @Override
    public List<Long> selectMenuListByRoleId(Long roleId) {
        return menuMapper.selectMenuIdsByRoleId(roleId);
    }
}
