<template>
  <div class="page">
    <van-nav-bar title="报名缴费" left-arrow @click-left="$router.back()" fixed placeholder />
    <div class="content">
      <van-cell-group inset>
        <van-cell v-for="o in orders" :key="o.id" :title="o.orderNo" :label="o.createdAt">
          <template #value><span :style="{color:o.payStatus===1?'#52c41a':'#faad14'}">¥{{ o.payAmount||0 }}</span></template>
          <template #right-icon><van-tag :type="o.payStatus===1?'success':'warning'" size="small">{{ o.payStatus===1?'已付':'待付' }}</van-tag></template>
        </van-cell>
      </van-cell-group>
      <van-empty v-if="!orders.length" description="暂无报名订单" />
      <p style="color:#999;font-size:12px;text-align:center;padding:12px">新建报名请前往PC管理后台</p>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'

const orders = ref([])
onMounted(async () => {
  try {
    const data = await request.get('/orders', { params: { pageSize: 20 } })
    orders.value = data?.list || []
  } catch(e) {}
})
</script>
<style scoped>.page{background:#f7f8fa;min-height:100vh}.content{padding:12px}</style>
