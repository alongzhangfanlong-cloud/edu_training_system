<template>
  <!-- 管理员端：我的 -->
  <view v-if="isAdmin" class="profile-page admin-profile">
    <view class="profile-header">
      <view class="ph-avatar">👨‍💼</view>
      <view class="ph-info">
        <text class="ph-name">{{ authStore.userInfo?.realName || '管理员' }}</text>
        <text class="ph-meta">系统管理员</text>
      </view>
    </view>

    <view class="menu-section">
      <view class="menu-item" @click="switchTabPage('/pages/admin/students/index')">
        <text class="menu-label">👥 学生管理</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @click="navigateTo('/pages/enrollment/index')">
        <text class="menu-label">📋 报名记录</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @click="navigateTo('/pages/schedule/index')">
        <text class="menu-label">📅 总课表</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @click="navigateTo('/pages/points/index')">
        <text class="menu-label">⭐ 积分管理</text>
        <text class="menu-arrow">›</text>
      </view>
    </view>

    <view class="menu-section">
      <view class="logout-btn" @click="handleLogout"><text>退出登录</text></view>
    </view>
  </view>

  <!-- 教师端：我的 -->
  <view v-else-if="isTeacher" class="profile-page teacher-profile">
    <view class="profile-header">
      <view class="ph-avatar">👨‍🏫</view>
      <view class="ph-info">
        <text class="ph-name">{{ teacherStore.teacherName }}</text>
        <text class="ph-meta">{{ teacherStore.userInfo?.subjectName || '教师' }}</text>
      </view>
    </view>

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

    <view class="menu-section">
      <view class="menu-item" @click="switchTabPage('/pages/teacher/students/index')">
        <text class="menu-label">👥 我的学生</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @click="switchTabPage('/pages/teacher/schedule/index')">
        <text class="menu-label">📅 我的课表</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @click="navigateTo('/pages/teacher/teaching/index')">
        <text class="menu-label">📚 教学工作台</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @click="navigateTo('/pages/teacher/leave/submit')">
        <text class="menu-label">📝 请假申请</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @click="navigateTo('/pages/teacher/leave/list')">
        <text class="menu-label">📋 请假记录</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @click="navigateTo('/pages/teacher/settings/index')">
        <text class="menu-label">⚙️ 通知设置</text>
        <text class="menu-arrow">›</text>
      </view>
    </view>

    <view class="menu-section">
      <view class="logout-btn" @click="handleLogout"><text>退出登录</text></view>
    </view>
  </view>

  <!-- 家长端：我的 -->
  <view v-else class="profile-page">
    <view v-if="childrenStore.loading"><loading-skeleton :rows="2" /></view>
    <view v-else-if="!childrenStore.children.length"><empty-state icon="👶" text="暂无关联孩子" /></view>
    <view v-else class="section">
      <view class="section-title">我的孩子</view>
      <view
        v-for="child in childrenStore.children"
        :key="child.studentId || child.id"
        class="child-card"
        @click="goChildDetail(child.studentId || child.id)"
      >
        <image class="child-avatar" :src="child.avatar || '/static/logo.png'" mode="aspectFill" />
        <view class="child-info">
          <text class="child-name">{{ child.name || child.studentName }}</text>
          <text class="child-meta">{{ child.grade || child.className || '--' }} · {{ child.school || '--' }}</text>
          <view class="child-hours">
            <text>剩余课时：<text class="hours-num">{{ child.remainingHours ?? child.remainHours ?? 0 }}</text> / {{ child.totalHours ?? child.total ?? 0 }}</text>
          </view>
        </view>
        <text class="child-arrow">›</text>
      </view>
    </view>

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

    <view class="section">
      <view class="logout-btn" @click="handleLogout"><text>退出登录</text></view>
    </view>
  </view>
</template>

