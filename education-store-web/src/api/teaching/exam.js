/**
 * 成绩管理 API — 对齐后端 ExamScoreController
 */
import request from '@/utils/request';

const BASE = '/api/v1/teaching/exam';

export function listExam(query) {
  return request({ url: `${BASE}/list`, method: 'get', params: query });
}

export function getExam(id) {
  return request({ url: `${BASE}/${id}`, method: 'get' });
}

export function getExamStudents(id) {
  return request({ url: `${BASE}/${id}/students`, method: 'get' });
}

export function addExam(data) {
  return request({ url: BASE, method: 'post', data });
}

export function updateExam(id, data) {
  return request({ url: `${BASE}/${id}`, method: 'put', data });
}

export function delExam(id, currentUserId) {
  return request({ url: `${BASE}/${id}`, method: 'delete', params: { currentUserId } });
}
