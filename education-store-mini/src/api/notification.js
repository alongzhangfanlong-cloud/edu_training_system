import request from '@/utils/request';

export function getNotificationSettings() {
  return request({ url: '/api/v1/user/notification-settings', method: 'GET' });
}

export function updateNotificationSettings(data) {
  return request({ url: '/api/v1/user/notification-settings', method: 'PUT', data });
}

export function getNotifications(params) {
  return request({ url: '/api/v1/user/notifications', method: 'GET', data: params });
}

export function getNotificationDetail(id) {
  return request({ url: `/api/v1/user/notifications/${id}`, method: 'GET' });
}

export function markNotificationRead(id) {
  return request({ url: `/api/v1/user/notifications/${id}/read`, method: 'PUT' });
}

export function markAllNotificationsRead() {
  return request({ url: '/api/v1/user/notifications/read-all', method: 'PUT' });
}

export function getUnreadCount() {
  return request({ url: '/api/v1/user/notifications/unread-count', method: 'GET' });
}
