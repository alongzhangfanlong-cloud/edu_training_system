import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { login as apiLogin, wechatLogin } from '@/api/auth';

function parseJwt(token) {
  try {
    const payload = token.split('.')[1];
    const base64 = payload.replace(/-/g, '+').replace(/_/g, '/');
    const pad = base64.length % 4;
    const padded = pad ? base64 + '='.repeat(4 - pad) : base64;
    const decoded = JSON.parse(atob(padded));
    return decoded.role || decoded.roles || decoded.userRole || '';
  } catch { return ''; }
}

function normalizeRole(role) {
  if (!role) return 'PARENT';
  let r;
  if (Array.isArray(role)) {
    r = role.join(',').toUpperCase();
  } else {
    r = String(role).toUpperCase();
  }
  if (r.includes('ADMIN') || r.includes('MANAGER')) return 'ADMIN';
  if (r.includes('TEACHER')) return 'TEACHER';
  return 'PARENT';
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref(uni.getStorageSync('token') || '');
  const userInfo = ref(null);
  const role = ref(uni.getStorageSync('role') || '');
  const isLogin = computed(() => !!token.value);
  const isTeacher = computed(() => role.value === 'TEACHER');
  const isAdmin = computed(() => role.value === 'ADMIN');

  async function doLogin(username, password) {
    const res = await apiLogin({ username, password });
    return handleLoginResponse(res);
  }

  async function doWechatLogin(code) {
    const res = await wechatLogin({ code });
    return handleLoginResponse(res);
  }

  function handleLoginResponse(res) {
    const data = res.data || {};
    const jwt = data.accessToken || data.token || data.access_token;
    if (!jwt) throw new Error('登录返回数据异常');

    token.value = jwt;
    userInfo.value = data.userInfo || data.user || {
      userId: data.userId,
      username: data.username,
      realName: data.realName,
      avatar: data.avatar,
      permissions: data.permissions,
      roles: data.roles,
    };

    let userRole = '';
    if (data.roles && Array.isArray(data.roles) && data.roles.length > 0) {
      userRole = data.roles;
    } else if (data.userInfo && data.userInfo.roles) {
      userRole = data.userInfo.roles;
    } else if (data.role) {
      userRole = data.role;
    } else if (data.userInfo && data.userInfo.role) {
      userRole = data.userInfo.role;
    } else {
      userRole = parseJwt(jwt);
    }
    userRole = normalizeRole(userRole);
    role.value = userRole;

    uni.setStorageSync('token', token.value);
    uni.setStorageSync('role', userRole);
    uni.setStorageSync('userInfo', JSON.stringify(userInfo.value));
    return { ...data, role: userRole };
  }

  async function login() {
    return new Promise((resolve, reject) => {
      // #ifdef MP-WEIXIN
      uni.login({
        provider: 'weixin',
        success: async (loginRes) => {
          try {
            const data = await doWechatLogin(loginRes.code);
            resolve(data);
          } catch (e) { reject(e); }
        },
        fail: reject,
      });
      // #endif
      // #ifndef MP-WEIXIN
      reject(new Error('请使用账号密码登录'));
      // #endif
    });
  }

  async function loginWithPassword(username, password) {
    const data = await doLogin(username, password);
    return data;
  }

  function logout() {
    token.value = '';
    userInfo.value = null;
    role.value = '';
    uni.removeStorageSync('token');
    uni.removeStorageSync('role');
    uni.removeStorageSync('currentChildId');
    uni.reLaunch({ url: '/pages/login/index' });
  }

  function checkAuth() {
    if (!token.value) return false;
    role.value = uni.getStorageSync('role') || '';
    return true;
  }

  function getHomePage() {
    if (role.value === 'TEACHER') return '/pages/teacher/schedule/index';
    return '/pages/index/index';
  }

  return {
    token, userInfo, role, isLogin, isTeacher, isAdmin,
    login, loginWithPassword, logout, checkAuth, getHomePage,
  };
});
