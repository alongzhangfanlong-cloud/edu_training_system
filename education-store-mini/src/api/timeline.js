import request from '@/utils/request';

export function getTimeline(params) {
  return request({ url: '/api/v1/parent/timeline', method: 'GET', data: params });
}
