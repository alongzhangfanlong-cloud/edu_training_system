<template>
  <!-- 管理员首页 -->
  <view v-if="isAdmin" class="home-page admin-home">
    <view class="admin-header">
      <text class="admin-greeting">{{ greetingText }}，{{ authStore.userInfo?.realName || '管理员' }}</text>
      <text class="admin-date">{{ todayText }}</text>
    </view>

    <view class="admin-menu">
      <view class="admin-menu-item" @click="switchTabPage('/pages/admin/students/index')">
        <view class="ami-icon" style="background:linear-gradient(135deg,#667eea,#409eff)">👥</view>
        <text class="ami-label">学生管理</text>
      </view>
      <view class="admin-menu-item" @click="navigateTo('/pages/enrollment/index')">
        <view class="ami-icon" style="background:linear-gradient(135deg,#f093fb,#e6a23c)">📋</view>
        <text class="ami-label">报名记录</text>
      </view>
      <view class="admin-menu-item" @click="navigateTo('/pages/schedule/index')">
        <view class="ami-icon" style="background:linear-gradient(135deg,#43e97b,#67c23a)">📅</view>
        <text class="ami-label">总课表</text>
      </view>
      <view class="admin-menu-item" @click="navigateTo('/pages/points/index')">
        <view class="ami-icon" style="background:linear-gradient(135deg,#fa709a,#f59e0b)">⭐</view>
        <text class="ami-label">积分管理</text>
      </view>
    </view>

    <view class="admin-tips">
      <text class="tips-title">📢 提示</text>
      <text class="tips-text">管理员可查看全部学生数据、课表、报名记录等运营数据</text>
    </view>
  </view>

  <!-- 家长首页 -->
  <view v-else class="home-page">
    <view class="header-section">
      <view class="greeting">
        <text class="greeting-text">{{ greetingText }}</text>
        <text class="date-text">{{ todayText }}</text>
      </view>
      <child-switcher
        v-if="childrenStore.children.length > 1"
        :children="childrenStore.children"
        :current-id="currentChildId"
        @change="onChildChange"
        class="child-switcher-comp"
      />
    </view>

    <view v-if="currentChild" class="child-info-card">
      <view class="custom-avatar">
        <text class="avatar-text">{{ currentChild.name?.charAt(0) || '?' }}</text>
      </view>
      <view class="child-detail">
        <text class="child-name">{{ currentChild.name || '未命名' }}</text>
        <text class="child-meta">{{ currentChild.grade || '' }} {{ currentChild.className || '' }}</text>
      </view>
      <view v-if="unreadCount > 0" class="custom-badge">
        <text class="badge-text">{{ unreadCount > 99 ? '99+' : unreadCount }}</text>
      </view>
    </view>

    <view v-if="loading" class="loading-wrap">
      <view class="loading-spinner"></view>
      <text class="loading-text">加载中...</text>
    </view>

    <view v-else-if="error" class="error-wrap">
      <text class="error-icon">❌</text>
      <text class="error-text">加载失败</text>
      <view class="retry-btn" @click="loadData(true)">
        <text class="retry-btn-text">点击重试</text>
      </view>
    </view>

    <template v-else>
      <view class="overview-card">
        <view class="section-header">
          <text class="section-title">今日概览</text>
        </view>
        <view class="overview-grid">
          <view class="overview-item" @click="navigateTo('/pages/schedule/index')">
            <view class="overview-icon course-icon">
              <text class="icon-emoji">📅</text>
            </view>
            <text class="overview-num">{{ todaySchedule.length }}</text>
            <text class="overview-label">今日课程</text>
          </view>
          <view class="overview-item" @click="navigateTo('/pages/homework/index')">
            <view class="overview-icon homework-icon">
              <text class="icon-emoji">📝</text>
            </view>
            <text class="overview-num">{{ pendingHomework }}</text>
            <text class="overview-label">待办作业</text>
          </view>
          <view class="overview-item" @click="navigateTo('/pages/attendance/index')">
            <view class="overview-icon attendance-icon">
              <text class="icon-emoji">✅</text>
            </view>
            <text class="overview-num" :style="{ color: attendanceStatusColor }">{{ attendanceStatusText }}</text>
            <text class="overview-label">考勤状态</text>
          </view>
          <view class="overview-item" @click="navigateTo('/pages/points/index')">
            <view class="overview-icon points-icon">
              <text class="icon-emoji">⭐</text>
            </view>
            <text class="overview-num">{{ pointsBalance }}</text>
            <text class="overview-label">当前积分</text>
          </view>
        </view>
      </view>

      <view class="quick-actions">
        <view class="section-header">
          <text class="section-title">快捷功能</text>
        </view>
        <view class="action-grid">
          <view class="action-item" @click="navigateTo('/pages/schedule/index')">
            <view class="action-icon bg-blue">
              <text class="action-emoji">📅</text>
            </view>
            <text class="action-label">课表</text>
          </view>
          <view class="action-item" @click="navigateTo('/pages/homework/index')">
            <view class="action-icon bg-orange">
              <text class="action-emoji">📝</text>
            </view>
            <text class="action-label">作业</text>
          </view>
          <view class="action-item" @click="navigateTo('/pages/exam/index')">
            <view class="action-icon bg-purple">
              <text class="action-emoji">📊</text>
            </view>
            <text class="action-label">成绩</text>
          </view>
          <view class="action-item" @click="navigateTo('/pages/leave/index')">
            <view class="action-icon bg-red">
              <text class="action-emoji">📋</text>
            </view>
            <text class="action-label">请假</text>
          </view>
          <view class="action-item" @click="navigateTo('/pages/attendance/index')">
            <view class="action-icon bg-green">
              <text class="action-emoji">✅</text>
            </view>
            <text class="action-label">考勤</text>
          </view>
          <view class="action-item" @click="navigateTo('/pages/points/index')">
            <view class="action-icon bg-yellow">
              <text class="action-emoji">⭐</text>
            </view>
            <text class="action-label">积分</text>
          </view>
        </view>
      </view>

      <view class="timeline-section">
        <view class="section-header">
          <text class="section-title">最近消息</text>
          <text class="section-more" @click="navigateTo('/pages/messages/index')">查看全部</text>
        </view>
        <view v-if="timelineList.length === 0" class="empty-timeline">
          <text class="empty-icon">📭</text>
          <text class="empty-text">暂无消息</text>
        </view>
        <view v-else class="timeline-list">
          <view v-for="(item, index) in timelineList.slice(0, 5)" :key="item.id || index" class="timeline-item">
            <view class="timeline-dot" :style="{ backgroundColor: eventMeta(item.eventType).color }" />
            <view class="timeline-content">
              <view class="timeline-header">
                <text class="timeline-type" :style="{ color: eventMeta(item.eventType).color }">{{ eventMeta(item.eventType).label }}</text>
                <text class="timeline-time">{{ fromNow(item.createTime) }}</text>
              </view>
              <text class="timeline-title">{{ item.title || item.content || '新消息' }}</text>
              <text v-if="item.content" class="timeline-desc">{{ item.content }}</text>
            </view>
          </view>
        </view>
      </view>

      <view v-if="todaySchedule.length > 0" class="schedule-preview">
        <view class="section-header">
          <text class="section-title">今日课程</text>
          <text class="section-more" @click="navigateTo('/pages/schedule/index')">查看全部</text>
        </view>
        <view class="schedule-list">
          <view v-for="(item, index) in todaySchedule.slice(0, 3)" :key="item.id || index" class="schedule-item">
            <view class="schedule-time-col">
              <text class="schedule-time">{{ item.startTime || '--' }}</text>
              <view class="schedule-time-line" v-if="index < Math.min(todaySchedule.length, 3) - 1" />
            </view>
            <view class="schedule-info">
              <text class="schedule-course">{{ item.courseName || '未命名课程' }}</text>
              <text class="schedule-meta" v-if="item.roomName || item.teacherName">
                <text v-if="item.roomName">📍 {{ item.roomName }}</text>
                <text v-if="item.roomName && item.teacherName"> | </text>
                <text v-if="item.teacherName">👨‍🏫 {{ item.teacherName }}</text>
              </text>
            </view>
          </view>
        </view>
      </view>
    </template>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue';
