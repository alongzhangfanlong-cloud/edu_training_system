/**
 * 课酬单价 API — 对齐后端 EduTeacherPayRateController
 */
import request from '@/utils/request';

const BASE = '/api/v1/pay-rates';

/** 分页查询课酬单价列表 (teacherId/courseId) */
export function listPayRate(query) {
  return request({ url: `${BASE}/list`, method: 'get', params: query });
}

/** 获取课酬单价详情 */
export function getPayRate(id) {
  return request({ url: `${BASE}/${id}`, method: 'get' });
}

/** 新增课酬单价 */
export function addPayRate(data) {
  return request({ url: BASE, method: 'post', data });
}

/** 更新课酬单价 */
export function updatePayRate(data) {
  return request({ url: BASE, method: 'put', data });
}

/** 批量删除课酬单价 */
export function delPayRate(ids) {
  return request({ url: `${BASE}/${ids}`, method: 'delete' });
}
