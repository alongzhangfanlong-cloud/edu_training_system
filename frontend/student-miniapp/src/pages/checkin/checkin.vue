<template>
  <view class="container">
    <view class="checkin-card card">
      <text class="title">扫码签到</text>
      <text class="subtitle text-muted">请扫描教室屏幕上的二维码</text>

      <!-- 扫码区域 (Phase 1: 模拟) -->
      <view class="scan-area" @click="simulateScan">
        <text class="scan-icon">📷</text>
        <text class="scan-text">点击模拟扫码</text>
      </view>

      <!-- 签到结果 -->
      <view v-if="checkResult" :class="['result-card', checkResult.success ? 'success' : 'fail']">
        <text class="result-icon">{{ checkResult.success ? '✅' : '❌' }}</text>
        <text class="result-title">{{ checkResult.success ? '签到成功' : '签到失败' }}</text>
        <text class="result-msg">{{ checkResult.message }}</text>
        <view v-if="checkResult.success" class="result-info">
          <text>课程: {{ checkResult.schedule?.className }}</text>
          <text>时间: {{ checkResult.schedule?.startTime }}</text>
          <text>签到时间: {{ checkResult.checkInTime }}</text>
        </view>
        <button class="btn-primary" @click="goHome">返回首页</button>
      </view>
    </view>
  </view>
</template>

<script>
import { checkIn } from '../../api/attendance'

export default {
  data() {
    return {
      scheduleId: null,
      checkResult: null,
      scanning: false
    }
  },
  onLoad(options) {
    this.scheduleId = options.scheduleId
  },
  methods: {
    async simulateScan() {
      if (this.scanning) return
      this.scanning = true
      uni.showLoading({ title: '签到中...' })
      try {
        // Phase 1: 使用模拟token和GPS
        const result = await checkIn({
          qrToken: 'mock-qr-token-' + Date.now(),
          latitude: 39.9042,
          longitude: 116.4074
        })
        this.checkResult = { success: true, ...result, message: '签到成功！' }
      } catch (e) {
        this.checkResult = { success: false, message: e.message || '签到失败，请重试' }
      } finally {
        uni.hideLoading()
        this.scanning = false
      }
    },
    goHome() { uni.switchTab({ url: '/pages/index/index' }) }
  }
}
</script>

<style scoped>
.title { font-size: 36rpx; font-weight: 700; text-align: center; display: block; }
.subtitle { text-align: center; display: block; margin: 12rpx 0 32rpx; }
.scan-area { width: 400rpx; height: 400rpx; margin: 0 auto; background: #f5f5f5; border-radius: 16rpx; display: flex; flex-direction: column; align-items: center; justify-content: center; border: 2rpx dashed #d9d9d9; }
.scan-icon { font-size: 80rpx; }
.scan-text { font-size: 26rpx; color: #999; margin-top: 16rpx; }
.result-card { text-align: center; padding: 40rpx 0; }
.result-card.success { background: #f6ffed; border-radius: 12rpx; margin-top: 32rpx; }
.result-card.fail { background: #fff2f0; border-radius: 12rpx; margin-top: 32rpx; }
.result-icon { font-size: 64rpx; display: block; }
.result-title { font-size: 32rpx; font-weight: 600; margin: 12rpx 0; display: block; }
.result-msg { font-size: 26rpx; color: #666; }
.result-info { margin: 16rpx 0; }
.result-info text { display: block; font-size: 24rpx; padding: 4rpx 0; }
.btn-primary { width: 60%; margin: 20rpx auto 0; }
</style>
