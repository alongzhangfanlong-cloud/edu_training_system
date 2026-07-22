/**
 * 字典 Pinia Store — 硬编码版
 * 原从后台动态加载字典，现改为直接返回常量。保留原有 API 以兼容已有代码。
 */
import { defineStore } from 'pinia';
import { reactive, computed } from 'vue';
import { HARDCODED_DICTS } from '@/utils/dictConstants';
import { BUSINESS_DICTS } from '@/utils/businessConstants';

// 合并全部字典
const ALL_DICTS = { ...HARDCODED_DICTS, ...BUSINESS_DICTS };

export const useDictsStore = defineStore('dictsState', () => {
  const _dicts = reactive({ ...ALL_DICTS });

  // 同步获取字典（保留原有签名）
  const getDictValue = () => Promise.resolve();

  // 批量加载（保留原有签名，无操作）
  const loadDicts = () => Promise.resolve();

  // 加载单个（保留原有签名，无操作）
  const loadDict = () => Promise.resolve();

  // 同步获取
  const getDictData = (typeCode) => _dicts[typeCode] || [];

  // 刷新（保留原有签名，无操作）
  const freshDict = () => Promise.resolve();

  // 清空缓存（保留原有签名，无操作）
  const clearCache = () => {};

  // 兼容原有 computed proxy 用法
  const dicts = computed(() => _dicts);

  return { dicts, freshDict, loadDict, loadDicts, getDictData, clearCache };
});
