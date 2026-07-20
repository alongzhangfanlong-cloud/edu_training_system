/**
 * 教师请假 API — 对齐后端 EduLeaveTeacherController
 */
import request from '@/utils/request';

const BASE = '/api/v1/attendance/leaves/teacher';

/** 教师请假列表（分页） */
export function listTeacherLeave(query) {
  return request({ url: `${BASE}/list`, method: 'get', params: query });
}

/** 教师请假详情 */
export function getTeacherLeave(id) {
  return request({ url: `${BASE}/${id}`, method: 'get' });
}

/** 提交教师请假申请 */
export function addTeacherLeave(data) {
  return request({ url: BASE, method: 'post', data });
}

/** 管理员审批通过 */
export function approveTeacherLeave(id, approverId) {
  return request({ url: `${BASE}/${id}/approve`, method: 'put', data: { approverId } });
}

/** 管理员驳回 */
export function rejectTeacherLeave(id, approverId, reason) {
  return request({ url: `${BASE}/${id}/reject`, method: 'put', data: { approverId, reason } });
}
