/**
 * 课件管理 API — 对齐后端 EduResourceController
 */
import request from '@/utils/request';

const BASE = '/api/v1/resources';

export function listResource(query) {
  return request({ url: `${BASE}/list`, method: 'get', params: query });
}

export function getResource(id) {
  return request({ url: `${BASE}/${id}`, method: 'get' });
}

export function addResource(data) {
  return request({ url: BASE, method: 'post', data });
}

export function updateResource(id, data) {
  return request({ url: `${BASE}/${id}`, method: 'put', data });
}

export function delResource(ids) {
  return request({ url: `${BASE}/${ids}`, method: 'delete' });
}

// 记录下载（原子累加计数）
export function recordDownload(id) {
  return request({ url: `${BASE}/${id}/download`, method: 'post' });
}
