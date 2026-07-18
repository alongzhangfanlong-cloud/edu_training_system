/**
 * ---------------------------------------------
 * 侧边栏菜单状态
 * ---------------------------------------------
 */

import { defineStore } from "pinia";
import { ref } from "vue";

export default defineStore("siderState", () => {
  // 侧边栏收起状态
  const siderIsCollapsed = ref(getSiderIsCollapsed());

  // 侧边栏切换
  const toggleSiderCollapsed = () => {
    siderIsCollapsed.value = !siderIsCollapsed.value;
    setSiderIsCollapsed(siderIsCollapsed.value);
  };

  return { siderIsCollapsed, toggleSiderCollapsed };
});

// ## 使用的方法
// #region -------------------------------------------------------
// 侧边栏状态名
const SIDER_COLLAPSED_STATE_NAME = "SIDER_COLLAPSED_STATE_NAME";

// 获取侧边栏是否折叠
const getSiderIsCollapsed = () => {
  return localStorage.getItem(SIDER_COLLAPSED_STATE_NAME) === "true";
};

// 设置侧边栏是否折叠
const setSiderIsCollapsed = isCollapsed => {
  localStorage.setItem(SIDER_COLLAPSED_STATE_NAME, isCollapsed);
};
// #endregion  --------------------------
