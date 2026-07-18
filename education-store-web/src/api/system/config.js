/**
 * 系统参数配置 API
 */
import request from '@/utils/request';

// 获取参数配置列表(分页)
export function listConfig(query) {
  return request({ url: '/api/v1/system/config/list', method: 'get', params: query });
}

// 获取参数配置详情
export function getConfig(configId) {
  return request({ url: `/api/v1/system/config/${configId}`, method: 'get' });
}

// 根据参数键名查询配置
export function getConfigKey(configKey) {
  return request({ url: `/api/v1/system/config/configKey/${configKey}`, method: 'get' });
}

// 新增参数配置
export function addConfig(data) {
  return request({ url: '/api/v1/system/config', method: 'post', data });
}

// 修改参数配置
export function updateConfig(data) {
  return request({ url: '/api/v1/system/config', method: 'put', data });
}

// 删除参数配置
export function delConfig(configIds) {
  return request({ url: `/api/v1/system/config/${configIds}`, method: 'delete' });
}

// 刷新参数缓存
export function refreshCache() {
  return request({ url: '/api/v1/system/config/refreshCache', method: 'post' });
}
