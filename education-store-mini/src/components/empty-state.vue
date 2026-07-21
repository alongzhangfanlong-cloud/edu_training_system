<template>
  <view class="empty-state">
    <text class="empty-icon">{{ icon }}</text>
    <text class="empty-text">{{ text }}</text>
    <view v-if="showRetry" class="empty-retry" @click="$emit('retry')">
      <text>点击重试</text>
    </view>
  </view>
</template>

<script setup>
defineProps({
  icon: { type: String, default: '📭' },
  text: { type: String, default: '暂无数据' },
  showRetry: { type: Boolean, default: false },
});
defineEmits(['retry']);
</script>

<style scoped lang="scss">
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120rpx 0;
}
.empty-icon { font-size: 80rpx; margin-bottom: 24rpx; }
.empty-text { font-size: 28rpx; color: #909399; }
.empty-retry {
  margin-top: 32rpx;
  padding: 16rpx 48rpx;
  background: #409eff;
  border-radius: 40rpx;
  color: #fff;
  font-size: 26rpx;
}
</style>
