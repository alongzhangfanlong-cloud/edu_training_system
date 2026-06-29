<template>
  <view class="container">
    <view class="card menu-list">
      <view class="menu-item"><text>关于我们</text><text class="menu-arrow">›</text></view>
      <view class="menu-item"><text>用户协议</text><text class="menu-arrow">›</text></view>
      <view class="menu-item"><text>隐私政策</text><text class="menu-arrow">›</text></view>
      <view class="menu-item"><text>版本号</text><text class="text-muted">v1.0.0</text></view>
    </view>
    <view class="logout-btn" @click="doLogout">退出登录</view>
  </view>
</template>

<script>
import { logout } from '../../api/auth'
import { getUserStore } from '../../store/user'

export default {
  methods: {
    async doLogout() {
      try { await logout() } catch(e) {}
      getUserStore().logout()
      uni.reLaunch({ url: '/pages/index/index' })
    }
  }
}
</script>

<style scoped>
.menu-list { padding: 0; }
.menu-item { display: flex; justify-content: space-between; align-items: center; padding: 28rpx 24rpx; border-bottom: 1px solid #f5f5f5; font-size: 28rpx; }
.menu-arrow { font-size: 32rpx; color: #ccc; }
.logout-btn { text-align: center; padding: 24rpx; color: #ff4d4f; font-size: 28rpx; margin-top: 40rpx; }
</style>
