/**
 * 作业提交批改 API — 对齐后端 EduHomeworkSubmissionController
 */
import request from '@/utils/request';

const BASE = '/api/v1/teaching/homework-submission';

export function listSubmission(query) {
  return request({ url: `${BASE}/list`, method: 'get', params: query });
}

export function getSubmission(id) {
  return request({ url: `${BASE}/${id}`, method: 'get' });
}

export function gradeSubmission(id, data) {
  return request({ url: `${BASE}/${id}/grade`, method: 'put', data });
}
