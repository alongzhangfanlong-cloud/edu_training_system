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

/** 排课计划预览 — 三维冲突检测 */
export function previewSchedulePlan(data) {
  return request({ url: `${BASE}/preview`, method: 'post', data });
}

/** 重新生成排课 */
export function regenerateSchedulePlan(id) {
  return request({ url: `${BASE}/${id}/regenerate`, method: 'put' });
}

/** 生成课表（首次生成） */
export function generateSchedulePlan(id, data = {}) {
  return request({ url: `${BASE}/${id}/generate`, method: 'put', data });
}

/** 获取排课计划的规则列表 */
export function getPlanRules(planId) {
  return request({ url: `${BASE}/${planId}/rules`, method: 'get' });
}

/** 保存排课计划的规则 */
export function savePlanRules(planId, rules) {
  return request({ url: `${BASE}/${planId}/rules`, method: 'post', data: rules });
}

