import request from '@/utils/request';

export function getNotificationSettings() {
  return request({ url: '/api/v1/user/notification-settings', method: 'GET' });
}

export function updateNotificationSettings(data) {
  return request({ url: '/api/v1/user/notification-settings', method: 'PUT', data });
}