import { onShow, onPullDownRefresh } from '@dcloudio/uni-app';
import { useChildrenStore } from '@/stores/children';
import { getSchedule } from '@/api/schedule';
import { getHomework } from '@/api/homework';
import { getAttendance } from '@/api/attendance';
import { getPoints } from '@/api/points';
import { getTimeline } from '@/api/timeline';
import { fromNow, eventMeta, formatDate, STATUS_MAP, parsePaginationResponse, normalizePaginationParams, safeNavigate, switchTabPage } from '@/utils/index';
import ChildSwitcher from '@/components/child-switcher.vue';
import { useAuthStore } from '@/stores/auth';

const childrenStore = useChildrenStore();
const authStore = useAuthStore();
const currentChildId = ref(null);
const loading = ref(true);
const error = ref(false);
const todaySchedule = ref([]);
const pendingHomework = ref(0);
const todayAttendance = ref(null);
const pointsBalance = ref(0);
const timelineList = ref([]);
const unreadCount = ref(0);

const isAdmin = computed(() => authStore.isAdmin);

const currentChild = computed(() =>
  childrenStore.children.find((c) => c.studentId === currentChildId.value) || childrenStore.children[0] || null
);

const greetingText = computed(() => {
  const hour = new Date().getHours();
  if (hour < 6) return '夜深了';
  if (hour < 9) return '早上好';
  if (hour < 12) return '上午好';
  if (hour < 14) return '中午好';
  if (hour < 18) return '下午好';
  if (hour < 22) return '晚上好';
  return '夜深了';
});

