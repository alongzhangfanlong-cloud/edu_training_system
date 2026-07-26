/**
 * 教师管理 API — 对齐后端 EduTeacherController + SysUserController
 */
import request from '@/utils/request';

const BASE = '/api/v1/teaching/teacher';

// ===== 教师档案 CRUD =====

export function listTeacher(query) {
  return request({ url: `${BASE}/list`, method: 'get', params: query });
}

export function getTeacher(id) {
  return request({ url: `${BASE}/${id}`, method: 'get' });
}

export function addTeacher(data) {
  return request({ url: BASE, method: 'post', data });
}

export function updateTeacher(data) {
  return request({ url: BASE, method: 'put', data });
}

export function delTeacher(ids) {
  return request({ url: `${BASE}/${ids}`, method: 'delete' });
}

// ===== 教师角色用户（用于下拉选项，返回sys_user.id） =====

/** 查询所有拥有教师角色的启用用户（支持关键字搜索） */
export function listTeacherUsers(keyword) {
  return request({ url: '/api/v1/teachers/options', method: 'get', params: { keyword } });
}
