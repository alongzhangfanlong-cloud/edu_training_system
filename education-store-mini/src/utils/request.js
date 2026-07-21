import { useAuthStore } from '@/stores/auth';

const BASE_URL = 'http://localhost:8080';

function request(options = {}) {
  const authStore = useAuthStore();

  return new Promise((resolve, reject) => {
    uni.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data,
      header: {
        'Content-Type': 'application/json',
        ...(authStore.token ? { Authorization: `Bearer ${authStore.token}` } : {}),
      },
      success(res) {
        const { statusCode, data } = res;
        if (statusCode === 200) {
          resolve(data);
        } else if (statusCode === 401) {
          authStore.logout();
          uni.reLaunch({ url: '/pages/login/index' });
          reject(new Error('登录已过期'));
        } else {
          uni.showToast({ title: data.msg || '请求失败', icon: 'none' });
          reject(new Error(data.msg || `HTTP ${statusCode}`));
        }
      },
      fail(err) {
        uni.showToast({ title: '网络请求失败', icon: 'none' });
        reject(err);
      },
    });
  });
}

export default request;