const todayText = computed(() => {
  const now = new Date();
  const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
  return `${formatDate(now)} ${weekDays[now.getDay()]}`;
});

const attendanceStatusText = computed(() => {
  if (!todayAttendance.value) return '待签到';
  const status = todayAttendance.value.status || todayAttendance.value.statusText;
  if (status) return STATUS_MAP.attendance[status] || status;
  return '已签到';
});

const attendanceStatusColor = computed(() => {
  const text = attendanceStatusText.value;
  const colorMap = { 出勤: '#67c23a', 迟到: '#e6a23c', 缺勤: '#f56c6c', 请假: '#409eff', 待签到: '#909399', 已签到: '#67c23a' };
  return colorMap[text] || '#909399';
});

async function loadData(isRefresh = false) {
  if (!currentChildId.value) return;
  loading.value = true;
  error.value = false;
  const today = formatDate(new Date());
  
  // 并行加载，单个接口失败不影响其他
  const results = await Promise.allSettled([
    getSchedule(currentChildId.value, { startDate: today, endDate: today }).catch(e => ({ error: e })),
    getHomework(currentChildId.value, normalizePaginationParams({ page: 1, pageSize: 50 })).catch(e => ({ error: e })),
    getAttendance(currentChildId.value, normalizePaginationParams({ page: 1, pageSize: 10, yearMonth: today.substring(0, 7), date: today })).catch(e => ({ error: e })),
    getPoints(currentChildId.value).catch(e => ({ error: e })),
    getTimeline(normalizePaginationParams({ page: 1, pageSize: 20 })).catch(e => ({ error: e })),
  ]);
  
  const [scheduleRes, homeworkRes, attendanceRes, pointsRes, timelineRes] = results.map(r => 
    r.status === 'fulfilled' ? r.value : { code: 0, msg: 'request failed' }
  );
  
  // 逐个处理结果，失败的用默认值
  try {
    if (scheduleRes && scheduleRes.code === 200) {
      const { list } = parsePaginationResponse(scheduleRes);
      todaySchedule.value = list.filter((item) => (item.scheduleDate || item.date) === today);
    }
  } catch (e) { console.warn('schedule parse error', e); }
  
  try {
    if (homeworkRes && homeworkRes.code === 200) {
      const { list } = parsePaginationResponse(homeworkRes);
      pendingHomework.value = list.filter((h) => {
        const status = h.status || h.completionStatus;
        return status !== 'COMPLETED' && status !== '已完成' && status !== 1;
      }).length;
    }
  } catch (e) { console.warn('homework parse error', e); }
  
  try {
    if (attendanceRes && attendanceRes.code === 200) {
      const { list } = parsePaginationResponse(attendanceRes);
      todayAttendance.value = list.find((a) => (a.attendanceDate || a.date) === today) || null;
    }
  } catch (e) { console.warn('attendance parse error', e); }
  
  try {
    if (pointsRes && pointsRes.code === 200) {
      const pointsData = pointsRes.data || {};
      pointsBalance.value = pointsData.balance ?? pointsData.points ?? pointsData.total ?? 0;
    }
  } catch (e) { console.warn('points parse error', e); }
  
  try {
    if (timelineRes && timelineRes.code === 200) {
      const { list } = parsePaginationResponse(timelineRes);
      timelineList.value = list;
      unreadCount.value = timelineList.value.filter((t) => t.read === false || t.isRead === false || !t.readTime).length;
    }
  } catch (e) { console.warn('timeline parse error', e); }
  
  loading.value = false;
}

function onChildChange(id) {
  currentChildId.value = id;
  childrenStore.setCurrentChild(id);
  loadData(true);
}

function navigateTo(url) {
  safeNavigate(url);
}

