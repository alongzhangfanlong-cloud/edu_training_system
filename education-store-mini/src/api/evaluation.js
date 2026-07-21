import request from '@/utils/request';

export function getEvaluations(studentId) {
  return request({ url: `/api/v1/parent/children/${studentId}/evaluations`, method: 'GET' });
}
