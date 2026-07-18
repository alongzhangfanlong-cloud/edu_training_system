/**
 * 科目管理 API — 对齐后端 EduSubjectController
 */
import request from '@/utils/request';

const BASE = '/api/v1/subjects';

export function listSubject(query) {
  return request({ url: `${BASE}/list`, method: 'get', params: query });
}

export function getSubject(id) {
  return request({ url: `${BASE}/${id}`, method: 'get' });
}

export function addSubject(data) {
  return request({ url: BASE, method: 'post', data });
}

export function updateSubject(data) {
  return request({ url: BASE, method: 'put', data });
}

export function delSubject(ids) {
  return request({ url: `${BASE}/${ids}`, method: 'delete' });
}
