/**
 * 教师工作台 API — 对齐后端 TeacherDashboardController
 */
import request from '@/utils/request';

const BASE = '/api/v1/teacher/dashboard';

/** 获取欢迎区信息（教师姓名/上次登录/服务器日期） */
export function getUserInfo() {
  return request({ url: `${BASE}/user-info`, method: 'get' });
}

/** 获取统计卡片数据（今日课程数/待签到班级数/待评价学生数） */
export function getStatistics() {
  return request({ url: `${BASE}/statistics`, method: 'get' });
}

/** 获取今日课表（含签到状态摘要） */
export function getSchedule() {
  return request({ url: `${BASE}/schedule`, method: 'get' });
}

/** 获取某排课的班级学生名单+签到状态 */
export function getScheduleStudents(scheduleId) {
  return request({ url: `${BASE}/schedule/${scheduleId}/students`, method: 'get' });
}

/** 获取待办数据（待批改作业/待回复请假） */
export function getTodos() {
  return request({ url: `${BASE}/todos`, method: 'get' });
}
