/**
 * 课程管理 API — 对齐后端 EduCourseController
 */
import request from '@/utils/request';

const BASE = '/api/v1/courses';

export function listCourse(query) {
  return request({ url: `${BASE}/list`, method: 'get', params: query });
}

export function getCourse(id) {
  return request({ url: `${BASE}/${id}`, method: 'get' });
}

export function addCourse(data) {
  return request({ url: BASE, method: 'post', data });
}

export function updateCourse(data) {
  return request({ url: BASE, method: 'put', data });
}

export function delCourse(ids) {
  return request({ url: `${BASE}/${ids}`, method: 'delete' });
}
