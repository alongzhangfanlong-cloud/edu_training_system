import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { wechatLogin } from '@/api/auth';

export const useAuthStore = defineStore('auth', () => {
  const token = ref(uni.getStorageSync('token') || '');
  const userInfo = ref(null);
  const isLogin = computed(() => !!token.value);

  async function login() {
    return new Promise((resolve, reject) => {
      uni.login({
        provider: 'weixin',
        success: async (loginRes) => {
          try {
            const res = await wechatLogin({ code: loginRes.code });
            if (res.code === 200) {
              token.value = res.data.token || res.data.access_token;
              userInfo.value = res.data.userInfo || res.data;
              uni.setStorageSync('token', token.value);
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
    uni.removeStorageSync('token');
    uni.reLaunch({ url: '/pages/login/index' });
  }

  function checkAuth() {
    if (!token.value) return false;
    return true;
  }

  return { token, userInfo, isLogin, login, logout, checkAuth };
});
