import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))

  const isLoggedIn = computed(() => !!token.value)
  const userName = computed(() => userInfo.value.name || '管理员')

  function setToken(t) { token.value = t; localStorage.setItem('token', t) }
  function setUser(user) { userInfo.value = user; localStorage.setItem('userInfo', JSON.stringify(user)) }
  function logout() { token.value = ''; userInfo.value = {}; localStorage.clear() }

  return { token, userInfo, isLoggedIn, userName, setToken, setUser, logout }
})
