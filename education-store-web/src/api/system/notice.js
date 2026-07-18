/**
 * 通知公告 API
 */
import request from '@/utils/request';

// 获取通知公告列表(分页)
export function listNotice(query) {
  return request({ url: '/api/v1/system/notice/list', method: 'get', params: query });
}

// 获取通知公告详情
export function getNotice(noticeId) {
  return request({ url: `/api/v1/system/notice/${noticeId}`, method: 'get' });
}

// 新增通知公告
export function addNotice(data) {
  return request({ url: '/api/v1/system/notice', method: 'post', data });
}

// 修改通知公告
export function updateNotice(data) {
  return request({ url: '/api/v1/system/notice', method: 'put', data });
}

// 删除通知公告
export function delNotice(noticeIds) {
  return request({ url: `/api/v1/system/notice/${noticeIds}`, method: 'delete' });
}
