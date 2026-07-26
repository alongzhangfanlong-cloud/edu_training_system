/**
 * ---------------------------------------------
 * 请求封装
 *    统一返回
 *    {
 *      success: boolean,
 *      data: any,
 *      msg: string
 *    }
 *    不返回错误信息,错误信息由全局错误处理统一处理
 * ---------------------------------------------
 */

import axios from "axios";
import { getToken, getRefreshToken, setToken, setRefreshToken, logout } from "./validate";
import { refreshToken as refreshTokenApi } from "@/api/auth/login";

// 错误消息映射配置
const errorMessages = {
  // 网络相关错误
  NETWORK_ERROR: "网络连接异常，请检查网络设置",
  TIMEOUT_ERROR: "请求超时，请稍后重试",

  // HTTP状态码错误
  400: "请求参数错误",
  401: "登录已过期，请重新登录",
  403: "没有权限访问该资源",
  404: "请求的资源不存在",
  405: "请求方法不被允许",
  408: "请求超时",
  500: "服务器内部错误",
  502: "网关错误",
  503: "服务不可用",
  504: "网关超时",

  // 默认错误
  DEFAULT: "请求失败，请稍后重试"
};

// ============= Token 刷新队列机制 =============

/** 是否正在刷新Token */
let isRefreshing = false;

/** 等待刷新的请求队列 */
let refreshSubscribers = [];

/**
 * 订阅Token刷新完成
 * @param {Function} callback 刷新完成后回调，接收 newToken
 */
function subscribeTokenRefresh(callback) {
  refreshSubscribers.push(callback);
}

/**
 * Token刷新完成，重放所有等待的请求
 * @param {string|null} newToken 新Token，null表示刷新失败
 */
function onTokenRefreshed(newToken) {
  refreshSubscribers.forEach(cb => cb(newToken));
  refreshSubscribers = [];
}

// ============= Axios 实例 =============

// 创建Axios实例
const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_PREFIX || "", // 基础URL，根据环境变量设置
  timeout: 1000 * 30 // 默认超时时间30s
});

// 添加请求拦截器
axiosInstance.interceptors.request.use(
  function (config) {
    const token = getToken();

    // 如果 token 存在且未标记忽略Token，添加到请求头
    if (token && !config.ignoreToken) {
      config.headers = config.headers || {};
      config.headers.Authorization = `Bearer ${token}`;
    }

    // 重要：如果是 FormData，不要设置 Content-Type，让浏览器自动设置
    if (config.data instanceof FormData) {
      // 删除可能手动设置的 Content-Type
      delete config.headers["Content-Type"];
    }

    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

// 响应拦截器：统一处理 401 跳转与返回 data
axiosInstance.interceptors.response.use(
  response => {
    // Blob 响应（文件下载）原样返回，不做 JSON 转换
    if (response.config?.responseType === 'blob') {
      return response;
    }
    // token校验失败（响应体 code 为 401）- 尝试刷新Token，与HTTP 401处理一致
    if (response?.data?.code === 401) {
      if (!response.config?.__isRefreshRequest && isRefreshTokenAvailable()) {
        return retryRequest(response.config);
      }
      logout();
    }
    // 默认返回 data，调用方若需要完整响应可通过 service.request 获取
    if (response && Object.prototype.hasOwnProperty.call(response, "data")) {
      const result = {
        ...response.data,
        // 统一 success 字段，code 200 视为成功
        success: response.data?.code === 200,
        // 统一 msg 字段（后端用 message，前端统一用 msg）
        msg: response.data?.message
      };
      // 统一分页响应格式：MyBatis-Plus IPage 返回 records/total，同时提供 rows 别名
      if (result.data && typeof result.data === 'object' && 'records' in result.data && !('rows' in result.data)) {
        result.data.rows = result.data.records;
      }
      return result;
    } else {
      return {
        ...(response || {}),
        success: false,
        msg: '请求失败'
      };
    }
  },
  async error => {
    const { config, response } = error || {};
    const status = response?.status;

    // ============= 401 自动刷新Token =============
    // 不是刷新请求本身，且有 refreshToken，尝试刷新
    if (status === 401 && !config?.__isRefreshRequest) {
      const refreshTokenValue = getRefreshToken();

      if (!refreshTokenValue) {
        // 没有 refreshToken，直接退出
        logout();
        return Promise.reject(error);
      }

      // 如果正在刷新中，将当前请求加入等待队列
      if (isRefreshing) {
        return new Promise((resolve) => {
          subscribeTokenRefresh(newToken => {
            if (newToken) {
              config.headers.Authorization = `Bearer ${newToken}`;
              resolve(axiosInstance(config));
            } else {
              resolve({ success: false, data: null, msg: "登录已过期" });
            }
          });
        });
      }

      // 发起刷新
      isRefreshing = true;

      try {
        const refreshRes = await refreshTokenApi(refreshTokenValue);
        if (refreshRes?.success && refreshRes?.data?.accessToken) {
          const newAccessToken = refreshRes.data.accessToken;
          const newRefreshToken = refreshRes.data.refreshToken;

          // 更新本地存储
          setToken(newAccessToken);
          if (newRefreshToken) {
            setRefreshToken(newRefreshToken);
          }

          // 通知所有等待的请求
          onTokenRefreshed(newAccessToken);

          // 重试当前请求
          config.headers.Authorization = `Bearer ${newAccessToken}`;
          return axiosInstance(config);
        } else {
          // 刷新失败
          onTokenRefreshed(null);
          logout();
          return Promise.reject(error);
        }
      } catch (refreshError) {
        // 刷新请求异常
        onTokenRefreshed(null);
        logout();
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }

    // 刷新请求本身返回401，直接退出
    if (status === 401 && config?.__isRefreshRequest) {
      logout();
      return Promise.reject(error);
    }

    // ============= 其他错误统一处理 =============
    let errorMessage = errorMessages.DEFAULT;

    if (error?.response) {
      // 服务器响应错误（4xx, 5xx）
      errorMessage = errorMessages[status] || `请求错误（${status}）`;

      if (status === 401) {
        // 走到这里说明没有refreshToken或就是刷新请求本身
        errorMessage = "登录已过期，请重新登录";
      }
    } else if (error?.code === "ECONNABORTED" || error?.message?.includes("timeout")) {
      // 超时错误
      errorMessage = errorMessages.TIMEOUT_ERROR;
    } else if (error?.request) {
      // 网络错误（无响应）
      errorMessage = errorMessages.NETWORK_ERROR;
    }

    return {
      success: false,
      data: null,
      msg: errorMessage,
      message: errorMessage,
      error
    };
  }
);

// 通用 request 函数：支持 request(config)
const request = config => {
  return axiosInstance.request(config);
};

export default request;

/**
 * 下载文件（blob 响应）
 * 自动设置 responseType: 'blob'，返回原始 axios response（含 data/headers）
 * 走完整拦截器链（Token 刷新、错误处理）
 *
 * @param {object} config — axios 配置（url, method, params 等，不需要设置 responseType）
 * @returns {Promise} 原始 axios 响应，res.data 为 Blob，res.headers 含 Content-Disposition
 */
export function downloadBlob(config) {
  return request({ ...config, responseType: 'blob' });
}
