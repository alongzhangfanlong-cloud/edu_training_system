/**
 * ---------------------------------------------
 * 用户权限校验 + Token管理
 * ---------------------------------------------
 */

// 存储在storage中的用户信息key
export const STORAGE_USERINFO_KEY = "USERINFO";

/**
 * 获取用户基本信息
 * 从sessionStorage中读取用户信息并解析返回
 * @returns {Object} 用户信息对象，如果获取失败则返回空对象
 */
const getUserInfo = () => {
  let userInfo = {};

  // 尝试从sessionStorage获取用户信息并解析
  try {
    const info = sessionStorage.getItem(STORAGE_USERINFO_KEY);
    userInfo = JSON.parse(info) || userInfo;
  } catch (error) {
  }

  return userInfo;
};

/** 获取AccessToken */
export const getToken = () => getUserInfo()?.accessToken || "";

/** 获取RefreshToken */
export const getRefreshToken = () => getUserInfo()?.refreshToken || "";

/** 获取用户名 */
export const getUserName = () => getUserInfo()?.username || "";

/** 获取用户名 */
export const getNickName = () => getUserInfo()?.nickname || "";

/** 更新AccessToken（保留其他用户信息） */
export const setToken = (accessToken) => {
  const info = getUserInfo();
  info.accessToken = accessToken;
  sessionStorage.setItem(STORAGE_USERINFO_KEY, JSON.stringify(info));
};

/** 更新RefreshToken（保留其他用户信息） */
export const setRefreshToken = (refreshToken) => {
  const info = getUserInfo();
  info.refreshToken = refreshToken;
  sessionStorage.setItem(STORAGE_USERINFO_KEY, JSON.stringify(info));
};

/** 退出登录 */
export const logout = () => {
  sessionStorage.clear();
  window.location.href = "/login";
};
