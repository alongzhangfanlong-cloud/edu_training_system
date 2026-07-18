/**
 * 收费管理 API — 对齐后端 EduTuitionFeeController
 */
import request from '@/utils/request';

const BASE = '/api/v1/tuition/fees';

/** 分页查询收费记录列表 (studentId/enrollmentId/status) */
export function listFee(query) {
  return request({ url: `${BASE}/list`, method: 'get', params: query });
}

/** 获取收费记录详情 */
export function getFee(id) {
  return request({ url: `${BASE}/${id}`, method: 'get' });
}

/** 新增收费记录 */
export function addFee(data) {
  return request({ url: BASE, method: 'post', data });
}

/** 更新收费记录 */
export function updateFee(data) {
  return request({ url: BASE, method: 'put', data });
}

/** 批量删除收费记录 */
export function delFee(ids) {
  return request({ url: `${BASE}/${ids}`, method: 'delete' });
}
