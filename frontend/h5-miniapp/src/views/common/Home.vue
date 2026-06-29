<template><div class="page"><div class="content" style="text-align:center;padding-top:80px"><van-loading type="spinner" /><p style="color:#999;margin-top:16px">加载中...</p></div></div></template>
<script setup>
import { onMounted } from 'vue'; import { useRouter } from 'vue-router'
const router = useRouter()
onMounted(() => {
  const role = JSON.parse(localStorage.getItem('userInfo') || '{}').role
  const home = { boss:'/boss/dashboard', principal:'/principal/dashboard', teacher:'/teacher/dashboard', parent:'/parent/dashboard', student:'/student/dashboard' }
  router.replace(home[role] || '/login')
})
</script>
<style scoped>.page{background:#f7f8fa;min-height:100vh}</style>
