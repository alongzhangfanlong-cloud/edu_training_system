import request from '@/utils/request';

/** 获取所有学生列表（管理员） */
export function getStudentList(params) {
  return request({ url: '/api/v1/students/list', method: 'GET', data: params });
}

/** 获取学生详情（管理员） */
export function getStudentDetail(id) {
  return request({ url: `/api/v1/students/${id}`, method: 'GET' });
}
