/**
 * 排课记录 API — 对齐后端 EduScheduleController
 */
import request from '@/utils/request';

const BASE = '/api/v1/schedules';

export function listSchedule(query) {
  return request({ url: `${BASE}/list`, method: 'get', params: query });
}

export function getSchedule(id) {
  return request({ url: `${BASE}/${id}`, method: 'get' });
}

export function addSchedule(data) {
  return request({ url: BASE, method: 'post', data });
}

export function delSchedule(ids) {
  return request({ url: `${BASE}/${ids}`, method: 'delete' });
}

// 停课
export function cancelSchedule(id, data) {
  return request({ url: `${BASE}/${id}/cancel`, method: 'put', data });
}

// 调课
export function rescheduleSchedule(id, data) {
  return request({ url: `${BASE}/${id}/reschedule`, method: 'put', data });
}

// 手动消课
export function consumeSchedule(id) {
  return request({ url: `${BASE}/${id}/consume`, method: 'put' });
}
