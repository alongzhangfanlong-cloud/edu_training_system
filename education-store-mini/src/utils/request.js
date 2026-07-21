const BASE_URL = 'http://localhost:8080';

function getToken() {
  try {
    return uni.getStorageSync('token') || '';
  } catch { return ''; }
}

// 只清理持久化 Storage，不操作 Pinia store。
// 设计决策：这里故意不 import auth store 以避免循环依赖（auth → api → request → auth）。
// reLaunch 会销毁所有页面并重载应用，Pinia store 中的残留状态会随应用重置而消失，
// reLaunch 执行前的短暂窗口期内不存在对 store 的读取，因此不会引发 UI 不一致。
function handleUnauthorized() {
  try {
    uni.removeStorageSync('token');
    uni.removeStorageSync('role');
  } catch { /* storage clear failure is non-fatal */ }
  uni.reLaunch({ url: '/pages/login/index' });
}

function request(options = {}) {
  const token = getToken();

  return new Promise((resolve, reject) => {
    uni.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data,
      header: {
        'Content-Type': 'application/json',
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
      },
      success(res) {
        const { statusCode, data } = res;
        if (statusCode === 200) {
          resolve(data);
        } else if (statusCode === 401) {
          handleUnauthorized();
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
