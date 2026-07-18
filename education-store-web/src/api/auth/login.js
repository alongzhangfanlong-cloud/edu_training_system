/**
 * 认证授权 API — 对齐后端 AuthController
 */
import request from '@/utils/request';

// 用户名密码登录
export function login(data) {
  return request({ url: '/api/v1/auth/login', method: 'post', data });
}

// 刷新Token
export function refreshToken(refreshTokenValue) {
  return request({
    url: '/api/v1/auth/refresh',
    method: 'post',
    data: { refreshToken: refreshTokenValue },
    ignoreToken: true,
    __isRefreshRequest: true
  });
}

// 退出登录
export function logout() {
  return request({ url: '/api/v1/auth/logout', method: 'post' });
}

// 修改密码
export function updatePassword(data) {
  return request({ url: '/api/v1/auth/password', method: 'put', data });
}
