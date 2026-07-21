<template>
  <view class="page-header" :style="{ paddingTop: statusBarHeight + 'px' }">
    <view class="ph-inner">
      <view v-if="showBack" class="ph-back" @click="goBack">
        <text class="ph-back-icon">←</text>
      </view>
      <text class="ph-title">{{ title }}</text>
      <view class="ph-placeholder" />
    </view>
  </view>
</template>

<script setup>
defineProps({
  title: { type: String, default: '' },
  showBack: { type: Boolean, default: false },
});

const statusBarHeight = uni.getSystemInfoSync().statusBarHeight || 20;

function goBack() {
  uni.navigateBack();
}
</script>

<style scoped lang="scss">
.page-header { background: #fff; }
.ph-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 88rpx;
  padding: 0 24rpx;
  border-bottom: 1rpx solid #ebeef5;
}
.ph-back { padding: 8rpx 12rpx; }
.ph-back-icon { font-size: 36rpx; color: #303133; }
.ph-title { font-size: 32rpx; font-weight: 600; color: #303133; flex: 1; text-align: center; }
.ph-placeholder { width: 60rpx; }
</style>
