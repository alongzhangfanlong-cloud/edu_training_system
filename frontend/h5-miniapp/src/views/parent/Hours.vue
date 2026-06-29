<template>
  <div class="page">
    <van-nav-bar title="课时查询" left-arrow @click-left="$router.back()" fixed placeholder />
    <div class="content">
      <van-field v-model="studentId" label="学员ID" type="digit" placeholder="输入学员ID" />
      <van-button type="primary" size="small" style="margin:8px 0" @click="loadHours">查询</van-button>
      <div class="card" style="text-align:center">
        <div class="label">剩余课时</div>
        <div class="big">{{ remain }} <span class="unit">课时</span></div>
        <van-progress :percentage="pct" stroke-color="#1989fa" />
        <div class="sub">已用 {{ used }} / 总计 {{ total }}</div>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, computed } from 'vue'
import request from '../../utils/request'

const studentId = ref('')
const total = ref(0); const used = ref(0); const remain = ref(0)
const pct = computed(() => total.value > 0 ? Math.round(used.value / total.value * 100) : 0)

async function loadHours() {
  if (!studentId.value) return
  try {
    const data = await request.get(`/students/${studentId.value}`)
    total.value = data?.totalHours || 0
    remain.value = data?.remainHours || 0
    used.value = total.value - remain.value
  } catch(e) {}
}
</script>
<style scoped>.page{background:#f7f8fa;min-height:100vh}.content{padding:12px}.card{background:#fff;border-radius:16px;padding:24px;margin-top:12px}.label{font-size:14px;color:#999}.big{font-size:52px;font-weight:700;color:#1989fa}.unit{font-size:16px;color:#999}.sub{font-size:13px;color:#999;margin-top:8px}</style>
