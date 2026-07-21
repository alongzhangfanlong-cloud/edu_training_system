import request from '@/utils/request';

export function getHomework(studentId, params) {
  return request({ url: `/api/v1/parent/children/${studentId}/homework`, method: 'GET', data: params });
}
