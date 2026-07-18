/**
 * 课时变动 API — 对齐后端 EduStudentHoursLogController
 *
 * 课时变动日志型表，只追加不修改，无 update_time/deleted 字段。
 */
import request from '@/utils/request';

// 查询某学员的课时变动记录（分页）
export function listHoursLog(studentId, query) {
  return request({ url: `/api/v1/students/${studentId}/hours-logs/list`, method: 'get', params: query });
}

// 手动调整课时（管理员操作）
export function adjustHours(studentId, data) {
  return request({ url: `/api/v1/students/${studentId}/hours-logs`, method: 'post', data });
}

// 查询课时余额
export function getHoursBalance(studentId) {
  return request({ url: `/api/v1/students/${studentId}/hours-logs/balance`, method: 'get' });
}
