const BASE_URL = 'http://localhost:8080';

function getToken() {
  try {
    return uni.getStorageSync('token') || '';
  } catch { return ''; }
}

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
