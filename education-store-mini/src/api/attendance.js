import request from '@/utils/request';

export function getAttendance(studentId, params) {
  return request({ url: `/api/v1/parent/children/${studentId}/attendance`, method: 'GET', data: params });
}

export function getAttendanceStats(studentId) {
  return request({ url: `/api/v1/parent/children/${studentId}/attendance-stats`, method: 'GET' });
}
