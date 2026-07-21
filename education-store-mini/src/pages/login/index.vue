<template>
  <view class="login-page">
    <view class="login-card">
      <image class="login-logo" src="/static/logo.png" mode="aspectFit" />
      <text class="login-title">教育管理平台</text>
      <text class="login-desc">在线教育课程展示与交易平台</text>
      <button
        class="login-btn"
        type="primary"
        :loading="loading"
        @click="handleLogin"
      >
        微信一键登录
      </button>
      <text class="login-tip">点击登录即表示同意用户协议和隐私政策</text>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { useChildrenStore } from '@/stores/children';

const loading = ref(false);
const authStore = useAuthStore();
const childrenStore = useChildrenStore();

async function handleLogin() {
  loading.value = true;
  try {
    await authStore.login();
    await childrenStore.fetchChildren();
    uni.switchTab({ url: '/pages/index/index' });
  } catch (e) {
    uni.showToast({ title: e.message || '登录失败', icon: 'none' });
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped lang="scss">
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 80rpx 64rpx;
  width: 600rpx;
  background: #fff;
  border-radius: 24rpx;
  box-shadow: 0 16rpx 48rpx rgba(0, 0, 0, 0.12);
}
.login-logo { width: 160rpx; height: 160rpx; margin-bottom: 32rpx; }
.login-title { font-size: 40rpx; font-weight: 700; color: #303133; margin-bottom: 8rpx; }
.login-desc { font-size: 26rpx; color: #909399; margin-bottom: 64rpx; }
.login-btn {
  width: 100%;
  height: 88rpx;
  line-height: 88rpx;
  border-radius: 44rpx;
  background: #07c160;
  font-size: 32rpx;
  color: #fff;
}
.login-btn::after { border: none; }
.login-tip { font-size: 22rpx; color: #c0c4cc; margin-top: 24rpx; text-align: center; }
</style>
