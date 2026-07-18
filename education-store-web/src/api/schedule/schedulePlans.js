/**
 * 排课计划 API — 对齐后端 EduSchedulePlanController
 */
import request from '@/utils/request';

const BASE = '/api/v1/schedule/plans';

export function listSchedulePlan(query) {
  return request({ url: `${BASE}/list`, method: 'get', params: query });
}

export function getSchedulePlan(id) {
  return request({ url: `${BASE}/${id}`, method: 'get' });
}

export function addSchedulePlan(data) {
  return request({ url: BASE, method: 'post', data });
}

export function updateSchedulePlan(data) {
  return request({ url: BASE, method: 'put', data });
}

export function delSchedulePlan(ids) {
  return request({ url: `${BASE}/${ids}`, method: 'delete' });
}
