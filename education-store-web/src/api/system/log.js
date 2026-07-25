/**
 * 系统日志 API
 */
import request from '@/utils/request';

// ========== 操作日志 ==========

// 获取操作日志列表(分页)
export function listOperlog(query) {
  return request({ url: '/api/v1/system/log/operlog/list', method: 'get', params: query });
}

// 删除操作日志
export function delOperlog(operIds) {
  return request({ url: `/api/v1/system/log/operlog/${operIds}`, method: 'delete' });
}

// 清空操作日志
export function cleanOperlog() {
  return request({ url: '/api/v1/system/log/operlog/clean', method: 'delete' });
}