<script setup>
import { computed } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import { useChildrenStore } from '@/stores/children';
import { useTeacherStore } from '@/stores/teacher';
import { useAuthStore } from '@/stores/auth';
import EmptyState from '@/components/empty-state.vue';
import LoadingSkeleton from '@/components/loading-skeleton.vue';

const childrenStore = useChildrenStore();
const teacherStore = useTeacherStore();
const authStore = useAuthStore();

const isTeacher = computed(() => authStore.isTeacher);
const isAdmin = computed(() => authStore.isAdmin);

onShow(() => {
  if (authStore.isLogin) {
    if (authStore.isTeacher) {
      teacherStore.fetchDashboard();
    } else if (authStore.isParent) {
      childrenStore.fetchChildren();
    }
  }
});

function goChildDetail(studentId) {
  uni.navigateTo({ url: `/pages/profile/child-detail?studentId=${studentId}` });
}

function goPage(url) {
  const childId = childrenStore.currentChild?.studentId || childrenStore.currentChild?.id;
  uni.navigateTo({ url: childId ? `${url}?studentId=${childId}` : url });
}

function navigateTo(url) {
  uni.navigateTo({ url });
}

function switchTabPage(url) {
  uni.reLaunch({ url });
}

function handleLogout() {
  authStore.logout();
}
</script>

<style scoped lang="scss">
.profile-page { min-height: 100vh; padding-bottom: 120rpx; }

.profile-header {
  display: flex; align-items: center; padding: 48rpx 32rpx; background: linear-gradient(135deg, #409eff, #66b1ff);
}
.ph-avatar {
  width: 120rpx; height: 120rpx; border-radius: 50%; background: rgba(255,255,255,.2);
  display: flex; align-items: center; justify-content: center; font-size: 56rpx; margin-right: 24rpx;
}
.ph-info { flex: 1; }
.ph-name { font-size: 36rpx; font-weight: 700; color: #fff; display: block; }
.ph-meta { font-size: 26rpx; color: rgba(255,255,255,.8); margin-top: 8rpx; display: block; }

.stats-row { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16rpx; padding: 24rpx; margin-top: -24rpx; }
.stat-item { background: #fff; border-radius: 12rpx; padding: 24rpx 8rpx; text-align: center; box-shadow: 0 4rpx 12rpx rgba(0,0,0,.05); }
.stat-num { font-size: 40rpx; font-weight: 700; color: #409eff; display: block; }
.stat-label { font-size: 22rpx; color: #909399; margin-top: 4rpx; display: block; }

.menu-section { margin-top: 24rpx; background: #fff; }
.section { padding: 24rpx; }
.section-title { font-size: 28rpx; font-weight: 600; color: #303133; margin-bottom: 16rpx; }

.menu-list { background: #fff; border-radius: 12rpx; }
.menu-item {
  display: flex; justify-content: space-between; align-items: center;
  padding: 28rpx 24rpx; border-bottom: 1rpx solid #f5f7fa;
}
.menu-item:last-child { border-bottom: none; }
.menu-label { font-size: 28rpx; color: #303133; }
.menu-arrow { font-size: 32rpx; color: #c0c4cc; }

.child-card {
  display: flex; align-items: center; background: #fff; border-radius: 12rpx; padding: 24rpx; margin-bottom: 12rpx;
}
.child-avatar { width: 96rpx; height: 96rpx; border-radius: 50%; background: #ecf5ff; margin-right: 20rpx; }
.child-info { flex: 1; }
.child-name { font-size: 30rpx; font-weight: 600; color: #303133; display: block; }
.child-meta { font-size: 24rpx; color: #909399; display: block; margin-top: 4rpx; }
.child-hours { font-size: 24rpx; color: #606266; margin-top: 4rpx; }
.hours-num { color: #409eff; font-weight: 600; }
.child-arrow { font-size: 40rpx; color: #c0c4cc; }

.logout-btn {
  margin: 48rpx 24rpx; text-align: center; padding: 24rpx;
  background: #fff; border-radius: 12rpx; color: #f56c6c; font-size: 30rpx;
}
</style>
