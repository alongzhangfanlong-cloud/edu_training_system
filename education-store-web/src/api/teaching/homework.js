/**
 * 课后作业 API — 对齐后端 HomeworkController
 */
import request from '@/utils/request';

const BASE = '/api/v1/teaching/homework';

export function listHomework(query) {
  return request({ url: `${BASE}/list`, method: 'get', params: query });
}

export function getHomework(id) {
  return request({ url: `${BASE}/${id}`, method: 'get' });
}

export function addHomework(data) {
  return request({ url: BASE, method: 'post', data });
}

export function updateHomework(id, data) {
  return request({ url: `${BASE}/${id}`, method: 'put', data });
}

export function delHomework(id, currentUserId) {
  return request({ url: `${BASE}/${id}`, method: 'delete', params: { currentUserId } });
}
