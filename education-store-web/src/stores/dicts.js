/**
 * 字典数据 Pinia Store
 * 提供全局字典缓存，支持懒加载和批量加载
 */
import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import request from '@/utils/request';

export const useDictsStore = defineStore('dictsState', () => {
  const _dicts = ref({});
  const loadingDict = {};

  // 从后台请求某个字典数据
  const getDictValue = async (dictName) => {
    if (loadingDict[dictName]) return;
    loadingDict[dictName] = true;

    try {
      const res = await request({
        url: `/api/v1/system/dict/data/type/${dictName}`,
        method: 'get',
      });
      if (res.success) {
        _dicts.value[dictName] =
          (res.data || []).map((i) => ({
            label: i.label,
            value: i.value,
            cssClass: i.cssClass,
            listClass: i.listClass,
          }));
      }
    } catch (e) {
      console.warn(`Failed to load dict: ${dictName}`, e);
    }

    delete loadingDict[dictName];
  };

  // 批量加载字典
  const loadDicts = async (typeCodes) => {
    const uncached = typeCodes.filter((code) => !_dicts.value[code]);
    await Promise.all(uncached.map((code) => getDictValue(code)));
  };

  // 加载单个字典
  const loadDict = (typeCode) => getDictValue(typeCode);

  // 同步获取字典
  const getDictData = (typeCode) => _dicts.value[typeCode] || [];

  // 刷新某个字典
  const freshDict = (dictName) => getDictValue(dictName);

  // Proxy 懒加载访问
  const dicts = computed({
    get() {
      return new Proxy(_dicts.value, {
        get(target, key) {
          // 始终先读取 target[key]，确保 Vue 的响应式系统追踪这个依赖
          // 即使数据还没加载，当异步数据写入时也能触发重新渲染
          const val = target[key];
          if (val === undefined && !(key in target)) {
            getDictValue(key);
            return [];
          }
          return val;
        },
      });
    },
  });

  // 清空所有缓存
  const clearCache = () => {
    _dicts.value = {};
  };

  return { dicts, freshDict, loadDict, loadDicts, getDictData, clearCache };
});
