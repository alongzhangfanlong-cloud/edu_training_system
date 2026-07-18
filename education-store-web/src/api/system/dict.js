/**
 * 字典管理 API
 */
import request from '@/utils/request';

// ========== 字典类型 ==========

// 获取字典类型列表(分页)
export function listType(query) {
  return request({ url: '/api/v1/system/dict/type/list', method: 'get', params: query });
}

// 获取字典类型详情
export function getType(dictId) {
  return request({ url: `/api/v1/system/dict/type/${dictId}`, method: 'get' });
}

// 新增字典类型
export function addType(data) {
  return request({ url: '/api/v1/system/dict/type', method: 'post', data });
}

// 修改字典类型
export function updateType(data) {
  return request({ url: '/api/v1/system/dict/type', method: 'put', data });
}

// 删除字典类型
export function delType(dictIds) {
  return request({ url: `/api/v1/system/dict/type/${dictIds}`, method: 'delete' });
}

// 获取字典类型选择框列表
export function optionselect() {
  return request({ url: '/api/v1/system/dict/type/optionselect', method: 'get' });
}

// 刷新字典缓存
export function refreshCache() {
  return request({ url: '/api/v1/system/dict/type/cache/refresh', method: 'post' });
}

// ========== 字典数据 ==========

// 获取字典数据列表(根据字典类型查询)
export function listData(query) {
  return request({ url: '/api/v1/system/dict/data/list', method: 'get', params: query });
}

// 获取字典数据详情
export function getData(dictCode) {
  return request({ url: `/api/v1/system/dict/data/${dictCode}`, method: 'get' });
}

// 根据字典类型查询字典数据
export function listDataByType(dictType) {
  return request({ url: `/api/v1/system/dict/data/type/${dictType}`, method: 'get' });
}

// 新增字典数据
export function addData(data) {
  return request({ url: '/api/v1/system/dict/data', method: 'post', data });
}

// 修改字典数据
export function updateData(data) {
  return request({ url: '/api/v1/system/dict/data', method: 'put', data });
}

// 删除字典数据
export function delData(dictCodes) {
  return request({ url: `/api/v1/system/dict/data/${dictCodes}`, method: 'delete' });
}
