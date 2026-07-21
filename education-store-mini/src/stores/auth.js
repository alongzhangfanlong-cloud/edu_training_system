import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { wechatLogin } from '@/api/auth';

function parseJwt(token) {
  try {
    const payload = token.split('.')[1];
    const decoded = JSON.parse(atob(payload));
    return decoded.role || decoded.roles || '';
  } catch { return ''; }
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref(uni.getStorageSync('token') || '');
  const userInfo = ref(null);
  const role = ref(uni.getStorageSync('role') || '');
  const isLogin = computed(() => !!token.value);

  async function login() {
    return new Promise((resolve, reject) => {
      uni.login({
        provider: 'weixin',
        success: async (loginRes) => {
          try {
            const res = await wechatLogin({ code: loginRes.code });
            if (res.code === 200) {
              const jwt = res.data.token || res.data.access_token;
              token.value = jwt;
              userInfo.value = res.data.userInfo || res.data;
              const userRole = parseJwt(jwt) || (res.data.userInfo && res.data.userInfo.role) || (res.data.role) || 'PARENT';
              role.value = userRole;
              uni.setStorageSync('token', token.value);
              uni.setStorageSync('role', userRole);
              resolve(res.data);
            } else {
              reject(new Error(res.msg || '登录失败'));
            }
          } catch (e) {
            reject(e);
          }
        },
        fail: (err) => {
          reject(err);
        },
      });
    });
  }

  function logout() {
    token.value = '';
    userInfo.value = null;
    role.value = '';
    uni.removeStorageSync('token');
    uni.removeStorageSync('role');
    uni.reLaunch({ url: '/pages/login/index' });
  }

  function checkAuth() {
    if (!token.value) return false;
    role.value = uni.getStorageSync('role') || '';
    return true;
  }

  return { token, userInfo, role, isLogin, login, logout, checkAuth };
});
