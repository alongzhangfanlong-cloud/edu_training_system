/**
 * 学评教 API — 对齐后端 TeacherEvaluationController
 */
import request from '@/utils/request';

const BASE = '/api/v1/teaching/teacher-evaluation';

export function listTeacherEvaluation(query) {
  return request({ url: `${BASE}/list`, method: 'get', params: query });
}

export function getTeacherEvaluation(id) {
  return request({ url: `${BASE}/${id}`, method: 'get' });
}

export function addTeacherEvaluation(data) {
  return request({ url: BASE, method: 'post', data });
}

export function hideEvaluation(id) {
  return request({ url: `${BASE}/${id}/hide`, method: 'post' });
}

export function restoreEvaluation(id) {
  return request({ url: `${BASE}/${id}/restore`, method: 'post' });
}

export function getTeacherStats(teacherId) {
  return request({ url: `${BASE}/stats/teacher/${teacherId}`, method: 'get' });
}
