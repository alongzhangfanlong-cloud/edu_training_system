<template>
  <div class="page">
    <van-nav-bar title="财务管理" left-arrow @click-left="$router.back()" fixed placeholder />
    <div class="content">
      <van-grid :column-num="3">
        <van-grid-item text="今日收入"><template #default><div class="sv">¥{{ summary.todayRevenue || '0' }}</div></template></van-grid-item>
        <van-grid-item text="本月收入"><template #default><div class="sv">¥{{ summary.monthRevenue || '0' }}</div></template></van-grid-item>
        <van-grid-item text="待支付"><template #default><div class="sv">{{ summary.pendingCount || 0 }}笔</div></template></van-grid-item>
      </van-grid>
      <h4 style="color:#666;margin:16px 0 8px">最近订单</h4>
      <van-cell v-for="o in orders" :key="o.id" :title="`${o.orderNo}`" :label="o.createdAt">
        <template #value><span :style="{color: o.payStatus===1?'#52c41a':'#faad14'}">¥{{ o.payAmount||0 }}</span></template>
        <template #right-icon><van-tag :type="o.payStatus===1?'success':'warning'" size="small">{{ o.payStatus===1?'已付':'待付' }}</van-tag></template>
      </van-cell>
      <van-empty v-if="!orders.length" description="暂无订单" />
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'

const summary = ref({ todayRevenue: 0, monthRevenue: 0, pendingCount: 0 })
const orders = ref([])

onMounted(async () => {
  try { summary.value = await request.get('/finance/summary') || {} } catch(e) {}
  try {
    const data = await request.get('/orders', { params: { pageSize: 5 } })
    orders.value = data?.list || []
  } catch(e) {}
})
</script>
<style scoped>.page{background:#f7f8fa;min-height:100vh}.content{padding:12px}.sv{font-size:20px;font-weight:700;color:#ee0a24}</style>
