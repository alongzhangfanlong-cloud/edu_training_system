/**
 * 教师管理 API — 对齐后端 EduTeacherController
 */
import request from '@/utils/request';

const BASE = '/api/v1/teaching/teacher';

export function listTeacher(query) {
  return request({ url: `${BASE}/list`, method: 'get', params: query });
}

export function getTeacher(id) {
  return request({ url: `${BASE}/${id}`, method: 'get' });
}

export function addTeacher(data) {
  return request({ url: BASE, method: 'post', data });
}

export function updateTeacher(data) {
  return request({ url: BASE, method: 'put', data });
}

export function delTeacher(ids) {
  return request({ url: `${BASE}/${ids}`, method: 'delete' });
}
