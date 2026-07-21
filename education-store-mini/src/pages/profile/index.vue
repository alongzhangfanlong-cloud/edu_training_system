<template>
  <view class="profile-page">
    <!-- children list -->
    <view v-if="childrenStore.loading"><loading-skeleton :rows="2" /></view>
    <view v-else-if="!childrenStore.children.length"><empty-state icon="👶" text="暂无关联孩子" /></view>
    <view v-else class="section">
      <view class="section-title">我的孩子</view>
      <view
        v-for="child in childrenStore.children"
        :key="child.studentId"
        class="child-card"
        @click="goChildDetail(child.studentId)"
      >
        <image class="child-avatar" :src="child.avatar || '/static/logo.png'" mode="aspectFill" />
        <view class="child-info">
          <text class="child-name">{{ child.name }}</text>
          <text class="child-meta">{{ child.grade || '--' }} · {{ child.school || '--' }}</text>
          <view class="child-hours">
            <text>剩余课时：<text class="hours-num">{{ child.remainingHours || 0 }}</text> / {{ child.totalHours || 0 }}</text>
          </view>
        </view>
        <text class="child-arrow">›</text>
      </view>
    </view>

    <!-- quick links -->
    <view class="section">
      <view class="section-title">快捷入口</view>
      <view class="menu-list">
        <view class="menu-item" @click="goPage('/pages/attendance/index')">
          <text class="menu-label">📋 考勤记录</text><text class="menu-arrow">›</text>
        </view>
        <view class="menu-item" @click="goPage('/pages/homework/index')">
          <text class="menu-label">📝 课后作业</text><text class="menu-arrow">›</text>
        </view>
        <view class="menu-item" @click="goPage('/pages/schedule/index')">
          <text class="menu-label">📅 课程表</text><text class="menu-arrow">›</text>
        </view>
        <view class="menu-item" @click="goPage('/pages/exam/index')">
          <text class="menu-label">📊 考试成绩</text><text class="menu-arrow">›</text>
        </view>
        <view class="menu-item" @click="goPage('/pages/points/index')">
          <text class="menu-label">⭐ 积分中心</text><text class="menu-arrow">›</text>
        </view>
      </view>
    </view>

    <!-- logout -->
    <view class="section">
      <view class="logout-btn" @click="handleLogout"><text>退出登录</text></view>
    </view>
  </view>
</template>

<script setup>
import { useChildrenStore } from '@/stores/children';
import { useAuthStore } from '@/stores/auth';
import EmptyState from '@/components/empty-state.vue';
import LoadingSkeleton from '@/components/loading-skeleton.vue';

const childrenStore = useChildrenStore();
const authStore = useAuthStore();

function goChildDetail(studentId) {
  uni.navigateTo({ url: `/pages/profile/child-detail?studentId=${studentId}` });
}

function goPage(url) {
  const childId = childrenStore.currentChild?.studentId;
  uni.navigateTo({ url: childId ? `${url}?studentId=${childId}` : url });
}

function handleLogout() {
  uni.showModal({
    title: '退出登录',
    content: '确定要退出当前账号吗？',
    success: (res) => {
      if (res.confirm) authStore.logout();
    },
  });
}
</script>

<style scoped lang="scss">
.profile-page { padding: 24rpx; min-height: 100vh; }
.section { background: #fff; border-radius: 12rpx; margin-bottom: 24rpx; overflow: hidden; }
.section-title { font-size: 30rpx; font-weight: 600; color: #303133; padding: 24rpx 24rpx 16rpx; }
.child-card { display: flex; align-items: center; padding: 24rpx; border-bottom: 1rpx solid #f0f2f5; }
.child-card:last-child { border-bottom: none; }
.child-avatar { width: 96rpx; height: 96rpx; border-radius: 50%; background: #f0f2f5; flex-shrink: 0; }
.child-info { flex: 1; margin-left: 20rpx; min-width: 0; }
.child-name { font-size: 32rpx; font-weight: 600; color: #303133; display: block; }
.child-meta { font-size: 24rpx; color: #909399; margin-top: 4rpx; display: block; }
.child-hours { font-size: 24rpx; color: #606266; margin-top: 6rpx; }
.hours-num { color: #409eff; font-weight: 600; font-size: 28rpx; }
.child-arrow { font-size: 40rpx; color: #c0c4cc; padding: 0 8rpx; }
.menu-item { display: flex; justify-content: space-between; align-items: center; padding: 28rpx 24rpx; border-bottom: 1rpx solid #f0f2f5; }
.menu-item:last-child { border-bottom: none; }
.menu-label { font-size: 28rpx; color: #303133; }
.menu-arrow { font-size: 36rpx; color: #c0c4cc; }
.logout-btn { display: flex; justify-content: center; padding: 28rpx; color: #f56c6c; font-size: 30rpx; }
</style>
