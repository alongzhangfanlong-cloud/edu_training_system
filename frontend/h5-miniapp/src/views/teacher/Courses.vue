<template><div class="page"><van-nav-bar title="课程管理" left-arrow @click-left="$router.back()" fixed placeholder/><div class="content"><van-cell v-for="c in list" :key="c.id" :title="c.name" :label="`${c.subject||''} | ¥${c.unitPrice||0}/课时`"/><van-empty v-if="!list.length" description="暂无课程"/></div></div></template>
<script setup>
import { ref, onMounted } from 'vue'; import request from '../../utils/request'
const list=ref([])
onMounted(async()=>{try{const d=await request.get('/courses',{params:{pageSize:50}});list.value=d?.list||[]}catch(e){}})
</script>
<style scoped>.page{background:#f7f8fa;min-height:100vh}.content{padding:0}</style>
