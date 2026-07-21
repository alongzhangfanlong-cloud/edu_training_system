import request from '@/utils/request';

/** 批量签到 */
export function batchSignin(data) {
  return request({ url: '/api/v1/attendance/signin/batch', method: 'POST', data });
}

/** 签到记录列表（分页） */
export function getSigninList(params) {
  return request({ url: '/api/v1/attendance/signin/list', method: 'GET', data: params });
}

/** 某排课的签到统计 */
export function getSigninStats(scheduleId) {
  return request({ url: `/api/v1/attendance/signin/stats/${scheduleId}`, method: 'GET' });
}
