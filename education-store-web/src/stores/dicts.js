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

  // 非字典编码的内部属性名，Proxy 遇到这些 key 时不触发懒加载
  const INTERNAL_KEYS = new Set([
    // Object.prototype 标准属性
    'constructor', 'toString', 'toLocaleString', 'valueOf',
    'hasOwnProperty', 'isPrototypeOf', 'propertyIsEnumerable',
    // JSON
    'toJSON',
  ]);

  const isValidDictKey = (key) => {
    if (typeof key !== 'string') return false;       // Symbol 一律不过
    if (key.startsWith('__v_')) return false;         // Vue 3 内部 (__v_isRef, __v_raw 等)
    if (key.startsWith('__')) return false;           // 其他双下划线内部属性
    if (INTERNAL_KEYS.has(key)) return false;         // JS 标准属性
    return true;
  };

  // Proxy 懒加载：访问即加载，但过滤 Vue/JS 内部属性
  const dicts = computed({
    get() {
      return new Proxy(_dicts.value, {
        get(target, key) {
          const val = target[key];
          if (val === undefined && !(key in target) && isValidDictKey(key)) {
            getDictValue(key);
            return [];
          }
          return val ?? [];
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
