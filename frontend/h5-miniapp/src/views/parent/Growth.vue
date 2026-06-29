<template>
  <div class="page">
    <van-nav-bar title="成长档案" left-arrow @click-left="$router.back()" fixed placeholder />
    <div class="content">
      <van-field v-model="studentId" label="学员ID" type="digit" placeholder="输入学员ID查看" />
      <van-button type="primary" size="small" style="margin:8px 0" @click="loadRecords">查询</van-button>
      <div v-for="r in records" :key="r.id" class="card">
        <div class="title">📝 {{ r.recordDate }}</div>
        <div class="text">{{ r.content }}</div>
        <div class="score">课堂表现: {{ '★'.repeat(r.score||0) }}{{ '☆'.repeat(5-(r.score||0)) }}</div>
      </div>
      <van-empty v-if="!records.length && loaded" description="暂无成长记录" />
    </div>
  </div>
</template>
<script setup>
import { ref } from 'vue'
import request from '../../utils/request'

const studentId = ref('')
const records = ref([])
const loaded = ref(false)

async function loadRecords() {
  if (!studentId.value) return
  try {
    const data = await request.get('/growth-records', { params: { studentId: studentId.value } })
    records.value = data?.list || []
    loaded.value = true
  } catch(e) {}
}
</script>
<style scoped>.page{background:#f7f8fa;min-height:100vh}.content{padding:12px}.card{background:#fff;border-radius:12px;padding:16px;margin-bottom:12px}.title{font-size:15px;font-weight:600;margin-bottom:4px}.text{font-size:14px;color:#666;line-height:1.6}.score{font-size:16px;color:#faad14;margin-top:6px}</style>
