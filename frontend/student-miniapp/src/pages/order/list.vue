<template>
  <view class="container">
    <view v-if="loading" class="text-center text-muted">加载中...</view>
    <view v-else-if="orders.length === 0" class="empty text-muted">暂无订单记录</view>
    <view v-for="order in orders" :key="order.id" class="order-card card">
      <view class="flex-between">
        <text class="order-no">订单号: {{ order.orderNo }}</text>
        <text :class="['pay-tag', order.payStatus === 1 ? 'paid' : 'unpaid']">
          {{ order.payStatus === 1 ? '已支付' : '未支付' }}
        </text>
      </view>
      <view class="order-items">
        <view v-for="item in (order.items || [])" :key="item.id" class="order-item">
          <text>{{ item.courseName || '课程' }} - {{ item.hours }}课时</text>
          <text class="text-primary">¥{{ item.totalPrice }}</text>
        </view>
      </view>
      <view class="flex-between order-footer">
        <text>实付: <text class="price">¥{{ order.payAmount }}</text></text>
        <text class="text-muted">{{ order.createdAt }}</text>
      </view>
    </view>
  </view>
</template>

<script>
import { getOrderList } from '../../api/order'
import { getUserStore } from '../../store/user'

export default {
  data() {
    return { loading: false, orders: [], page: 1 }
  },
  onLoad() { this.loadOrders() },
  methods: {
    async loadOrders() {
      this.loading = true
      try {
        const store = getUserStore()
        const res = await getOrderList({ studentId: store.state.userInfo?.id, page: this.page, pageSize: 20 })
        if (res) this.orders = res.list || []
      } catch(e) { console.error(e) } finally { this.loading = false }
    }
  }
}
</script>

<style scoped>
.order-card { }
.order-no { font-size: 24rpx; color: #666; }
.pay-tag { font-size: 22rpx; padding: 2rpx 12rpx; border-radius: 4rpx; }
.pay-tag.paid { background: #f6ffed; color: #52c41a; }
.pay-tag.unpaid { background: #fff2f0; color: #ff4d4f; }
.order-items { margin: 12rpx 0; }
.order-item { display: flex; justify-content: space-between; padding: 6rpx 0; font-size: 26rpx; }
.order-footer { margin-top: 12rpx; font-size: 24rpx; }
.price { font-size: 30rpx; font-weight: 600; color: #ff4d4f; }
.empty { text-align: center; padding: 80rpx; }
.text-center { text-align: center; padding: 80rpx; }
</style>
