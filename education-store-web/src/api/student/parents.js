/**
 * 家长关联 API — 对齐后端 EduStudentParentController
 */
import request from '@/utils/request';

// 查询某学员的家长列表
export function listParent(studentId) {
  return request({ url: `/api/v1/students/${studentId}/parents/list`, method: 'get' });
}

// 添加家长关联（绑定现有 sys_user）
export function addParent(studentId, data) {
  return request({ url: `/api/v1/students/${studentId}/parents`, method: 'post', data });
}

// 更新家长信息
export function updateParent(studentId, id, data) {
  return request({ url: `/api/v1/students/${studentId}/parents/${id}`, method: 'put', data });
}

// 解绑家长（仅删关联，不删用户账号）
export function delParent(studentId, ids) {
  return request({ url: `/api/v1/students/${studentId}/parents/${ids}`, method: 'delete' });
}