onShow(async () => {
  // 教师端不使用首页，自动跳转到课表页
  if (authStore.isTeacher) {
    uni.reLaunch({ url: '/pages/teacher/schedule/index' });
    return;
  }
  // 管理员使用管理员首页，不加载家长孩子数据
  if (authStore.isAdmin) {
    return;
  }
  if (childrenStore.children.length === 0) {
    await childrenStore.fetchChildren();
  }
  if (!currentChildId.value) {
    currentChildId.value = childrenStore.currentChildId;
  }
  if (currentChildId.value) {
    loadData(true);
  }
});

onPullDownRefresh(async () => {
  if (authStore.isAdmin || authStore.isTeacher) {
    uni.stopPullDownRefresh();
    return;
  }
  await childrenStore.fetchChildren();
  await loadData(true);
  uni.stopPullDownRefresh();
});
</script>

<style scoped lang="scss">
.home-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 40rpx;
}

.header-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40rpx 32rpx 32rpx;
}

.greeting {
  margin-bottom: 24rpx;
}

.greeting-text {
  font-size: 40rpx;
  font-weight: 700;
  color: #fff;
  display: block;
}

.date-text {
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.8);
  margin-top: 8rpx;
  display: block;
}

.child-switcher-comp {
  :deep(.switcher-btn) {
    background: rgba(255, 255, 255, 0.2);
  }
  :deep(.switcher-label) {
    color: #fff;
  }
  :deep(.switcher-arrow) {
    color: rgba(255, 255, 255, 0.8);
  }
}

.child-info-card {
  display: flex;
  align-items: center;
  margin: -20rpx 24rpx 24rpx;
  padding: 28rpx;
  background: #fff;
  border-radius: 16rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.06);
  position: relative;
}

.child-detail {
  flex: 1;
  margin-left: 20rpx;
}

.child-name {
  font-size: 34rpx;
  font-weight: 600;
  color: #303133;
  display: block;
}

.child-meta {
  font-size: 24rpx;
  color: #909399;
  margin-top: 6rpx;
  display: block;
}

.loading-wrap,
.error-wrap {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120rpx 0;
}

.error-text {
  font-size: 28rpx;
  color: #909399;
  margin: 20rpx 0;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 28rpx 20rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #303133;
}

.section-more {
  font-size: 26rpx;
  color: #409eff;
}

.overview-card {
  margin: 0 24rpx 24rpx;
  background: #fff;
  border-radius: 16rpx;
  padding: 28rpx 0;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
}

.overview-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 8rpx;
}

.overview-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 8rpx 0;
}

.overview-icon {
  width: 80rpx;
  height: 80rpx;
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12rpx;
}

.course-icon { background: rgba(64, 158, 255, 0.1); }
.homework-icon { background: rgba(230, 162, 60, 0.1); }
.attendance-icon { background: rgba(103, 194, 58, 0.1); }
.points-icon { background: rgba(245, 158, 11, 0.1); }

.overview-num {
  font-size: 34rpx;
  font-weight: 700;
  color: #303133;
}

.overview-label {
  font-size: 22rpx;
  color: #909399;
  margin-top: 4rpx;
}

.quick-actions {
  margin: 0 24rpx 24rpx;
  background: #fff;
  border-radius: 16rpx;
  padding: 28rpx 0;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
}

.action-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24rpx 0;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.action-icon {
  width: 96rpx;
  height: 96rpx;
  border-radius: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12rpx;
}

