/**
 * 教室管理 API — 对齐后端 EduClassroomController
 */
import request from '@/utils/request';

const BASE = '/api/v1/classrooms';

export function listClassroom(query) {
  return request({ url: `${BASE}/list`, method: 'get', params: query });
}

export function getClassroom(id) {
  return request({ url: `${BASE}/${id}`, method: 'get' });
}

export function addClassroom(data) {
  return request({ url: BASE, method: 'post', data });
}

export function updateClassroom(data) {
  return request({ url: BASE, method: 'put', data });
}

export function delClassroom(ids) {
  return request({ url: `${BASE}/${ids}`, method: 'delete' });
}
