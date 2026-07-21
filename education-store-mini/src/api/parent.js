import request from '@/utils/request';

export function getChildren() {
  return request({ url: '/api/v1/parent/children', method: 'GET' });
}

export function getChildDetail(studentId) {
  return request({ url: `/api/v1/parent/children/${studentId}`, method: 'GET' });
}
