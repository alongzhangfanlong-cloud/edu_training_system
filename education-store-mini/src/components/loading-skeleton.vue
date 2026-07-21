<template>
  <view class="skeleton-list">
    <view v-for="i in rows" :key="i" class="skeleton-row">
      <view class="skeleton-line skeleton-line--short" />
      <view class="skeleton-line" />
      <view class="skeleton-line skeleton-line--medium" />
    </view>
  </view>
</template>

<script setup>
defineProps({
  rows: { type: Number, default: 3 },
});
</script>

<style scoped lang="scss">
.skeleton-list { padding: 24rpx; }
.skeleton-row {
  padding: 24rpx;
  margin-bottom: 16rpx;
  background: #fff;
  border-radius: 12rpx;
}
.skeleton-line {
  height: 24rpx;
  background: #f0f2f5;
  border-radius: 6rpx;
  margin-bottom: 16rpx;
  animation: shimmer 1.5s infinite;
  &--short { width: 40%; }
  &--medium { width: 70%; }
}
@keyframes shimmer {
  0% { opacity: 1; }
  50% { opacity: 0.5; }
  100% { opacity: 1; }
}
</style>
