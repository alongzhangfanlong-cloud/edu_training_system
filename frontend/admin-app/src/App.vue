<template>
  <el-container class="app-container">
    <el-aside v-if="!isLoginRoute" width="220px" class="sidebar">
      <div class="logo"><h2>📚 教务管理系统</h2><span class="version">v1.0</span></div>
      <el-menu :default-active="currentRoute" router :key="menuKey" background-color="#001529" text-color="#ffffffb3" active-text-color="#fff">
        <template v-for="menu in menuTree" :key="menu.id">
          <el-sub-menu v-if="menu.children && menu.children.length" :index="String(menu.id)">
            <template #title><span>{{ menu.menuName }}</span></template>
            <el-menu-item v-for="child in menu.children" :key="child.id" :index="child.menuPath">
              {{ child.menuName }}
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item v-else :index="menu.menuPath">{{ menu.menuName }}</el-menu-item>
        </template>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header v-if="!isLoginRoute" class="header">
        <div class="flex-between" style="width:100%">
          <h3>{{ pageTitle }}</h3>
          <el-dropdown @command="handleCommand">
            <span class="user-info"><el-avatar :size="32" icon="UserFilled" /><span class="ml-8">{{ userName }}</span></span>
            <template #dropdown>
              <el-dropdown-menu><el-dropdown-item command="logout">退出登录</el-dropdown-item></el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main-content"><router-view /></el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from './store/user'
import request from './utils/request'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const menuTree = ref([])
const menuKey = ref(0)

const isLoginRoute = computed(() => route.path === '/login')
const currentRoute = computed(() => route.path)
const pageTitle = computed(() => route.meta?.title || '')
const userName = computed(() => userStore.userName || '管理员')

// 加载菜单：组件挂载时 + 登录后路由变化时
async function loadMenus() {
  const token = localStorage.getItem('token')
  if (!token || route.path === '/login') return
  try {
    menuTree.value = await request.get('/menus/tree') || []
    menuKey.value++
  } catch(e) {}
}

// 监听路由变化 — 首次加载非登录页 或 从登录页跳转时自动加载菜单
watch(() => route.path, (newPath, oldPath) => {
  if (newPath !== '/login' && (oldPath === '/login' || oldPath === undefined)) {
    loadMenus()
  }
}, { immediate: true })

function handleCommand(cmd) {
  if (cmd === 'logout') { userStore.logout(); router.push('/login') }
}
</script>

<style>
* { margin: 0; padding: 0; box-sizing: border-box; }
.app-container { height: 100vh; }
.sidebar { background: #001529; overflow-y: auto; flex-shrink: 0; }
.logo { padding: 20px; text-align: center; color: #fff; }
.logo h2 { font-size: 18px; }
.version { font-size: 12px; color: #ffffff80; }
.header { background: #fff; border-bottom: 1px solid #e8e8e8; display: flex; align-items: center; padding: 0 24px; height: 56px; flex-shrink: 0; }
.user-info { cursor: pointer; display: flex; align-items: center; }
.ml-8 { margin-left: 8px; }
.main-content { background: #f0f2f5; min-height: calc(100vh - 56px); }
.flex-between { display: flex; justify-content: space-between; align-items: center; }
</style>
