import request from '@/utils/request';

export function getLeaves(studentId) {
  return request({ url: `/api/v1/parent/children/${studentId}/leaves`, method: 'GET' });
}

export function submitLeave(studentId, data) {
  return request({ url: `/api/v1/parent/children/${studentId}/leaves`, method: 'POST', data });
}
