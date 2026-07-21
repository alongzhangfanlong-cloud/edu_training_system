import request from '@/utils/request';

/** 教师请假列表（分页） */
export function getLeaveList(params) {
  return request({ url: '/api/v1/attendance/leaves/teacher/list', method: 'GET', data: params });
}

/** 教师请假详情 */
export function getLeaveDetail(id) {
  return request({ url: `/api/v1/attendance/leaves/teacher/${id}`, method: 'GET' });
}

/** 提交请假申请 */
export function submitLeave(data) {
  return request({ url: '/api/v1/attendance/leaves/teacher/', method: 'POST', data });
}