.bg-blue { background: linear-gradient(135deg, #667eea, #409eff); }
.bg-orange { background: linear-gradient(135deg, #f093fb, #e6a23c); }
.bg-purple { background: linear-gradient(135deg, #a855f7, #9b59b6); }
.bg-red { background: linear-gradient(135deg, #f5576c, #f56c6c); }
.bg-green { background: linear-gradient(135deg, #43e97b, #67c23a); }
.bg-yellow { background: linear-gradient(135deg, #fa709a, #f59e0b); }

.action-label {
  font-size: 26rpx;
  color: #303133;
}

.timeline-section {
  margin: 0 24rpx 24rpx;
  background: #fff;
  border-radius: 16rpx;
  padding: 28rpx 0;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
}

.empty-timeline {
  padding: 20rpx 0;
}

.timeline-list {
  padding: 0 28rpx;
}

.timeline-item {
  display: flex;
  position: relative;
  padding-bottom: 28rpx;
}

.timeline-item:last-child {
  padding-bottom: 0;
}

.timeline-dot {
  width: 16rpx;
  height: 16rpx;
  border-radius: 50%;
  margin-top: 8rpx;
  margin-right: 20rpx;
  flex-shrink: 0;
}

.timeline-content {
  flex: 1;
  min-width: 0;
}

.timeline-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8rpx;
}

.timeline-type {
  font-size: 24rpx;
  font-weight: 500;
}

.timeline-time {
  font-size: 22rpx;
  color: #c0c4cc;
}

.timeline-title {
  font-size: 28rpx;
  color: #303133;
  font-weight: 500;
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.timeline-desc {
  font-size: 24rpx;
  color: #909399;
  margin-top: 4rpx;
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.schedule-preview {
  margin: 0 24rpx;
  background: #fff;
  border-radius: 16rpx;
  padding: 28rpx 0;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
}

.schedule-list {
  padding: 0 28rpx;
}

.schedule-item {
  display: flex;
}

.schedule-time-col {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-right: 24rpx;
  position: relative;
}

.schedule-time {
  font-size: 26rpx;
  color: #409eff;
  font-weight: 500;
  white-space: nowrap;
}

.schedule-time-line {
  width: 2rpx;
  flex: 1;
  min-height: 40rpx;
  background: #e4e7ed;
  margin-top: 8rpx;
}

.schedule-info {
  flex: 1;
  padding-bottom: 28rpx;
}

.schedule-item:last-child .schedule-info {
  padding-bottom: 0;
}

.schedule-course {
  font-size: 30rpx;
  font-weight: 600;
  color: #303133;
  display: block;
}

.schedule-meta {
  font-size: 24rpx;
  color: #909399;
  margin-top: 6rpx;
  display: block;
}

/* 自定义头像 */
.custom-avatar {
  width: 40px;
  height: 40px;
  border-radius: 20px;
  background: #409eff;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.avatar-text {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
}

/* 自定义徽章 */
.custom-badge {
  position: absolute;
  top: -4px;
  right: -4px;
  min-width: 18px;
  height: 18px;
  border-radius: 9px;
  background: #f56c6c;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 5px;
}
.badge-text {
  color: #fff;
  font-size: 11px;
}

/* 加载中 */
.loading-spinner {
  width: 30px;
  height: 30px;
  border: 3px solid #e4e7ed;
  border-top-color: #409eff;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}
@keyframes spin {
  to { transform: rotate(360deg); }
}
.loading-text {
  font-size: 14px;
  color: #909399;
  margin-top: 12px;
}

/* 错误重试按钮 */
.error-icon {
  font-size: 40px;
}
.retry-btn {
  margin-top: 12px;
  padding: 8px 20px;
  background: #409eff;
  border-radius: 4px;
  cursor: pointer;
}
.retry-btn-text {
  color: #fff;
  font-size: 14px;
}

/* Emoji图标 */
.icon-emoji {
  font-size: 16px;
}
.action-emoji {
  font-size: 20px;
}

/* 空状态 */
.empty-timeline {
  padding: 20px 0;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.empty-icon {
  font-size: 40px;
  margin-bottom: 8px;
}
.empty-text {
  font-size: 14px;
  color: #909399;
}

/* 管理员首页样式 */
.admin-home { padding-bottom: 40rpx; }
.admin-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 60rpx 32rpx 48rpx;
}
.admin-greeting { font-size: 40rpx; font-weight: 700; color: #fff; display: block; }
.admin-date { font-size: 26rpx; color: rgba(255,255,255,.8); margin-top: 8rpx; display: block; }
.admin-menu {
  display: grid; grid-template-columns: repeat(2, 1fr); gap: 24rpx;
  padding: 24rpx; margin-top: -24rpx;
}
.admin-menu-item {
  background: #fff; border-radius: 16rpx; padding: 40rpx 24rpx;
  display: flex; flex-direction: column; align-items: center;
  box-shadow: 0 4rpx 12rpx rgba(0,0,0,.05);
}
.ami-icon {
  width: 96rpx; height: 96rpx; border-radius: 24rpx;
  display: flex; align-items: center; justify-content: center;
  font-size: 44rpx; margin-bottom: 16rpx;
}
.ami-label { font-size: 28rpx; color: #303133; font-weight: 500; }
.admin-tips { margin: 24rpx; padding: 24rpx; background: #fff; border-radius: 12rpx; }
.tips-title { font-size: 28rpx; font-weight: 600; color: #303133; display: block; margin-bottom: 8rpx; }
.tips-text { font-size: 24rpx; color: #909399; line-height: 1.6; }
</style>
