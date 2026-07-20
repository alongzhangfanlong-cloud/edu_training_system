/**
 * 工作台 Dashboard API — 对齐后端 DashboardController
 */
import request from '@/utils/request';

const BASE = '/api/v1/dashboard';

/** 获取欢迎区信息（用户名/上次登录/服务器日期） */
export function getUserInfo() {
  return request({ url: `${BASE}/user-info`, method: 'get' });
}

/** 获取统计卡片数据（学员数/排课数/教师数/营收+趋势） */
export function getStatistics() {
  return request({ url: `${BASE}/statistics`, method: 'get' });
}

/** 获取排课日历数据（月/周/日视图） */
export function getCalendar(params) {
  return request({ url: `${BASE}/calendar`, method: 'get', params });
}

/** 获取某排课的学生签到详情 */
export function getScheduleStudents(scheduleId) {
  return request({ url: `${BASE}/schedule/${scheduleId}/students`, method: 'get' });
}

/** 获取待办提醒数据（请假审批/退费处理/排课冲突） */
export function getTodos() {
  return request({ url: `${BASE}/todos`, method: 'get' });
}

/** 获取本月报名统计（按课程） */
export function getEnrollmentStats() {
  return request({ url: `${BASE}/enrollment-stats`, method: 'get' });
}
