/**
 * 意向学员 API — 对齐后端 EduProspectController + EduProspectFollowupController
 */
import request from '@/utils/request';

const BASE = '/api/v1/prospects';

// 分页查询意向学员列表
export function listProspect(query) {
  return request({ url: `${BASE}/list`, method: 'get', params: query });
}

// 获取意向学员详情
export function getProspect(id) {
  return request({ url: `${BASE}/${id}`, method: 'get' });
}

// 新增意向学员
export function addProspect(data) {
  return request({ url: BASE, method: 'post', data });
}

// 更新意向学员
export function updateProspect(data) {
  return request({ url: BASE, method: 'put', data });
}

// 批量删除意向学员
export function delProspect(ids) {
  return request({ url: `${BASE}/${ids}`, method: 'delete' });
}

// 转化为在学学员
export function convertProspect(id) {
  return request({ url: `${BASE}/${id}/convert`, method: 'post' });
}

// ========== 跟进记录（子资源） ==========

// 查询某意向学员的跟进记录列表
export function listFollowup(prospectId) {
  return request({ url: `${BASE}/${prospectId}/followups/list`, method: 'get' });
}

// 新增跟进记录
export function addFollowup(prospectId, data) {
  return request({ url: `${BASE}/${prospectId}/followups`, method: 'post', data });
}

// 更新跟进记录
export function updateFollowup(prospectId, id, data) {
  return request({ url: `${BASE}/${prospectId}/followups/${id}`, method: 'put', data });
}

// 删除跟进记录
export function delFollowup(prospectId, ids) {
  return request({ url: `${BASE}/${prospectId}/followups/${ids}`, method: 'delete' });
}
