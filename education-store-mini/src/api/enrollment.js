import request from '@/utils/request';

export function getEnrollments(studentId) {
  return request({ url: `/api/v1/parent/children/${studentId}/enrollments`, method: 'GET' });
}
