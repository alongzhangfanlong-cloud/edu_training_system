import request from '@/utils/request';

export function getSchedule(studentId, params) {
  return request({ url: `/api/v1/parent/children/${studentId}/schedule`, method: 'GET', data: params });
}
