/**
 * 赠送课时 API — 对齐后端 EduTuitionGiftHoursController
 */
import request from '@/utils/request';

const BASE = '/api/v1/tuition/gifts';

/** 分页查询赠送记录列表 (studentId) */
export function listGift(query) {
  return request({ url: `${BASE}/list`, method: 'get', params: query });
}

/** 获取赠送记录详情 */
export function getGift(id) {
  return request({ url: `${BASE}/${id}`, method: 'get' });
}

/** 新增赠送课时 */
export function addGift(data) {
  return request({ url: BASE, method: 'post', data });
}

/** 批量删除赠送记录 */
export function delGift(ids) {
  return request({ url: `${BASE}/${ids}`, method: 'delete' });
}
