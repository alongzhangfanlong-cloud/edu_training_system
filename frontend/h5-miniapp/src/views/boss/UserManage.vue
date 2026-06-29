<template>
  <div class="page">
    <van-nav-bar title="用户管理" left-text="返回" left-arrow @click-left="$router.back()" fixed placeholder />
    <div class="content">
      <van-cell v-for="u in list" :key="u.id" :title="u.realName" :label="u.username" :value="u.phone" />
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'
const list = ref([])
onMounted(async () => { try { const d = await request.get('/users', { params: { pageSize: 50 } }); list.value = d?.list || [] } catch(e){} })
</script>
<style scoped>.page{background:#f7f8fa;min-height:100vh}.content{padding:0}</style>
