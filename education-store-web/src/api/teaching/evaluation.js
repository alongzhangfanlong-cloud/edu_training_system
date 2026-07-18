/**
 * 教评学 API — 对齐后端 EvaluationController
 */
import request from '@/utils/request';

const BASE = '/api/v1/teaching/evaluation';

export function listEvaluation(query) {
  return request({ url: `${BASE}/list`, method: 'get', params: query });
}

export function getEvaluation(id) {
  return request({ url: `${BASE}/${id}`, method: 'get' });
}

export function saveDraft(params, data) {
  return request({ url: `${BASE}/draft`, method: 'post', params, data });
}

export function submitEvaluation(id, params, data) {
  return request({ url: `${BASE}/${id}/submit`, method: 'post', params, data });
}

export function delEvaluation(id, currentUserId) {
  return request({ url: `${BASE}/${id}`, method: 'delete', params: { currentUserId } });
}
