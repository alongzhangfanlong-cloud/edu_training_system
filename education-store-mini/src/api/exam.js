import request from '@/utils/request';

export function getExamScores(studentId) {
  return request({ url: `/api/v1/parent/children/${studentId}/exam-scores`, method: 'GET' });
}
