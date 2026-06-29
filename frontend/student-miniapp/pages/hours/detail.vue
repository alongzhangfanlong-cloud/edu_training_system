<template>
  <view class="container">
    <view class="card">
      <text class="title">课时明细</text>
      <!-- 课时汇总 -->
      <view v-if="summary" class="summary-row">
        <view class="summary-item"><text class="s-val text-primary">{{ summary.totalHours }}</text><text>总课时</text></view>
        <view class="summary-item"><text class="s-val text-warning">{{ summary.usedHours }}</text><text>已用</text></view>
        <view class="summary-item"><text class="s-val text-success">{{ summary.remainHours }}</text><text>剩余</text></view>
      </view>
    </view>

    <!-- 课时变动记录 -->
    <view class="card">
      <text class="section-title">变动记录</text>
      <view v-if="loading" class="text-center text-muted">加载中...</view>
      <view v-else-if="transactions.length === 0" class="text-center text-muted">暂无记录</view>
      <view v-for="tx in transactions" :key="tx.id" class="tx-item">
        <view class="flex-between">
          <view class="flex-row">
            <text :class="['tx-type', tx.changeHours > 0 ? 'increase' : 'decrease']">
              {{ tx.changeHours > 0 ? '+' : '' }}{{ tx.changeHours }}
            </text>
            <view class="ml-12">
              <text class="tx-desc">{{ tx.typeName }}</text>
              <text class="tx-class text-muted">{{ tx.className }}</text>
            </view>
          </view>
          <text class="tx-balance">余额: {{ tx.afterHours }}</text>
        </view>
        <text class="tx-date text-muted">{{ tx.createdAt }}</text>
      </view>
      <view v-if="hasMore" class="load-more text-primary" @click="loadMore">加载更多</view>
    </view>
  </view>
</template>

<script>
import { getStudentHours } from '../../api/student'
import { getUserStore } from '../../store/user'

export default {
  data() {
    return {
      loading: false,
      summary: null,
      transactions: [],
      page: 1,
      hasMore: false
    }
  },
  onLoad() { this.loadData() },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const store = getUserStore()
        const res = await getStudentHours(store.state.userInfo?.id, { page: this.page, pageSize: 20 })
        if (res) {
          this.transactions = res.list || []
          this.hasMore = this.transactions.length < (res.total || 0)
          // 计算汇总
          if (this.transactions.length) {
            let total = 0, used = 0
            this.transactions.forEach(t => { total += Math.abs(t.changeHours); if (t.changeHours < 0) used += Math.abs(t.changeHours) })
            this.summary = { totalHours: total, usedHours: used, remainHours: total - used }
          }
        }
      } catch(e) { console.error(e) } finally { this.loading = false }
    },
    loadMore() { this.page++; this.loadData() }
  }
}
</script>

<style scoped>
.title { font-size: 32rpx; font-weight: 600; display: block; margin-bottom: 20rpx; }
.summary-row { display: flex; justify-content: space-around; }
.summary-item { text-align: center; }
.s-val { font-size: 40rpx; font-weight: 700; display: block; }
.section-title { font-size: 28rpx; font-weight: 600; margin-bottom: 16rpx; display: block; }
.tx-item { padding: 20rpx 0; border-bottom: 1px solid #f0f0f0; }
.tx-type { font-size: 32rpx; font-weight: 700; min-width: 80rpx; }
.tx-type.increase { color: #52c41a; }
.tx-type.decrease { color: #ff4d4f; }
.tx-desc { font-size: 26rpx; display: block; }
.tx-class { font-size: 22rpx; }
.tx-balance { font-size: 24rpx; }
.tx-date { font-size: 22rpx; margin-top: 4rpx; display: block; }
.ml-12 { margin-left: 12rpx; }
.load-more { text-align: center; padding: 20rpx; font-size: 26rpx; }
.text-center { text-align: center; padding: 40rpx; }
</style>
