import request from '@/utils/request';

/** 教评学列表（教师评价学生，分页） */
export function getEvaluationList(params) {
  return request({ url: '/api/v1/teaching/evaluation/list', method: 'GET', data: params });
}

/** 教评学详情（含全部学生明细） */
export function getEvaluationDetail(id) {
  return request({ url: `/api/v1/teaching/evaluation/${id}`, method: 'GET' });
}

/** 暂存评价草稿 */
export function saveEvaluationDraft(data) {
  return request({ url: '/api/v1/teaching/evaluation/draft', method: 'POST', data });
}

/** 提交评价（status→1，含积分发放） */
export function submitEvaluation(data) {
  return request({ url: `/api/v1/teaching/evaluation/${data.id}/submit`, method: 'POST', data });
}

/** 删除评价（仅草稿可删） */
export function deleteEvaluation(id, currentUserId) {
  return request({ url: `/api/v1/teaching/evaluation/${id}`, method: 'DELETE', data: { currentUserId } });
}

/** 作业列表（分页） */
export function getHomeworkList(params) {
  return request({ url: '/api/v1/teaching/homework/list', method: 'GET', data: params });
}

/** 布置作业 */
export function createHomework(data) {
  return request({ url: '/api/v1/teaching/homework/', method: 'POST', data });
}

/** 作业详情 */
export function getHomeworkDetail(id) {
  return request({ url: `/api/v1/teaching/homework/${id}`, method: 'GET' });
}

/** 删除作业（软删除） */
export function deleteHomework(id, currentUserId) {
  return request({ url: `/api/v1/teaching/homework/${id}`, method: 'DELETE', data: { currentUserId } });
}
