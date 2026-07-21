import request from '@/utils/request';

export function wechatLogin(data) {
  return request({ url: '/api/v1/auth/wechat-login', method: 'POST', data });
}

export function wechatBind(data) {
  return request({ url: '/api/v1/auth/wechat-bind', method: 'POST', data });
}

export function refreshToken() {
  return request({ url: '/api/v1/auth/refresh', method: 'POST' });
}

export function logout() {
  return request({ url: '/api/v1/auth/logout', method: 'POST' });
}
