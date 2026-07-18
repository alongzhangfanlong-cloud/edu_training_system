/**
 * 用户信息 Pinia Store — 对齐后端 API（单机构，无多公司/部门）
 */
import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { login, logout as logoutApi } from '@/api/auth/login';
import { getInfo, getRouters } from '@/api/system/user';
import { setToken, setRefreshToken, getToken } from '@/utils/validate';

export const useUserStore = defineStore('user', () => {
  const token = ref('');
  const id = ref(null);
  const username = ref('');
  const realName = ref('');
  const avatar = ref('');
  const roles = ref([]);
  const permissions = ref([]);

  const isAdmin = computed(() => roles.value.includes('admin'));
  const hasPermission = (perm) => roles.value.includes('admin') || permissions.value.includes(perm);

  // 登录（一步式）
  const doLogin = async (loginForm) => {
    const res = await login(loginForm);
    if (res.code !== 200) return res;
    const data = res.data;
    token.value = data.accessToken;
    setToken(data.accessToken);
    if (data.refreshToken) setRefreshToken(data.refreshToken);
    await loadUserInfo();
    return res;
  };

  // 加载用户信息
  const loadUserInfo = async () => {
    // 确保 token 从 sessionStorage 恢复
    if (!token.value) {
      token.value = getToken();
    }
    const res = await getInfo();
    if (res.code === 200 && res.data) {
      const user = res.data.user || res.data;
      id.value = user.id;
      username.value = user.username;
      realName.value = user.realName;
      avatar.value = user.avatar;
      roles.value = res.data.roles || [];
      permissions.value = res.data.permissions || [];
    }
  };

  // 加载路由菜单
  const loadRouters = async () => {
    const res = await getRouters();
    if (res.code === 200) {
      return res.data || [];
    }
    return [];
  };

  const logout = async () => {
    try { await logoutApi(); } catch (e) { /* ignore */ }
    resetState();
  };

  const resetState = () => {
    token.value = '';
    id.value = null;
    username.value = '';
    realName.value = '';
    avatar.value = '';
    roles.value = [];
    permissions.value = [];
    sessionStorage.clear();
  };

  return {
    token, id, username, realName, avatar,
    roles, permissions, isAdmin, hasPermission,
    doLogin, loadUserInfo, loadRouters, logout, resetState,
  };
});
