/**
 * 学员管理 API — 对齐后端 EduStudentController
 */
import request from '@/utils/request';

const BASE = '/api/v1/students';

// 分页查询学员列表
export function listStudent(query) {
  return request({ url: `${BASE}/list`, method: 'get', params: query });
}

// 获取学员详情（含家长关联、课时余额）
export function getStudent(id) {
  return request({ url: `${BASE}/${id}`, method: 'get' });
}

// 新增学员
export function addStudent(data) {
  return request({ url: BASE, method: 'post', data });
}

// 更新学员信息（ID 从 body 取）
export function updateStudent(data) {
  return request({ url: BASE, method: 'put', data });
}

// 批量删除学员（逻辑删除，ids: "1,2,3"）
export function delStudent(ids) {
  return request({ url: `${BASE}/${ids}`, method: 'delete' });
}

// 更新学员状态（后端 @RequestParam，使用 params 而非 data）
export function changeStudentStatus(id, status) {
  return request({ url: `${BASE}/${id}/status`, method: 'put', params: { status } });
}
