/**
 * 菜单管理 API — 对齐后端 SysMenuController
 */
import request from '@/utils/request';

const BASE = '/api/v1/system/menu';

// 获取菜单树(全部) — 同时用于上级菜单下拉框（treeselect）
export function listMenu(params) {
  return request({ url: `${BASE}/tree`, method: 'get', params });
}

// 获取用户菜单树(根据userId过滤)
export function userMenuTree(userId) {
  return request({ url: `${BASE}/tree/${userId}`, method: 'get' });
}

// 新增菜单
export function addMenu(data) {
  return request({ url: BASE, method: 'post', data });
}

// 修改菜单
export function updateMenu(data) {
  return request({ url: `${BASE}/${data.id}`, method: 'put', data });
}

// 删除菜单(级联删除子节点)
export function delMenu(menuId) {
  return request({ url: `${BASE}/${menuId}`, method: 'delete' });
}
