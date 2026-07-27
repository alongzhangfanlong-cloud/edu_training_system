<template>
  <view class="custom-tab-bar">
    <view
      v-for="(tab, index) in tabs"
      :key="tab.pagePath"
      class="tab-item"
      :class="{ active: currentIndex === index }"
      @click="switchTab(tab, index)"
    >
      <u-icon
        :name="tab.icon"
        size="22"
        :color="currentIndex === index ? '#409eff' : '#909399'"
      />
      <text class="tab-text">{{ tab.text }}</text>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { useAuthStore } from '@/stores/auth';

const authStore = useAuthStore();

const parentTabs = [
  { pagePath: '/pages/index/index', text: '首页', icon: 'home-fill' },
  { pagePath: '/pages/messages/index', text: '消息', icon: 'bell-fill' },
  { pagePath: '/pages/profile/index', text: '我的', icon: 'account-fill' },
];

const teacherTabs = [
  { pagePath: '/pages/teacher/schedule/index', text: '课表', icon: 'calendar-fill' },
  { pagePath: '/pages/teacher/students/index', text: '学生', icon: 'account-fill' },
  { pagePath: '/pages/teacher/checkin/index', text: '签到', icon: 'checkmark-circle-fill' },
  { pagePath: '/pages/teacher/teaching/index', text: '教学', icon: 'file-text-fill' },
  { pagePath: '/pages/profile/index', text: '我的', icon: 'account-fill' },
];

const adminTabs = [
  { pagePath: '/pages/index/index', text: '首页', icon: 'home-fill' },
  { pagePath: '/pages/admin/students/index', text: '学生', icon: 'account-fill' },
  { pagePath: '/pages/messages/index', text: '消息', icon: 'bell-fill' },
  { pagePath: '/pages/profile/index', text: '我的', icon: 'account-fill' },
];

const tabs = computed(() => {
  const r = authStore.role;
  if (r === 'TEACHER' || r === 'teacher') return teacherTabs;
  if (r === 'ADMIN' || r === 'admin') return adminTabs;
  return parentTabs;
});

const currentIndex = ref(0);

// 监听页面切换以高亮正确的 tab
watch(() => tabs.value, () => {
  updateCurrentIndex();
}, { immediate: true });

function updateCurrentIndex() {
  const pages = getCurrentPages();
  if (pages.length > 0) {
    const currentPath = '/' + pages[pages.length - 1].route;
    const idx = tabs.value.findIndex(t => t.pagePath === currentPath);
    if (idx >= 0) currentIndex.value = idx;
  }
}

function switchTab(tab, index) {
  currentIndex.value = index;
  uni.switchTab({ url: tab.pagePath });
}
</script>

<style scoped lang="scss">
.custom-tab-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  height: 100rpx;
  background: #fff;
  border-top: 1rpx solid #ebeef5;
  padding-bottom: env(safe-area-inset-bottom);
  z-index: 999;
}
.tab-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  // gap 替代方案，兼容低版本小程序基础库
  .tab-item > * + * { margin-top: 2rpx; }
  padding: 8rpx 0;
}
.tab-text { font-size: 20rpx; color: #909399; }
.tab-item.active .tab-text { color: #409eff; font-weight: 600; }
</style>
