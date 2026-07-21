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

/** 获取某排课的班级学生名单 + 签到状态 */
export function getScheduleStudents(scheduleId) {
  return request({ url: `/api/v1/teacher/dashboard/schedule/${scheduleId}/students`, method: 'GET' });
}

/** 获取待办数据 */
export function getTodos() {
  return request({ url: '/api/v1/teacher/dashboard/todos', method: 'GET' });
}

/** 获取课表（日期范围） */
export function getSchedules(params) {
  return request({ url: '/api/v1/schedules/list', method: 'GET', data: params });
}
