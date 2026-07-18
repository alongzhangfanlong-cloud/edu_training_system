/**
 * 用户管理 API — 对齐后端 SysUserController
 */
import request, { downloadBlob } from '@/utils/request';

const BASE = '/api/v1/system/user';

// 分页查询用户列表
export function listUser(query) {
  return request({ url: `${BASE}/list`, method: 'get', params: query });
}

// 获取用户详情
export function getUser(userId) {
  return request({ url: `${BASE}/${userId}`, method: 'get' });
}

// 新增用户
export function addUser(data) {
  return request({ url: BASE, method: 'post', data });
}

// 修改用户
export function updateUser(userId, data) {
  return request({ url: `${BASE}/${userId}`, method: 'put', data });
}

// 批量删除用户 (ids: "1,2,3")
export function delUser(userIds) {
  return request({ url: `${BASE}/${userIds}`, method: 'delete' });
}

// 重置密码
export function resetUserPwd(userId, password) {
  return request({ url: `${BASE}/${userId}/password`, method: 'put', data: { password } });
}

// 启/停用用户状态
export function changeUserStatus(userId, status) {
  return request({ url: `${BASE}/${userId}/status`, method: 'put', data: { status } });
}

// 查询用户角色ID列表
export function getAuthRole(userId) {
  return request({ url: `${BASE}/${userId}/role`, method: 'get' });
}

// 更新用户角色
export function updateAuthRole(userId, roleIds) {
  return request({ url: `${BASE}/${userId}/role`, method: 'put', data: { roleIds } });
}

// 获取当前用户信息
export function getInfo() {
  return request({ url: `${BASE}/getInfo`, method: 'get' });
}

// 获取当前用户路由菜单
export function getRouters() {
  return request({ url: `${BASE}/getRouters`, method: 'get' });
}

// 下载导入模板
export function downloadTemplate() {
  return downloadBlob({ url: `${BASE}/template`, method: 'get' });
}

// 导入用户 (formData 包含 file 和 updateSupport)
export function importUser(formData) {
  return request({
    url: `${BASE}/import`,
    method: 'post',
    data: formData
  });
}

// 导出用户
export function exportUser(query) {
  return downloadBlob({ url: `${BASE}/export`, method: 'get', params: query });
}
