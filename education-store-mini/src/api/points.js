import request from '@/utils/request';

export function getPoints(studentId) {
  return request({ url: `/api/v1/parent/children/${studentId}/points`, method: 'GET' });
}

export function getPointsLog(studentId) {
  return request({ url: `/api/v1/parent/children/${studentId}/points/log`, method: 'GET' });
}
