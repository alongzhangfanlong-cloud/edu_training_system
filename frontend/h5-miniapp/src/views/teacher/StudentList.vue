<template>
  <div class="page">
    <van-nav-bar title="学员管理" left-arrow @click-left="$router.back()" fixed placeholder />
    <div class="content"><van-cell v-for="s in list" :key="s.id" :title="s.name" :label="'剩余:'+(s.remainHours||0)+'课时'" /></div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'
const list = ref([])
onMounted(async () => { try { const d = await request.get('/students', { params: { pageSize: 50 } }); list.value = d?.list || [] } catch(e){} })
</script>
<style scoped>.page{background:#f7f8fa;min-height:100vh}.content{padding:0}</style>
