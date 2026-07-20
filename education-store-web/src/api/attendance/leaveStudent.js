/**
 * 学生请假 API — 对齐后端 EduLeaveStudentController
 */
import request from '@/utils/request';

const BASE = '/api/v1/attendance/leaves/student';

/** 学生请假列表（分页） */
export function listStudentLeave(query) {
  return request({ url: `${BASE}/list`, method: 'get', params: query });
}

/** 学生请假详情 */
export function getStudentLeave(id) {
  return request({ url: `${BASE}/${id}`, method: 'get' });
}

/** 提交学生请假申请 */
export function addStudentLeave(data) {
  return request({ url: BASE, method: 'post', data });
}

/** 教师确认通过 */
export function approveStudentLeave(id, teacherId) {
  return request({ url: `${BASE}/${id}/approve`, method: 'put', data: { teacherId } });
}

/** 教师驳回 */
export function rejectStudentLeave(id, teacherId, reason) {
  return request({ url: `${BASE}/${id}/reject`, method: 'put', data: { teacherId, reason } });
}
