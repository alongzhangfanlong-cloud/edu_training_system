<template>
  <view class="profile-page">
    <!-- 个人头部 -->
    <view class="profile-header">
      <view class="ph-avatar">👨‍🏫</view>
      <view class="ph-info">
        <text class="ph-name">{{ teacherStore.teacherName }}</text>
        <text class="ph-meta">{{ teacherStore.userInfo?.serverDateFormatted || '' }}</text>
      </view>
    </view>

    <!-- 统计 -->
    <view class="stats-row">
      <view class="stat-item">
        <text class="stat-num">{{ teacherStore.todayCourseCount }}</text>
        <text class="stat-label">今日课程</text>
      </view>
      <view class="stat-item">
        <text class="stat-num">{{ teacherStore.todos?.pendingGradingCount || 0 }}</text>
        <text class="stat-label">待批改</text>
      </view>
      <view class="stat-item">
        <text class="stat-num">{{ teacherStore.todos?.pendingLeaveReplyCount || 0 }}</text>
        <text class="stat-label">待处理请假</text>
      </view>
    </view>

    <!-- 菜单 -->
    <view class="menu-section">
      <view class="menu-item" @click="goTab('/pages/teacher/schedule/index')">
        <text class="menu-label">📅 我的课表</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @click="goPage('/pages/teacher/leave/submit')">
        <text class="menu-label">📝 请假申请</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @click="goPage('/pages/teacher/leave/list')">
        <text class="menu-label">📋 请假记录</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @click="goPage('/pages/teacher/settings/index')">
        <text class="menu-label">⚙️ 通知设置</text>
        <text class="menu-arrow">›</text>
      </view>
    </view>

    <!-- 退出 -->
    <view class="menu-section">
      <view class="logout-btn" @click="handleLogout"><text>退出登录</text></view>
    </view>
  </view>
</template>

<script setup>
import { onMounted } from 'vue';
import { useTeacherStore } from '@/stores/teacher';
import { useAuthStore } from '@/stores/auth';

const teacherStore = useTeacherStore();
const authStore = useAuthStore();

function goTab(url) { uni.switchTab({ url }); }
function goPage(url) { uni.navigateTo({ url }); }

function handleLogout() {
  uni.showModal({
    title: '退出登录',
    content: '确定要退出当前账号吗？',
    success: (res) => { if (res.confirm) authStore.logout(); },
  });
}

onMounted(() => { teacherStore.fetchDashboard(); });
</script>

<style scoped lang="scss">
.profile-page { min-height: 100vh; padding: 24rpx; padding-bottom: 120rpx; }

.profile-header { display: flex; align-items: center; padding: 32rpx 24rpx; background: #fff; border-radius: 12rpx; margin-bottom: 24rpx; }
.ph-avatar { font-size: 80rpx; margin-right: 24rpx; }
.ph-name { font-size: 36rpx; font-weight: 700; color: #303133; display: block; }
.ph-meta { font-size: 24rpx; color: #909399; margin-top: 4rpx; display: block; }

.stats-row { display: grid; grid-template-columns: repeat(3, 1fr); gap: 12rpx; margin-bottom: 24rpx; }
.stat-item { background: #fff; border-radius: 12rpx; padding: 24rpx 8rpx; text-align: center; }
.stat-num { font-size: 36rpx; font-weight: 700; color: #409eff; display: block; }
.stat-label { font-size: 22rpx; color: #909399; margin-top: 4rpx; display: block; }

.menu-section { background: #fff; border-radius: 12rpx; margin-bottom: 24rpx; overflow: hidden; }
.menu-item { display: flex; justify-content: space-between; align-items: center; padding: 28rpx 24rpx; border-bottom: 1rpx solid #f0f2f5; }
.menu-item:last-child { border-bottom: none; }
.menu-label { font-size: 28rpx; color: #303133; }
.menu-arrow { font-size: 36rpx; color: #c0c4cc; }

.logout-btn { display: flex; justify-content: center; padding: 28rpx; color: #f56c6c; font-size: 30rpx; }
</style>
