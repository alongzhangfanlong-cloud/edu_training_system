/**
 * 报名管理 API — 对齐后端 EduEnrollmentController
 */
import request from '@/utils/request';

const BASE = '/api/v1/enrollments';

/** 分页查询报名列表 (studentId/courseId/paymentStatus) */
export function listEnrollment(query) {
  return request({ url: `${BASE}/list`, method: 'get', params: query });
}

/** 获取报名详情 */
export function getEnrollment(id) {
  return request({ url: `${BASE}/${id}`, method: 'get' });
}

/** 新增报名 */
export function addEnrollment(data) {
  return request({ url: BASE, method: 'post', data });
}

/** 更新报名 (ID 从 body 取) */
export function updateEnrollment(data) {
  return request({ url: BASE, method: 'put', data });
}

/** 取消报名 */
export function cancelEnrollment(id) {
  return request({ url: `${BASE}/${id}/cancel`, method: 'put' });
}

/** 批量删除报名 */
export function delEnrollment(ids) {
  return request({ url: `${BASE}/${ids}`, method: 'delete' });
}
