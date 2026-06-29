import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))
  const isLoggedIn = computed(() => !!token.value)
  const role = computed(() => userInfo.value.role || '')
  const userName = computed(() => userInfo.value.name || '用户')
  function setToken(t) { token.value = t; localStorage.setItem('token', t) }
  function setUser(u) { userInfo.value = u; localStorage.setItem('userInfo', JSON.stringify(u)) }
  function logout() { token.value = ''; userInfo.value = {}; localStorage.clear() }
  return { token, userInfo, isLoggedIn, role, userName, setToken, setUser, logout }
})
