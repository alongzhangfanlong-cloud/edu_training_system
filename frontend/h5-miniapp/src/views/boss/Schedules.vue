<template><div class="page"><van-nav-bar title="排课管理" left-arrow @click-left="$router.push('/boss/jiaowu')" fixed placeholder/><div class="content"><van-cell v-for="s in list" :key="s.id" :title="s.courseName||'课程#'+s.courseId" :label="`${s.date} ${s.startTime}-${s.endTime} | ${s.className||''} | ${s.teacherName||''}`"/><van-empty v-if="!list.length" description="暂无排课"/></div></div></template>
<script setup>
import { ref, onMounted } from 'vue'; import request from '../../utils/request'
const list=ref([])
onMounted(async()=>{try{const d=await request.get('/schedules',{params:{startDate:new Date().toISOString().slice(0,10),endDate:new Date(Date.now()+7*86400000).toISOString().slice(0,10)}});list.value=d?.schedules||[]}catch(e){}})
</script>
<style scoped>.page{background:#f7f8fa;min-height:100vh}.content{padding:0}</style>
