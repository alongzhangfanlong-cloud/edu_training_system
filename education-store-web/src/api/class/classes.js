/**
 * 班级管理 API — 对齐后端 EduClassController + EduClassStudentController
 */
import request from '@/utils/request';

const BASE = '/api/v1/classes';

// ===== 班级 CRUD =====

export function listClass(query) {
  return request({ url: `${BASE}/list`, method: 'get', params: query });
}

export function getClass(id) {
  return request({ url: `${BASE}/${id}`, method: 'get' });
}

export function addClass(data) {
  return request({ url: BASE, method: 'post', data });
}

export function updateClass(data) {
  return request({ url: BASE, method: 'put', data });
}

export function delClass(ids) {
  return request({ url: `${BASE}/${ids}`, method: 'delete' });
}

export function changeClassStatus(id, status) {
  return request({ url: `${BASE}/${id}/status`, method: 'put', data: { status } });
}

// ===== 班级学员 =====

export function listClassStudent(classId) {
  return request({ url: `${BASE}/${classId}/students/list`, method: 'get' });
}

export function addClassStudent(classId, data) {
  return request({ url: `${BASE}/${classId}/students`, method: 'post', data });
}

export function updateClassStudentStatus(classId, id, status) {
  return request({ url: `${BASE}/${classId}/students/${id}/status`, method: 'put', data: { status } });
}

export function removeClassStudent(classId, id) {
  return request({ url: `${BASE}/${classId}/students/${id}`, method: 'delete' });
}
