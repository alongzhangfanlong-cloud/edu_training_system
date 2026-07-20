/**
 * 签到管理 API — 对齐后端 EduAttendanceController
 */
import request from '@/utils/request';

const BASE = '/api/v1/attendance/signin';

/** 签到记录列表（分页） */
export function listSignin(query) {
  return request({ url: `${BASE}/list`, method: 'get', params: query });
}

/** 批量签到 */
export function batchSignin(scheduleId, classId, records, operatorId = 0) {
  return request({
    url: `${BASE}/batch`,
    method: 'post',
    params: { scheduleId, classId, operatorId },
    data: records
  });
}

/** 签到统计（按排课） */
export function getSigninStats(scheduleId) {
  return request({ url: `${BASE}/stats/${scheduleId}`, method: 'get' });
}
