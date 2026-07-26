/**
 * 课酬管理 API — 对齐后端 EduTeacherPayrollController
 */
import request from '@/utils/request';

const BASE = '/api/v1/payrolls';

/** 分页查询课酬列表 (teacherId/month/status) */
export function listPayroll(query) {
  return request({ url: `${BASE}/list`, method: 'get', params: query });
}

/** 获取课酬详情 */
export function getPayroll(id) {
  return request({ url: `${BASE}/${id}`, method: 'get' });
}

/** 新增课酬记录 */
export function addPayroll(data) {
  return request({ url: BASE, method: 'post', data });
}

/** 确认课酬 */
export function confirmPayroll(id) {
  return request({ url: `${BASE}/${id}/confirm`, method: 'put' });
}

/** 取消确认课酬 */
export function cancelConfirmPayroll(id) {
  return request({ url: `${BASE}/${id}/cancel-confirm`, method: 'put' });
}

/** 发放课酬 */
export function payPayroll(id) {
  return request({ url: `${BASE}/${id}/pay`, method: 'put' });
}

/** 批量删除课酬记录 */
export function delPayroll(ids) {
  return request({ url: `${BASE}/${ids}`, method: 'delete' });
}

/** 自动统计课酬（从已消课排课聚合生成） */
export function autoGeneratePayroll(month) {
  return request({ url: `${BASE}/auto-generate`, method: 'post', params: { month } });
}
