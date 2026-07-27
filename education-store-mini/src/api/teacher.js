import request from '@/utils/request';

/** 获取教师欢迎区信息 */
export function getUserInfo() {
  return request({ url: '/api/v1/teacher/dashboard/user-info', method: 'GET' });
}

/** 获取统计卡片数据 */
export function getStatistics() {
  return request({ url: '/api/v1/teacher/dashboard/statistics', method: 'GET' });
}

/** 获取今日课表（含签到状态摘要） */
export function getTodaySchedule() {
  return request({ url: '/api/v1/teacher/dashboard/schedule', method: 'GET' });
}

/** 获取日期范围课表（周/月视图） */
export function getSchedules(params) {
  return request({ url: '/api/v1/teacher/dashboard/schedule/range', method: 'GET', data: params });
}

/** 获取某排课的班级学生名单 + 签到状态 */
export function getScheduleStudents(scheduleId) {
  return request({ url: `/api/v1/teacher/dashboard/schedule/${scheduleId}/students`, method: 'GET' });
}

/** 获取某排课签到统计 */
export function getScheduleStats(scheduleId) {
  return request({ url: `/api/v1/teacher/dashboard/schedule/${scheduleId}/stats`, method: 'GET' });
}

/** 教师批量签到 */
export function batchSignin(scheduleId, records) {
  return request({
    url: `/api/v1/teacher/dashboard/signin/batch?scheduleId=${scheduleId}`,
    method: 'POST',
    data: records
  });
}

/** 获取待办数据 */
export function getTodos() {
  return request({ url: '/api/v1/teacher/dashboard/todos', method: 'GET' });
}

/** 获取我教的学生列表 */
export function getMyStudents() {
  return request({ url: '/api/v1/teacher/dashboard/my-students', method: 'GET' });
}
