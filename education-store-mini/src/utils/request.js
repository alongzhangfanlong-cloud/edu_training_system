const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

function getToken() {
  try {
    return uni.getStorageSync('token') || '';
  } catch { return ''; }
}

function handleUnauthorized() {
  try {
    uni.removeStorageSync('token');
    uni.removeStorageSync('role');
    uni.removeStorageSync('currentChildId');
  } catch { /* storage clear failure is non-fatal */ }
  const pages = getCurrentPages();
  const currentPage = pages[pages.length - 1];
  if (currentPage && currentPage.route !== 'pages/login/index') {
    uni.reLaunch({ url: '/pages/login/index' });
  }
}

function buildUrlWithParams(url, params) {
  if (!params || Object.keys(params).length === 0) return url;
  const query = Object.entries(params)
    .filter(([, v]) => v !== undefined && v !== null && v !== '')
    .map(([k, v]) => `${encodeURIComponent(k)}=${encodeURIComponent(v)}`)
    .join('&');
  return url.includes('?') ? `${url}&${query}` : `${url}?${query}`;
}

function request(options = {}) {
  const token = getToken();
  const method = (options.method || 'GET').toUpperCase();

  let requestUrl = BASE_URL + options.url;
  let requestData = options.data;

  if (method === 'GET' && options.data) {
    requestUrl = buildUrlWithParams(BASE_URL + options.url, options.data);
    requestData = undefined;
  }

  return new Promise((resolve, reject) => {
    uni.request({
      url: requestUrl,
      method,
      data: requestData,
      header: {
        'Content-Type': 'application/json',
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
        ...(options.header || {}),
      },
      success(res) {
        const { statusCode, data } = res;
        if (statusCode === 200) {
          if (data && typeof data === 'object' && 'code' in data) {
            const code = data.code;
            if (code === 200 || code === 0 || code === '200' || code === '0') {
              resolve(data);
            } else if (code === 401 || code === 403) {
              handleUnauthorized();
              reject(new Error(data.msg || data.message || '登录已过期'));
            } else {
              const msg = data.msg || data.message || '请求失败';
              if (!options.silent) {
                uni.showToast({ title: msg, icon: 'none' });
              }
              reject(new Error(msg));
            }
          } else {
            resolve(data);
          }
        } else if (statusCode === 401 || statusCode === 403) {
          handleUnauthorized();
          reject(new Error('登录已过期'));
        } else {
          const msg = (data && (data.msg || data.message)) || `HTTP ${statusCode}`;
          if (!options.silent) {
            uni.showToast({ title: msg, icon: 'none' });
          }
          reject(new Error(msg));
        }
      },
      fail(err) {
        if (!options.silent) {
          uni.showToast({ title: '网络请求失败', icon: 'none' });
        }
        reject(err);
      },
    });
  });
}

export default request;
