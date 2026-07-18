/**
 * 角色管理 API — 对齐后端 SysRoleController
 */
import request from '@/utils/request';

const BASE = '/api/v1/system/role';

// 分页查询角色列表
export function listRole(query) {
  return request({ url: `${BASE}/list`, method: 'get', params: query });
}

// 获取所有角色(下拉列表)
export function listRoleAll() {
  return request({ url: `${BASE}/all`, method: 'get' });
}

// 获取角色详情
export function getRole(roleId) {
  return request({ url: `${BASE}/${roleId}`, method: 'get' });
}

// 新增角色
export function addRole(data) {
  return request({ url: BASE, method: 'post', data });
}

// 修改角色
export function updateRole(roleId, data) {
  return request({ url: `${BASE}/${roleId}`, method: 'put', data });
}

// 批量删除角色 (ids: "1,2,3")
export function delRole(roleIds) {
  return request({ url: `${BASE}/${roleIds}`, method: 'delete' });
}

// 启/停用角色状态
export function changeRoleStatus(roleId, status) {
  return request({ url: `${BASE}/${roleId}/status`, method: 'put', data: { status } });
}

// 查询角色已分配的用户ID列表
export function getAuthUsers(roleId) {
  return request({ url: `${BASE}/${roleId}/auth-user`, method: 'get' });
}

// 分配用户（全量替换）
export function updateAuthUsers(roleId, data) {
  return request({ url: `${BASE}/${roleId}/auth-user`, method: 'put', data });
}
