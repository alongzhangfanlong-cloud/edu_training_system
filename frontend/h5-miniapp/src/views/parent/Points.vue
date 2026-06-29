<template>
  <div class="page">
    <van-nav-bar title="积分商城" left-arrow @click-left="$router.back()" fixed placeholder />
    <div class="content">
      <van-field v-model="studentId" label="学员ID" type="digit" placeholder="输入学员ID" />
      <van-button type="primary" size="small" style="margin:8px 0" @click="loadData">查询</van-button>
      <div class="points-header" v-if="loaded || points">
        🏆 当前积分: <b style="font-size:32px;color:#1989fa">{{ points }}</b>
      </div>

      <!-- 积分流水 -->
      <h4 style="color:#666;margin:12px 0 8px">积分明细</h4>
      <van-cell v-for="tx in transactions" :key="tx.id" :title="tx.reason">
        <template #right-icon>
          <span :style="{ color: tx.changeAmount > 0 ? '#52c41a' : '#ff4d4f', fontWeight: 600 }">
            {{ tx.changeAmount > 0 ? '+' : '' }}{{ tx.changeAmount }}
          </span>
        </template>
        <template #label>{{ tx.createdAt }}</template>
      </van-cell>
      <van-empty v-if="!transactions.length" description="暂无积分记录" />

      <!-- 兑换区 -->
      <h4 style="color:#666;margin:16px 0 8px">积分兑换</h4>
      <van-grid :column-num="2" gutter="12">
        <van-grid-item v-for="g in goods" :key="g.name" :icon="g.icon" :text="g.name" :label="`${g.price}积分`" @click="exchange(g)" />
      </van-grid>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { showToast, showDialog } from 'vant'
import request from '../../utils/request'

const points = ref(0)
const transactions = ref([])
const studentId = ref('')
const loaded = ref(false)
const goods = ref([
  { name: '文具礼包', icon: 'gift-o', price: 200 },
  { name: '课外读物', icon: 'bookmark-o', price: 500 },
  { name: '体验课1节', icon: 'certificate', price: 800 },
  { name: '定制书包', icon: 'bag-o', price: 1200 }
])

onMounted(() => {})

async function loadData() {
  if (!studentId.value) return
  loaded.value = true
  try {
    const data = await request.get(`/points/student/${studentId.value}`)
    points.value = data?.balance || 0
  } catch(e) {}
  try {
    const data = await request.get(`/points/student/${studentId.value}/transactions`)
    transactions.value = data?.list || []
  } catch(e) {}
}

async function exchange(g) {
  try {
    await showDialog({ title: '确认兑换', message: `确定使用${g.price}积分兑换${g.name}？` })
    if (points.value < g.price) return showToast('积分不足')
    await request.post('/points/change', {
      studentId: studentId.value,
      changeAmount: -g.price,
      reason: `兑换: ${g.name}`
    })
    showToast('兑换成功')
    loadData()
  } catch(e) {}
}
</script>
<style scoped>.page{background:#f7f8fa;min-height:100vh}.content{padding:12px}.points-header{text-align:center;padding:20px;background:#fff;border-radius:12px;margin-bottom:12px;font-size:16px}</style>
