<template>
  <div class="page">
    <van-nav-bar title="课时明细" left-arrow @click-left="$router.back()" fixed placeholder />
    <div class="content">
      <div class="card">
        <div class="row"><span>总课时</span><b>{{ total }}</b></div>
        <div class="row"><span>已消耗</span><b style="color:#ff4d4f">{{ used }}</b></div>
        <div class="row"><span>剩余</span><b style="color:#1989fa">{{ remain }}</b></div>
      </div>
      <div style="margin-top:16px">
        <h4 style="color:#666;margin-bottom:8px">消课记录</h4>
        <van-cell v-for="tx in transactions" :key="tx.id" :title="tx.reason || `消课${tx.consumeHours}课时`" :label="tx.createdAt">
          <template #right-icon>
            <span style="color:#ff4d4f;font-weight:600">-{{ tx.consumeHours || 0 }}</span>
          </template>
        </van-cell>
        <van-empty v-if="!transactions.length" description="暂无消课记录" />
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'

const total = ref(40); const used = ref(12); const remain = ref(28)
const transactions = ref([])
const studentId = ref(null)

onMounted(async () => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  try {
    const students = await request.get('/students', { params: { keyword: userInfo.phone || '', pageSize: 10 } })
    const student = students?.list?.find(s => s.phone === userInfo.phone)
    if (student) {
      studentId.value = student.id
      const data = await request.get(`/students/${student.id}`)
      total.value = data?.totalHours || 0
      remain.value = data?.remainHours || 0
      used.value = total.value - remain.value
    }
  } catch(e) {}
})
</script>
<style scoped>.page{background:#f7f8fa;min-height:100vh}.content{padding:12px}.card{background:#fff;border-radius:12px;padding:16px}.row{display:flex;justify-content:space-between;padding:8px 0;font-size:15px;border-bottom:1px solid #f5f5f5}b{font-size:18px}</style>
