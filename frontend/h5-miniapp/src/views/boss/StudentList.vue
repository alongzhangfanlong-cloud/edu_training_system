<template><div class="page"><van-nav-bar title="学员管理" left-arrow @click-left="$router.push('/boss/jiaowu')" fixed placeholder/><div class="content"><van-cell v-for="s in list" :key="s.id" :title="s.name" :label="`${s.studentNo} | ${s.grade||''} | ${s.phone||''}`" :value="`${s.remainHours||0}课时`"/><van-empty v-if="!list.length" description="暂无学员"/></div></div></template>
<script setup>
import { ref, onMounted } from 'vue'; import request from '../../utils/request'
const list=ref([])
onMounted(async()=>{try{const d=await request.get('/students',{params:{pageSize:50}});list.value=d?.list||[]}catch(e){}})
</script>
<style scoped>.page{background:#f7f8fa;min-height:100vh}.content{padding:0}</style>
