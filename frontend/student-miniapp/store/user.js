import { reactive } from 'vue'

// 简易状态管理 (类Pinia)
export const useUserStore = () => {
  const state = reactive({
    userInfo: null,
    isLoggedIn: false,
    role: '',
    currentChild: null, // 家长端当前选中的孩子
    children: []        // 家长端孩子列表
  })

  const setUser = (user) => {
    state.userInfo = user
    state.isLoggedIn = true
    state.role = user.role
  }

  const logout = () => {
    state.userInfo = null
    state.isLoggedIn = false
    state.role = ''
    state.currentChild = null
    state.children = []
  }

  const setChildren = (children) => {
    state.children = children
    if (children.length > 0 && !state.currentChild) {
      state.currentChild = children[0]
    }
  }

  const switchChild = (child) => {
    state.currentChild = child
  }

  return { state, setUser, logout, setChildren, switchChild }
}

// 全局单例
let storeInstance = null
export const getUserStore = () => {
  if (!storeInstance) {
    storeInstance = useUserStore()
  }
  return storeInstance
}
