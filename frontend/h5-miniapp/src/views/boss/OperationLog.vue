<template>
  <div class="page">
    <van-nav-bar title="操作日志" left-arrow @click-left="$router.push('/boss/system')" fixed placeholder />
    <div class="content">
      <van-cell v-for="log in list" :key="log.id">
        <template #title>
          <span style="font-weight:600">{{ log.userName }}</span>
          <van-tag style="margin-left:8px" :type="log.action==='CREATE'?'success':log.action==='DELETE'?'danger':'warning'" size="small">{{ log.action }}</van-tag>
        </template>
        <template #label>
          <div>{{ log.detail || log.target }}</div>
          <div style="font-size:11px;color:#bbb;margin-top:2px">{{ log.module }} · {{ log.ip || '-' }} · {{ log.createdAt }}</div>
        </template>
      </van-cell>
      <van-empty v-if="!list.length" description="暂无操作记录" />
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'

const list = ref([])
onMounted(async () => {
  try {
    const d = await request.get('/operation-logs', { params: { pageSize: 50 } })
    list.value = d?.list || []
  } catch(e) {}
})
</script>
<style scoped>.page{background:#f7f8fa;min-height:100vh}.content{padding:0}</style>
