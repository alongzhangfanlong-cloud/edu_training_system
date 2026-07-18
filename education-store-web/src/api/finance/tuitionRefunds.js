/**
 * 退费管理 API — 对齐后端 EduTuitionRefundController
 */
import request from '@/utils/request';

const BASE = '/api/v1/tuition/refunds';

/** 分页查询退费记录列表 (studentId/status) */
export function listRefund(query) {
  return request({ url: `${BASE}/list`, method: 'get', params: query });
}

/** 获取退费记录详情 */
export function getRefund(id) {
  return request({ url: `${BASE}/${id}`, method: 'get' });
}

/** 发起退费申请 */
export function addRefund(data) {
  return request({ url: BASE, method: 'post', data });
}

/** 审批通过退费 (body: { approverId }) */
export function approveRefund(id, approverId) {
  return request({ url: `${BASE}/${id}/approve`, method: 'put', data: { approverId } });
}

/** 驳回退费 (body: { approverId, reason }) */
export function rejectRefund(id, approverId, reason) {
  return request({ url: `${BASE}/${id}/reject`, method: 'put', data: { approverId, reason: reason || '' } });
}

/** 批量删除退费记录 */
export function delRefund(ids) {
  return request({ url: `${BASE}/${ids}`, method: 'delete' });
}
