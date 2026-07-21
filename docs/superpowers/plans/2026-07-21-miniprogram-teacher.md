# 小程序教师端 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 在已有家长端小程序中新增教师端（4 Tab + 8 子页面），复用全部现有后端 API，通过自定义 TabBar 实现角色路由分发。

**Architecture:** 使用 uni-app `tabBar.custom: true` + `src/custom-tab-bar/index.vue` 自定义 TabBar 组件，根据 authStore.role 渲染不同 Tab 列表。教师端 4 Tab（课表/签到/教学/我的），家长端 3 Tab（首页/消息/我的）保持不变。

**Tech Stack:** uni-app 3.x (Vite + Vue 3 Composition API), uView Plus 3.x, Pinia, SCSS

---

### Task 1: Core — 自定义 TabBar + 角色路由

**Files:**
- Modify: `education-store-mini/src/pages.json`
- Modify: `education-store-mini/src/App.vue`
- Modify: `education-store-mini/src/stores/auth.js`
- Create: `education-store-mini/src/custom-tab-bar/index.vue`

- [ ] **Step 1: 修改 auth store — 新增 role 字段**

```js
// stores/auth.js — 在 token 和 userInfo 之后新增:
const role = ref(uni.getStorageSync('role') || '');

// login() 中，获取 token 后解析 role:
// token 是 JWT，从中解析 role
function parseJwt(token) {
  try {
    const payload = token.split('.')[1];
    const decoded = JSON.parse(atob(payload));
    return decoded.role || decoded.roles || '';
  } catch { return ''; }
}

// login() success 回调中:
const jwt = res.data.token || res.data.access_token;
token.value = jwt;
userInfo.value = res.data.userInfo || res.data;
const userRole = parseJwt(jwt) || (res.data.userInfo && res.data.userInfo.role) || (res.data.role) || 'PARENT';
role.value = userRole;
uni.setStorageSync('role', userRole);

// checkAuth() 中恢复 role:
function checkAuth() {
  if (!token.value) return false;
  role.value = uni.getStorageSync('role') || '';
  return true;
}

// logout() 中清除 role:
function logout() {
  // ... existing logout code
  role.value = '';
}

// return 中新增 role
return { token, userInfo, role, isLogin, login, logout, checkAuth };
```

- [ ] **Step 2: 修改 pages.json — custom tabBar + 注册教师端页面**

```json
{
  "easycom": { "autoscan": true, "custom": { "^u-(.*)": "uview-plus/components/u-$1/u-$1.vue" } },
  "pages": [
    { "path": "pages/login/index", "style": { "navigationBarTitleText": "" } },
    { "path": "pages/index/index", "style": { "navigationBarTitleText": "首页", "enablePullDownRefresh": true } },
    { "path": "pages/messages/index", "style": { "navigationBarTitleText": "消息" } },
    { "path": "pages/profile/index", "style": { "navigationBarTitleText": "我的" } },
    { "path": "pages/profile/child-detail", "style": { "navigationBarTitleText": "孩子详情" } },
    { "path": "pages/attendance/index", "style": { "navigationBarTitleText": "考勤记录", "enablePullDownRefresh": true } },
    { "path": "pages/homework/index", "style": { "navigationBarTitleText": "课后作业", "enablePullDownRefresh": true } },
    { "path": "pages/leave/index", "style": { "navigationBarTitleText": "请假记录", "enablePullDownRefresh": true } },
    { "path": "pages/leave/submit", "style": { "navigationBarTitleText": "发起请假" } },
    { "path": "pages/enrollment/index", "style": { "navigationBarTitleText": "报名记录", "enablePullDownRefresh": true } },
    { "path": "pages/schedule/index", "style": { "navigationBarTitleText": "课程表", "enablePullDownRefresh": true } },
    { "path": "pages/exam/index", "style": { "navigationBarTitleText": "考试成绩", "enablePullDownRefresh": true } },
    { "path": "pages/evaluation/index", "style": { "navigationBarTitleText": "课堂评价", "enablePullDownRefresh": true } },
    { "path": "pages/points/index", "style": { "navigationBarTitleText": "积分中心", "enablePullDownRefresh": true } },
    { "path": "pages/teacher/schedule/index", "style": { "navigationBarTitleText": "课表" } },
    { "path": "pages/teacher/checkin/index", "style": { "navigationBarTitleText": "签到" } },
    { "path": "pages/teacher/checkin/batch", "style": { "navigationBarTitleText": "批量签到" } },
    { "path": "pages/teacher/teaching/index", "style": { "navigationBarTitleText": "教学" } },
    { "path": "pages/teacher/teaching/eval-list", "style": { "navigationBarTitleText": "课堂评价" } },
    { "path": "pages/teacher/teaching/eval-form", "style": { "navigationBarTitleText": "评价表单" } },
    { "path": "pages/teacher/teaching/homework-list", "style": { "navigationBarTitleText": "课后作业", "enablePullDownRefresh": true } },
    { "path": "pages/teacher/teaching/homework-form", "style": { "navigationBarTitleText": "布置作业" } },
    { "path": "pages/teacher/profile/index", "style": { "navigationBarTitleText": "我的" } },
    { "path": "pages/teacher/leave/list", "style": { "navigationBarTitleText": "请假记录", "enablePullDownRefresh": true } },
    { "path": "pages/teacher/leave/submit", "style": { "navigationBarTitleText": "发起请假" } },
    { "path": "pages/teacher/settings/index", "style": { "navigationBarTitleText": "通知设置" } }
  ],
  "globalStyle": {
    "navigationBarTextStyle": "black",
    "navigationBarTitleText": "教育管理",
    "navigationBarBackgroundColor": "#ffffff",
    "backgroundColor": "#f5f7fa"
  },
  "tabBar": {
    "custom": true,
    "color": "#909399",
    "selectedColor": "#409eff",
    "borderStyle": "black",
    "backgroundColor": "#ffffff",
    "list": [
      { "pagePath": "pages/index/index", "text": "首页" },
      { "pagePath": "pages/messages/index", "text": "消息" },
      { "pagePath": "pages/profile/index", "text": "我的" }
    ]
  }
}
```

- [ ] **Step 3: 修改 App.vue — 角色路由分发**

```vue
<script setup>
import { onLaunch } from '@dcloudio/uni-app';
import { useAuthStore } from '@/stores/auth';
import { useChildrenStore } from '@/stores/children';

onLaunch(async () => {
  const authStore = useAuthStore();
  if (authStore.checkAuth()) {
    if (authStore.role === 'TEACHER' || authStore.role === 'teacher') {
      // 教师端：默认跳转课表 Tab
      // custom-tab-bar 会自动根据 role 渲染教师端 4 Tab
    } else {
      // 家长端：加载孩子列表
      const childrenStore = useChildrenStore();
      await childrenStore.fetchChildren();
    }
  }
});
</script>

<style lang="scss">
@import 'uview-plus/index.scss';
@import '@/uni.scss';

page {
  background-color: #f5f7fa;
  font-family: -apple-system, BlinkMacSystemFont, 'Helvetica Neue', Helvetica, Arial, sans-serif;
  font-size: 28rpx;
  color: #303133;
}
</style>
```

- [ ] **Step 4: 创建自定义 TabBar 组件**

```vue
<!-- custom-tab-bar/index.vue -->
<template>
  <view class="custom-tab-bar">
    <view
      v-for="(tab, index) in tabs"
      :key="index"
      class="tab-item"
      :class="{ active: currentIndex === index }"
      @click="switchTab(tab, index)"
    >
      <text class="tab-icon">{{ tab.icon }}</text>
      <text class="tab-text">{{ tab.text }}</text>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useAuthStore } from '@/stores/auth';

const authStore = useAuthStore();

const parentTabs = [
  { pagePath: '/pages/index/index', text: '首页', icon: '🏠' },
  { pagePath: '/pages/messages/index', text: '消息', icon: '🔔' },
  { pagePath: '/pages/profile/index', text: '我的', icon: '👤' },
];

const teacherTabs = [
  { pagePath: '/pages/teacher/schedule/index', text: '课表', icon: '📅' },
  { pagePath: '/pages/teacher/checkin/index', text: '签到', icon: '✅' },
  { pagePath: '/pages/teacher/teaching/index', text: '教学', icon: '📝' },
  { pagePath: '/pages/teacher/profile/index', text: '我的', icon: '👤' },
];

const tabs = computed(() => {
  const role = authStore.role;
  return (role === 'TEACHER' || role === 'teacher') ? teacherTabs : parentTabs;
});

const currentIndex = ref(0);

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
  padding: 8rpx 0;
}
.tab-icon { font-size: 36rpx; margin-bottom: 2rpx; }
.tab-text { font-size: 20rpx; color: #909399; }
.tab-item.active .tab-text { color: #409eff; font-weight: 600; }
</style>
```

- [ ] **Step 5: Verify** — `npx uni build -p mp-weixin` 编译通过，自定义 TabBar 正常渲染。

---

### Task 2: API 层 — 教师端 API 模块（4 文件）

**Files:**
- Create: `education-store-mini/src/api/teacher.js`
- Create: `education-store-mini/src/api/checkin.js`
- Create: `education-store-mini/src/api/teacher-leave.js`
- Create: `education-store-mini/src/api/teacher-evaluation.js`

- [ ] **Step 1: 创建 teacher.js（教师工作台 + 课表）**

```js
import request from '@/utils/request';

/** 获取教师欢迎区信息 */
export function getUserInfo() {
  return request({ url: '/api/v1/teacher/dashboard/user-info', method: 'GET' });
}

/** 获取统计卡片数据 */
export function getStatistics() {
  return request({ url: '/api/v1/teacher/dashboard/statistics', method: 'GET' });
}

/** 获取今日课表（含签到状态摘要） */
export function getTodaySchedule() {
  return request({ url: '/api/v1/teacher/dashboard/schedule', method: 'GET' });
}

/** 获取排课学生名单+签到状态 */
export function getScheduleStudents(scheduleId) {
  return request({ url: `/api/v1/teacher/dashboard/schedule/${scheduleId}/students`, method: 'GET' });
}

/** 获取待办数据 */
export function getTodos() {
  return request({ url: '/api/v1/teacher/dashboard/todos', method: 'GET' });
}

/** 获取课表（日期范围） */
export function getSchedules(params) {
  return request({ url: '/api/v1/schedules/list', method: 'GET', data: params });
}
```

- [ ] **Step 2: 创建 checkin.js（签到）**

```js
import request from '@/utils/request';

/** 批量签到 */
export function batchSignin(data) {
  return request({ url: '/api/v1/attendance/signin/batch', method: 'POST', data });
}

/** 签到记录列表 */
export function getSigninList(params) {
  return request({ url: '/api/v1/attendance/signin/list', method: 'GET', data: params });
}

/** 排课签到统计 */
export function getSigninStats(scheduleId) {
  return request({ url: `/api/v1/attendance/signin/stats/${scheduleId}`, method: 'GET' });
}
```

- [ ] **Step 3: 创建 teacher-leave.js（教师请假）**

```js
import request from '@/utils/request';

/** 教师请假列表 */
export function getLeaveList(params) {
  return request({ url: '/api/v1/attendance/leaves/teacher/list', method: 'GET', data: params });
}

/** 教师请假详情 */
export function getLeaveDetail(id) {
  return request({ url: `/api/v1/attendance/leaves/teacher/${id}`, method: 'GET' });
}

/** 提交请假申请 */
export function submitLeave(data) {
  return request({ url: '/api/v1/attendance/leaves/teacher/', method: 'POST', data });
}
```

- [ ] **Step 4: 创建 teacher-evaluation.js（教评学：教师评价学生）**

```js
import request from '@/utils/request';

/** 教评学列表（教师评价学生） */
export function getEvaluationList(params) {
  return request({ url: '/api/v1/teaching/evaluation/list', method: 'GET', data: params });
}

/** 教评学详情 */
export function getEvaluationDetail(id) {
  return request({ url: `/api/v1/teaching/evaluation/${id}`, method: 'GET' });
}

/** 暂存评价草稿 */
export function saveEvaluationDraft(data) {
  return request({ url: '/api/v1/teaching/evaluation/draft', method: 'POST', data });
}

/** 提交评价 */
export function submitEvaluation(id) {
  return request({ url: `/api/v1/teaching/evaluation/${id}/submit`, method: 'POST' });
}

/** 删除评价（仅草稿） */
export function deleteEvaluation(id, currentUserId) {
  return request({ url: `/api/v1/teaching/evaluation/${id}`, method: 'DELETE', data: { currentUserId } });
}

/** 作业列表 */
export function getHomeworkList(params) {
  return request({ url: '/api/v1/teaching/homework/list', method: 'GET', data: params });
}

/** 布置作业 */
export function createHomework(data) {
  return request({ url: '/api/v1/teaching/homework/', method: 'POST', data });
}

/** 作业详情 */
export function getHomeworkDetail(id) {
  return request({ url: `/api/v1/teaching/homework/${id}`, method: 'GET' });
}

/** 删除作业 */
export function deleteHomework(id, currentUserId) {
  return request({ url: `/api/v1/teaching/homework/${id}`, method: 'DELETE', data: { currentUserId } });
}
```

- [ ] **Step 5: Verify** — 所有 API 文件语法正确。

---

### Task 3: Store — 教师 Store

**Files:**
- Create: `education-store-mini/src/stores/teacher.js`

- [ ] **Step 1: 创建 teacher.js**

```js
import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { getUserInfo, getStatistics, getTodaySchedule, getTodos } from '@/api/teacher';

export const useTeacherStore = defineStore('teacher', () => {
  const userInfo = ref(null);
  const statistics = ref(null);
  const todaySchedule = ref([]);
  const todos = ref(null);
  const loading = ref(false);

  const teacherName = computed(() => userInfo.value?.userName || '老师');
  const todayCourseCount = computed(() => statistics.value?.todayCourseCount || 0);
  const uncheckedCount = computed(() => statistics.value?.todayUncheckedClassCount || 0);

  async function fetchDashboard() {
    loading.value = true;
    try {
      const [infoRes, statsRes, schedRes, todosRes] = await Promise.all([
        getUserInfo(),
        getStatistics(),
        getTodaySchedule(),
        getTodos(),
      ]);
      if (infoRes.code === 200) userInfo.value = infoRes.data;
      if (statsRes.code === 200) statistics.value = statsRes.data;
      if (schedRes.code === 200) todaySchedule.value = schedRes.data || [];
      if (todosRes.code === 200) todos.value = todosRes.data;
    } catch { /* ignore */ }
    finally { loading.value = false; }
  }

  return {
    userInfo, statistics, todaySchedule, todos, loading,
    teacherName, todayCourseCount, uncheckedCount,
    fetchDashboard,
  };
});
```

- [ ] **Step 2: Verify** — Store 文件语法正确。

---

### Task 4: 新组件 — schedule-card

**Files:**
- Create: `education-store-mini/src/components/schedule-card.vue`

- [ ] **Step 1: 创建 schedule-card.vue**

```vue
<template>
  <view class="sched-card" :class="{ 'is-finished': isFinished, 'is-active': isActive }">
    <view class="sc-time-bar">
      <text class="sc-time">{{ startTime }} - {{ endTime }}</text>
      <text v-if="isActive" class="sc-badge">进行中</text>
    </view>
    <view class="sc-body">
      <text class="sc-course">{{ courseName }}</text>
      <view class="sc-meta">
        <text class="sc-class">{{ className }}</text>
        <text class="sc-room">{{ classroomName }}</text>
      </view>
      <view class="sc-attendance">
        <text>签到：</text>
        <text :style="{ color: checkinColor }">{{ checkedCount }}/{{ expectedCount }}</text>
        <text class="sc-status-text">{{ attendanceLabel }}</text>
      </view>
    </view>
    <view v-if="showAction" class="sc-action" @click.stop="$emit('action')">
      <text>{{ actionText }}</text>
    </view>
  </view>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  startTime: { type: String, default: '' },
  endTime: { type: String, default: '' },
  courseName: { type: String, default: '' },
  className: { type: String, default: '' },
  classroomName: { type: String, default: '' },
  expectedCount: { type: Number, default: 0 },
  checkedCount: { type: Number, default: 0 },
  courseStatus: { type: String, default: '' },
  attendanceStatus: { type: String, default: '' },
  showAction: { type: Boolean, default: false },
  actionText: { type: String, default: '签到' },
});

defineEmits(['action']);

const isActive = computed(() => props.courseStatus === 'IN_PROGRESS');
const isFinished = computed(() => props.courseStatus === 'FINISHED');

const attendanceLabel = computed(() => {
  const map = {
    COMPLETED: '✓已签到',
    PARTIAL: '签到中',
    UNCHECKED: '待签到',
    NOT_STARTED: '未开始',
  };
  return map[props.attendanceStatus] || '';
});

const checkinColor = computed(() => {
  const map = {
    COMPLETED: '#67c23a',
    PARTIAL: '#e6a23c',
    UNCHECKED: '#f56c6c',
    NOT_STARTED: '#909399',
  };
  return map[props.attendanceStatus] || '#909399';
});
</script>

<style scoped lang="scss">
.sched-card {
  background: #fff;
  border-radius: 12rpx;
  padding: 20rpx 24rpx;
  margin-bottom: 12rpx;
  border-left: 4rpx solid transparent;
  &.is-active { border-left-color: #67c23a; }
  &.is-finished { opacity: 0.6; }
}
.sc-time-bar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8rpx; }
.sc-time { font-size: 26rpx; color: #409eff; font-weight: 500; }
.sc-badge { font-size: 20rpx; background: #67c23a; color: #fff; padding: 2rpx 12rpx; border-radius: 20rpx; }
.sc-body { margin-bottom: 8rpx; }
.sc-course { font-size: 30rpx; font-weight: 600; color: #303133; display: block; margin-bottom: 4rpx; }
.sc-meta { display: flex; gap: 16rpx; margin-bottom: 4rpx; }
.sc-class { font-size: 24rpx; color: #606266; }
.sc-room { font-size: 24rpx; color: #909399; }
.sc-attendance { font-size: 24rpx; color: #606266; }
.sc-status-text { margin-left: 8rpx; color: #909399; }
.sc-action {
  margin-top: 8rpx;
  text-align: center;
  padding: 12rpx;
  background: #409eff;
  border-radius: 8rpx;
  color: #fff;
  font-size: 26rpx;
}
</style>
```

- [ ] **Step 2: Verify** — 组件文件语法正确。

---

### Task 5: Tab1 课表 — pages/teacher/schedule/index.vue

**Files:**
- Create: `education-store-mini/pages/teacher/schedule/index.vue`

- [ ] **Step 1: 创建课表页面（周/月视图切换 + 日期翻页）**

```vue
<template>
  <view class="schedule-page">
    <!-- 视图切换 -->
    <view class="view-bar">
      <view class="view-tabs">
        <text :class="{ active: viewMode === 'week' }" @click="viewMode = 'week'">周视图</text>
        <text :class="{ active: viewMode === 'month' }" @click="viewMode = 'month'">月视图</text>
      </view>
      <text class="today-btn" @click="goToday">今天</text>
    </view>

    <!-- 日期导航 -->
    <view class="nav-bar">
      <text class="nav-arrow" @click="prev">‹</text>
      <text class="nav-label">{{ dateLabel }}</text>
      <text class="nav-arrow" @click="next">›</text>
    </view>

    <!-- 周视图 -->
    <view v-if="viewMode === 'week'" class="week-view">
      <view class="week-header">
        <view v-for="(d, i) in weekDays" :key="i" class="week-day-col" :class="{ today: d.isToday }">
          <text class="wd-name">{{ d.dayName }}</text>
          <text class="wd-date">{{ d.date }}</text>
        </view>
      </view>
      <view v-if="loading"><loading-skeleton :rows="3" /></view>
      <view v-else-if="!list.length"><empty-state icon="📅" text="本周暂无排课" /></view>
      <view v-else class="week-body">
        <schedule-card
          v-for="item in list"
          :key="item.scheduleId || item.id"
          v-bind="item"
          :start-time="item.startTime"
          :end-time="item.endTime"
          :course-name="item.courseName"
          :class-name="item.className"
          :classroom-name="item.classroomName || item.roomName"
          :expected-count="item.expectedCount || item.studentCount || 0"
          :checked-count="item.checkedCount || 0"
          :course-status="item.courseStatus"
          :attendance-status="item.attendanceStatus"
          @action="goCheckin(item.scheduleId || item.id)"
        />
      </view>
    </view>

    <!-- 月视图 -->
    <view v-else class="month-view">
      <view class="month-grid">
        <view v-for="d in monthDays" :key="d.date" class="month-cell" :class="{ today: d.isToday, 'has-course': d.hasCourse }" @click="selectMonthDay(d)">
          <text class="mc-date">{{ d.day }}</text>
          <view v-if="d.hasCourse" class="mc-dot" />
        </view>
      </view>
      <view v-if="selectedDay" class="day-detail">
        <text class="dd-title">{{ selectedDay.dateStr }} 课程</text>
        <schedule-card
          v-for="item in selectedDayCourses"
          :key="item.scheduleId || item.id"
          v-bind="item"
          :start-time="item.startTime"
          :end-time="item.endTime"
          :course-name="item.courseName"
          :class-name="item.className"
          :classroom-name="item.classroomName"
          :expected-count="item.expectedCount || 0"
          :checked-count="item.checkedCount || 0"
          :course-status="item.courseStatus"
          :attendance-status="item.attendanceStatus"
          @action="goCheckin(item.scheduleId || item.id)"
        />
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue';
import { getSchedules } from '@/api/teacher';
import ScheduleCard from '@/components/schedule-card.vue';
import EmptyState from '@/components/empty-state.vue';
import LoadingSkeleton from '@/components/loading-skeleton.vue';

const viewMode = ref('week');
const loading = ref(false);
const list = ref([]);
const error = ref(false);

const now = new Date();
const currentMonday = ref(getMonday(now));
const currentMonthFirst = ref(new Date(now.getFullYear(), now.getMonth(), 1));

function getMonday(d) { const r = new Date(d); r.setDate(r.getDate() - (r.getDay() || 7) + 1); return r; }
function fmt(d) { return d.getFullYear() + '-' + String(d.getMonth() + 1).padStart(2, '0') + '-' + String(d.getDate()).padStart(2, '0'); }

const dateLabel = computed(() => {
  if (viewMode.value === 'week') {
    const sun = new Date(currentMonday.value); sun.setDate(sun.getDate() + 6);
    return fmt(currentMonday.value) + ' ~ ' + fmt(sun);
  }
  return currentMonday.value.getFullYear() + '年' + (currentMonday.value.getMonth() + 1) + '月';
});

const weekDays = computed(() => {
  const names = ['一', '二', '三', '四', '五', '六', '日'];
  return Array.from({ length: 7 }, (_, i) => {
    const d = new Date(currentMonday.value); d.setDate(d.getDate() + i);
    const dateStr = fmt(d);
    return { dayName: names[i], date: d.getDate(), dateStr, isToday: fmt(now) === dateStr };
  });
});

const monthDays = computed(() => {
  const y = currentMonthFirst.value.getFullYear(), m = currentMonthFirst.value.getMonth();
  const firstDay = new Date(y, m, 1).getDay() || 7;
  const daysInMonth = new Date(y, m + 1, 0).getDate();
  const courseDates = new Set(list.value.map(i => i.scheduleDate || i.date));
  const cells = [];
  for (let i = 1; i < firstDay; i++) cells.push({ day: '', date: '', isToday: false, hasCourse: false });
  for (let d = 1; d <= daysInMonth; d++) {
    const dateStr = y + '-' + String(m + 1).padStart(2, '0') + '-' + String(d).padStart(2, '0');
    cells.push({ day: d, date: dateStr, isToday: fmt(now) === dateStr, hasCourse: courseDates.has(dateStr), dateStr });
  }
  return cells;
});

const selectedDay = ref(null);
const selectedDayCourses = computed(() => {
  if (!selectedDay.value) return [];
  return list.value.filter(i => (i.scheduleDate || i.date) === selectedDay.value.dateStr);
});

function selectMonthDay(d) {
  if (!d.hasCourse) return;
  selectedDay.value = d;
}

async function loadData() {
  loading.value = true;
  error.value = false;
  try {
    let startDate, endDate;
    if (viewMode.value === 'week') {
      startDate = fmt(currentMonday.value);
      const sun = new Date(currentMonday.value); sun.setDate(sun.getDate() + 6);
      endDate = fmt(sun);
    } else {
      const y = currentMonthFirst.value.getFullYear(), m = currentMonthFirst.value.getMonth();
      startDate = y + '-' + String(m + 1).padStart(2, '0') + '-01';
      endDate = y + '-' + String(m + 1).padStart(2, '0') + '-' + new Date(y, m + 1, 0).getDate();
    }
    const res = await getSchedules({ startDate, endDate });
    if (res.code === 200) list.value = res.data?.records || res.data || [];
  } catch { error.value = true; }
  finally { loading.value = false; }
}

function prev() {
  if (viewMode.value === 'week') {
    const d = new Date(currentMonday.value); d.setDate(d.getDate() - 7); currentMonday.value = d;
  } else {
    currentMonthFirst.value = new Date(currentMonthFirst.value.getFullYear(), currentMonthFirst.value.getMonth() - 1, 1);
  }
  loadData();
}
function next() {
  if (viewMode.value === 'week') {
    const d = new Date(currentMonday.value); d.setDate(d.getDate() + 7); currentMonday.value = d;
  } else {
    currentMonthFirst.value = new Date(currentMonthFirst.value.getFullYear(), currentMonthFirst.value.getMonth() + 1, 1);
  }
  loadData();
}
function goToday() {
  currentMonday.value = getMonday(now);
  currentMonthFirst.value = new Date(now.getFullYear(), now.getMonth(), 1);
  loadData();
}
function goCheckin(id) { uni.navigateTo({ url: `/pages/teacher/checkin/batch?scheduleId=${id}` }); }

watch(viewMode, () => loadData());
onMounted(() => loadData());
</script>

<style scoped lang="scss">
.schedule-page { min-height: 100vh; padding-bottom: 120rpx; }

.view-bar { display: flex; justify-content: space-between; align-items: center; padding: 16rpx 24rpx; background: #fff; }
.view-tabs { display: flex; gap: 32rpx; }
.view-tabs text { font-size: 28rpx; color: #909399; padding: 8rpx 0; }
.view-tabs text.active { color: #409eff; font-weight: 600; border-bottom: 4rpx solid #409eff; }
.today-btn { font-size: 26rpx; color: #409eff; padding: 8rpx 24rpx; border: 1rpx solid #409eff; border-radius: 32rpx; }

.nav-bar { display: flex; justify-content: center; align-items: center; padding: 16rpx; background: #fff; border-bottom: 1rpx solid #ebeef5; }
.nav-label { font-size: 28rpx; color: #303133; font-weight: 500; margin: 0 32rpx; }
.nav-arrow { font-size: 48rpx; color: #409eff; padding: 8rpx 12rpx; }

.week-view { padding: 16rpx 24rpx; }
.week-header { display: flex; }
.week-day-col { flex: 1; text-align: center; padding: 12rpx 0; }
.week-day-col.today { background: #ecf5ff; border-radius: 8rpx; }
.wd-name { font-size: 24rpx; color: #909399; display: block; }
.wd-date { font-size: 32rpx; color: #303133; font-weight: 600; display: block; margin-top: 4rpx; }

.month-view { padding: 16rpx 24rpx; }
.month-grid { display: grid; grid-template-columns: repeat(7, 1fr); gap: 4rpx; }
.month-cell { aspect-ratio: 1; display: flex; flex-direction: column; align-items: center; justify-content: center; border-radius: 8rpx; }
.month-cell.today { background: #ecf5ff; }
.mc-date { font-size: 26rpx; color: #303133; }
.mc-dot { width: 8rpx; height: 8rpx; border-radius: 50%; background: #409eff; margin-top: 4rpx; }

.day-detail { margin-top: 24rpx; }
.dd-title { font-size: 28rpx; font-weight: 600; color: #303133; margin-bottom: 12rpx; display: block; }
</style>
```

- [ ] **Step 2: Verify** — 课表页面编译通过。

---

### Task 6: Tab2 签到 — checkin/index + checkin/batch

**Files:**
- Create: `education-store-mini/pages/teacher/checkin/index.vue`
- Create: `education-store-mini/pages/teacher/checkin/batch.vue`

- [ ] **Step 1: 创建 checkin/index.vue（今日课程签到列表）**

```vue
<template>
  <view class="checkin-page">
    <!-- 统计卡片 -->
    <view class="stats-row">
      <view class="stat-card">
        <text class="stat-num">{{ teacherStore.todayCourseCount }}</text>
        <text class="stat-label">今日课程</text>
      </view>
      <view class="stat-card">
        <text class="stat-num" style="color:#e6a23c">{{ teacherStore.uncheckedCount }}</text>
        <text class="stat-label">待签到</text>
      </view>
      <view class="stat-card">
        <text class="stat-num" style="color:#67c23a">{{ checkedCount }}</text>
        <text class="stat-label">已签到</text>
      </view>
    </view>

    <view v-if="teacherStore.loading"><loading-skeleton :rows="3" /></view>
    <view v-else-if="!teacherStore.todaySchedule.length"><empty-state icon="📅" text="今日无课程安排 ☕" /></view>

    <view v-else class="schedule-list">
      <schedule-card
        v-for="item in teacherStore.todaySchedule"
        :key="item.scheduleId"
        v-bind="item"
        :start-time="item.startTime"
        :end-time="item.endTime"
        :course-name="item.courseName"
        :class-name="item.className"
        :classroom-name="item.classroomName"
        :expected-count="item.expectedCount"
        :checked-count="item.checkedCount"
        :course-status="item.courseStatus"
        :attendance-status="item.attendanceStatus"
        :show-action="item.attendanceStatus !== 'COMPLETED' && item.courseStatus !== 'NOT_STARTED'"
        :action-text="item.attendanceStatus === 'PARTIAL' ? '签到管理' : item.attendanceStatus === 'UNCHECKED' ? '去签到' : ''"
        @action="goBatch(item.scheduleId)"
      />
    </view>
  </view>
</template>

<script setup>
import { computed, onMounted } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import { useTeacherStore } from '@/stores/teacher';
import ScheduleCard from '@/components/schedule-card.vue';
import EmptyState from '@/components/empty-state.vue';
import LoadingSkeleton from '@/components/loading-skeleton.vue';

const teacherStore = useTeacherStore();

const checkedCount = computed(() =>
  teacherStore.todaySchedule.filter(s => s.attendanceStatus === 'COMPLETED').length
);

function goBatch(scheduleId) {
  uni.navigateTo({ url: `/pages/teacher/checkin/batch?scheduleId=${scheduleId}` });
}

onShow(() => { teacherStore.fetchDashboard(); });
</script>

<style scoped lang="scss">
.checkin-page { min-height: 100vh; padding-bottom: 120rpx; }
.stats-row { display: grid; grid-template-columns: repeat(3, 1fr); gap: 12rpx; padding: 24rpx; }
.stat-card { background: #fff; border-radius: 12rpx; padding: 24rpx 8rpx; text-align: center; }
.stat-num { font-size: 40rpx; font-weight: 700; color: #409eff; display: block; }
.stat-label { font-size: 22rpx; color: #909399; margin-top: 4rpx; display: block; }
.schedule-list { padding: 0 24rpx; }
</style>
```

- [ ] **Step 2: 创建 checkin/batch.vue（批量签到）**

```vue
<template>
  <view class="batch-page">
    <view class="batch-header">
      <text class="bh-course">{{ courseName }}</text>
      <text class="bh-class">{{ className }}</text>
      <text class="bh-count">已签 {{ signedCount }} / 应到 {{ students.length }}</text>
    </view>

    <view v-if="loading"><loading-skeleton :rows="4" /></view>
    <view v-else-if="!students.length"><empty-state icon="👥" text="暂无学生名单" /></view>

    <view v-else class="student-list">
      <view v-for="s in students" :key="s.studentId" class="student-row">
        <view class="sr-info">
          <text class="sr-name">{{ s.studentName }}</text>
        </view>
        <view class="sr-status" @click="cycleStatus(s)">
          <text class="sr-tag" :class="statusClass(s)">{{ statusText(s) }}</text>
        </view>
      </view>
    </view>

    <view v-if="students.length" class="submit-bar">
      <button class="submit-btn" :loading="submitting" @click="handleSubmit">确认签到</button>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { getScheduleStudents } from '@/api/teacher';
import { batchSignin } from '@/api/checkin';
import EmptyState from '@/components/empty-state.vue';
import LoadingSkeleton from '@/components/loading-skeleton.vue';

const STATUSES = ['PRESENT', 'LATE', 'ABSENT'];
const STATUS_LABELS = { PRESENT: '到校', LATE: '迟到', ABSENT: '缺勤' };

let scheduleId = null;
const courseName = ref('');
const className = ref('');
const students = ref([]);
const loading = ref(true);
const submitting = ref(false);

onLoad((options) => {
  scheduleId = options.scheduleId;
  loadStudents();
});

async function loadStudents() {
  loading.value = true;
  try {
    const res = await getScheduleStudents(scheduleId);
    if (res.code === 200) {
      const data = res.data || [];
      students.value = data.map(s => ({
        ...s,
        _status: s.attendanceStatus || 'PRESENT',
      }));
    }
  } catch { /* ignore */ }
  finally { loading.value = false; }
}

const signedCount = computed(() => students.value.length);

function cycleStatus(s) {
  const idx = STATUSES.indexOf(s._status);
  s._status = STATUSES[(idx + 1) % 3];
}

function statusText(s) { return STATUS_LABELS[s._status] || '--'; }
function statusClass(s) {
  return { PRESENT: 'tag-green', LATE: 'tag-orange', ABSENT: 'tag-red' }[s._status] || '';
}

async function handleSubmit() {
  const confirmText = students.value.map(s => `${s.studentName}: ${statusText(s)}`).join('\n');
  const confirmed = await new Promise(resolve => {
    uni.showModal({ title: '确认签到', content: confirmText, success: r => resolve(r.confirm) });
  });
  if (!confirmed) return;

  submitting.value = true;
  try {
    const data = students.value.map(s => ({
      scheduleId: Number(scheduleId),
      studentId: s.studentId,
      status: s._status,
    }));
    const res = await batchSignin(data);
    if (res.code === 200) {
      uni.showToast({ title: '签到成功', icon: 'success' });
      setTimeout(() => uni.navigateBack(), 1500);
    } else {
      uni.showToast({ title: res.msg || '签到失败', icon: 'none' });
    }
  } catch { uni.showToast({ title: '网络错误', icon: 'none' }); }
  finally { submitting.value = false; }
}
</script>

<style scoped lang="scss">
.batch-page { min-height: 100vh; padding-bottom: 120rpx; }
.batch-header { padding: 24rpx; background: #fff; margin-bottom: 16rpx; }
.bh-course { font-size: 34rpx; font-weight: 700; color: #303133; display: block; }
.bh-class { font-size: 26rpx; color: #606266; margin-top: 4rpx; display: block; }
.bh-count { font-size: 24rpx; color: #409eff; margin-top: 8rpx; display: block; }

.student-list { padding: 0 24rpx; }
.student-row { display: flex; justify-content: space-between; align-items: center; padding: 24rpx; background: #fff; border-radius: 12rpx; margin-bottom: 8rpx; }
.sr-name { font-size: 28rpx; color: #303133; font-weight: 500; }
.sr-tag { font-size: 24rpx; padding: 6rpx 20rpx; border-radius: 20rpx; border: 1rpx solid #dcdfe6; }
.tag-green { background: #f0f9eb; color: #67c23a; border-color: #c2e7b0; }
.tag-orange { background: #fdf6ec; color: #e6a23c; border-color: #f5dab1; }
.tag-red { background: #fef0f0; color: #f56c6c; border-color: #fbc4c4; }

.submit-bar { position: fixed; bottom: 0; left: 0; right: 0; padding: 16rpx 24rpx; padding-bottom: calc(16rpx + env(safe-area-inset-bottom)); background: #fff; border-top: 1rpx solid #ebeef5; }
.submit-btn { width: 100%; height: 88rpx; line-height: 88rpx; background: #409eff; color: #fff; border-radius: 44rpx; font-size: 32rpx; border: none; }
.submit-btn::after { border: none; }
</style>
```

- [ ] **Step 3: Verify** — 签到页面编译通过。

---

### Task 7: Tab3 教学 — teaching/index (容器) + eval-list + eval-form + homework-list + homework-form

**Files:**
- Create: `education-store-mini/pages/teacher/teaching/index.vue`
- Create: `education-store-mini/pages/teacher/teaching/eval-list.vue`
- Create: `education-store-mini/pages/teacher/teaching/eval-form.vue`
- Create: `education-store-mini/pages/teacher/teaching/homework-list.vue`
- Create: `education-store-mini/pages/teacher/teaching/homework-form.vue`

- [ ] **Step 1: 创建 teaching/index.vue（顶部分段控制器容器）**

```vue
<template>
  <view class="teaching-page">
    <view class="teaching-tabs">
      <text :class="{ active: currentTab === 'eval' }" @click="currentTab = 'eval'">评价</text>
      <text :class="{ active: currentTab === 'homework' }" @click="currentTab = 'homework'">作业</text>
    </view>
    <eval-list v-if="currentTab === 'eval'" />
    <homework-list v-else />
  </view>
</template>

<script setup>
import { ref } from 'vue';
import EvalList from './eval-list.vue';
import HomeworkList from './homework-list.vue';

const currentTab = ref('eval');
</script>

<style scoped lang="scss">
.teaching-page { min-height: 100vh; }
.teaching-tabs { display: flex; background: #fff; border-bottom: 1rpx solid #ebeef5; }
.teaching-tabs text { flex: 1; text-align: center; padding: 24rpx 0; font-size: 30rpx; color: #909399; }
.teaching-tabs text.active { color: #409eff; font-weight: 600; border-bottom: 4rpx solid #409eff; }
</style>
```

- [ ] **Step 2: 创建 eval-list.vue（教评学列表）**

```vue
<template>
  <view class="eval-list-page">
    <view class="sub-tabs">
      <text :class="{ active: status === '' }" @click="status = ''; loadData(true)">待评价</text>
      <text :class="{ active: status === '1' }" @click="status = '1'; loadData(true)">已评价</text>
    </view>

    <view v-if="loading"><loading-skeleton :rows="4" /></view>
    <view v-else-if="error"><empty-state icon="⚠️" text="加载失败" :show-retry="true" @retry="() => loadData(true)" /></view>
    <view v-else-if="!list.length"><empty-state icon="⭐" :text="status === '1' ? '暂无已评价记录' : '暂无待评价课程'" /></view>

    <view v-else class="eval-list">
      <view v-for="item in list" :key="item.id" class="eval-card" @click="goForm(item)">
        <view class="ec-header">
          <text class="ec-date">{{ item.createTime || item.evaluationDate || '' }}</text>
          <text class="ec-status" :style="{ color: item.status === 1 ? '#67c23a' : '#e6a23c' }">{{ item.status === 1 ? '已评' : '待评' }}</text>
        </view>
        <text class="ec-course">{{ item.courseName || '--' }}</text>
        <text class="ec-class">{{ item.className || '--' }} · {{ item.studentCount || 0 }}人</text>
      </view>
    </view>

    <view v-if="hasMore && !loading" class="load-more" @click="loadData()">加载更多</view>
  </view>
</template>

<script setup>
import { ref } from 'vue';
import { onMounted } from '@dcloudio/uni-app';
import { getEvaluationList } from '@/api/teacher-evaluation';
import EmptyState from '@/components/empty-state.vue';
import LoadingSkeleton from '@/components/loading-skeleton.vue';

const status = ref('');
const list = ref([]);
const loading = ref(true);
const error = ref(false);
const page = ref(1);
const hasMore = ref(true);

async function loadData(isRefresh) {
  if (isRefresh) { page.value = 1; hasMore.value = true; error.value = false; }
  if (!hasMore.value && !isRefresh) return;
  loading.value = true;
  try {
    const res = await getEvaluationList({ page: page.value, pageSize: 10, status: status.value });
    if (res.code === 200) {
      const data = res.data?.records || res.data || [];
      if (isRefresh || page.value === 1) list.value = [];
      list.value.push(...data);
      hasMore.value = data.length >= 10;
      page.value++;
    }
  } catch { if (page.value === 1 || isRefresh) error.value = true; }
  finally { loading.value = false; }
}

function goForm(item) { uni.navigateTo({ url: `/pages/teacher/teaching/eval-form?id=${item.id}` }); }

onMounted(() => loadData(true));
</script>

<style scoped lang="scss">
.eval-list-page { padding-bottom: 120rpx; }
.sub-tabs { display: flex; padding: 16rpx 24rpx; background: #fff; gap: 32rpx; }
.sub-tabs text { font-size: 28rpx; color: #909399; padding: 8rpx 16rpx; }
.sub-tabs text.active { color: #409eff; font-weight: 600; background: #ecf5ff; border-radius: 8rpx; }

.eval-list { padding: 16rpx 24rpx; }
.eval-card { background: #fff; border-radius: 12rpx; padding: 24rpx; margin-bottom: 12rpx; }
.ec-header { display: flex; justify-content: space-between; margin-bottom: 8rpx; }
.ec-date { font-size: 24rpx; color: #909399; }
.ec-status { font-size: 24rpx; font-weight: 500; }
.ec-course { font-size: 30rpx; font-weight: 600; color: #303133; display: block; }
.ec-class { font-size: 24rpx; color: #909399; margin-top: 4rpx; display: block; }

.load-more { text-align: center; padding: 24rpx; color: #409eff; font-size: 26rpx; }
</style>
```

- [ ] **Step 3: 创建 eval-form.vue（评价表单）**

```vue
<template>
  <view class="eval-form-page">
    <view v-if="loading"><loading-skeleton :rows="4" /></view>
    <template v-else>
      <view class="form-section">
        <text class="section-label">课堂总结</text>
        <textarea v-model="form.summary" class="form-textarea" placeholder="请输入课堂整体总结（必填）" maxlength="500" />
      </view>

      <view class="form-section">
        <text class="section-label">学生评价</text>
        <view v-for="(s, i) in form.students" :key="s.studentId" class="student-eval">
          <text class="se-name">{{ s.studentName }}</text>
          <view class="se-dimensions">
            <view v-for="dim in dimensions" :key="dim.key" class="se-dim">
              <text class="se-dim-label">{{ dim.label }}</text>
              <view class="se-stars">
                <text v-for="star in 5" :key="star" class="se-star" :class="{ filled: star <= (s[dim.key] || 0) }" @click="s[dim.key] = star">{{ star <= (s[dim.key] || 0) ? '★' : '☆' }}</text>
              </view>
            </view>
          </view>
          <input v-model="s.comment" class="se-comment" placeholder="评语（选填，200字）" maxlength="200" />
        </view>
      </view>

      <view class="submit-bar">
        <button class="draft-btn" @click="saveDraft">暂存草稿</button>
        <button class="submit-btn" type="primary" :loading="submitting" @click="handleSubmit">提交评价</button>
      </view>
    </template>
  </view>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { getEvaluationDetail, saveEvaluationDraft, submitEvaluation } from '@/api/teacher-evaluation';
import LoadingSkeleton from '@/components/loading-skeleton.vue';

const dimensions = [
  { key: 'focus', label: '专注度' },
  { key: 'participation', label: '参与度' },
  { key: 'completion', label: '完成度' },
  { key: 'overall', label: '综合表现' },
];

let evalId = null;
const loading = ref(true);
const submitting = ref(false);
const form = reactive({ summary: '', students: [] });

onLoad((options) => {
  evalId = options.id;
  loadDetail();
});

async function loadDetail() {
  loading.value = true;
  try {
    const res = await getEvaluationDetail(evalId);
    if (res.code === 200) {
      const d = res.data;
      form.summary = d.summary || '';
      form.students = (d.students || []).map(s => ({
        studentId: s.studentId,
        studentName: s.studentName,
        focus: s.focus || 0,
        participation: s.participation || 0,
        completion: s.completion || 0,
        overall: s.overall || 0,
        comment: s.comment || '',
      }));
    }
  } catch { /* ignore */ }
  finally { loading.value = false; }
}

async function saveDraft() {
  submitting.value = true;
  try {
    const res = await saveEvaluationDraft({ id: evalId, summary: form.summary, students: form.students });
    if (res.code === 200) uni.showToast({ title: '已保存草稿', icon: 'success' });
  } catch { uni.showToast({ title: '保存失败', icon: 'none' }); }
  finally { submitting.value = false; }
}

async function handleSubmit() {
  if (!form.summary.trim()) { uni.showToast({ title: '请输入课堂总结', icon: 'none' }); return; }
  submitting.value = true;
  try {
    const res = await submitEvaluation(evalId);
    if (res.code === 200) {
      uni.showToast({ title: '提交成功', icon: 'success' });
      setTimeout(() => uni.navigateBack(), 1500);
    } else {
      uni.showToast({ title: res.msg || '提交失败', icon: 'none' });
    }
  } catch { uni.showToast({ title: '网络错误', icon: 'none' }); }
  finally { submitting.value = false; }
}
</script>

<style scoped lang="scss">
.eval-form-page { min-height: 100vh; padding: 24rpx; padding-bottom: 160rpx; }
.form-section { background: #fff; border-radius: 12rpx; padding: 24rpx; margin-bottom: 24rpx; }
.section-label { font-size: 30rpx; font-weight: 600; color: #303133; margin-bottom: 16rpx; display: block; }
.form-textarea { width: 100%; height: 200rpx; padding: 16rpx; background: #f5f7fa; border-radius: 8rpx; font-size: 28rpx; box-sizing: border-box; }

.student-eval { border-top: 1rpx solid #ebeef5; padding-top: 20rpx; margin-top: 20rpx; }
.se-name { font-size: 28rpx; font-weight: 600; color: #303133; display: block; margin-bottom: 12rpx; }
.se-dim { display: flex; align-items: center; margin-bottom: 8rpx; }
.se-dim-label { width: 120rpx; font-size: 24rpx; color: #909399; }
.se-stars { display: flex; gap: 4rpx; }
.se-star { font-size: 36rpx; color: #dcdfe6; }
.se-star.filled { color: #f7ba2a; }
.se-comment { margin-top: 8rpx; padding: 12rpx 16rpx; background: #f5f7fa; border-radius: 8rpx; font-size: 26rpx; width: 100%; box-sizing: border-box; }

.submit-bar { position: fixed; bottom: 0; left: 0; right: 0; display: flex; gap: 16rpx; padding: 16rpx 24rpx; padding-bottom: calc(16rpx + env(safe-area-inset-bottom)); background: #fff; border-top: 1rpx solid #ebeef5; }
.draft-btn { flex: 1; height: 88rpx; line-height: 88rpx; border: 1rpx solid #409eff; color: #409eff; border-radius: 44rpx; font-size: 30rpx; background: #fff; }
.submit-btn { flex: 1; height: 88rpx; line-height: 88rpx; background: #409eff; border-radius: 44rpx; font-size: 30rpx; border: none; }
.submit-btn::after, .draft-btn::after { border: none; }
</style>
```

- [ ] **Step 4: 创建 homework-list.vue（作业列表）**

```vue
<template>
  <view class="hw-list-page">
    <view v-if="loading"><loading-skeleton :rows="4" /></view>
    <view v-else-if="error"><empty-state icon="⚠️" text="加载失败" :show-retry="true" @retry="() => loadData(true)" /></view>
    <view v-else-if="!list.length"><empty-state icon="📝" text="暂无课后作业" /></view>

    <view v-else class="hw-list">
      <view v-for="item in list" :key="item.id" class="hw-card">
        <view class="hwc-header">
          <text class="hwc-title">{{ item.title || item.content?.substring(0, 30) || '无标题' }}</text>
          <text class="hwc-time">{{ item.createTime || '' }}</text>
        </view>
        <text class="hwc-content">{{ (item.content || '').substring(0, 80) }}</text>
        <view class="hwc-meta">
          <text>{{ item.className || '' }}</text>
          <text class="hwc-delete" @click="handleDelete(item.id)">删除</text>
        </view>
      </view>
    </view>

    <view v-if="hasMore && !loading" class="load-more" @click="loadData()">加载更多</view>

    <view class="fab-wrap">
      <button class="fab-btn" @click="goCreate">+ 布置作业</button>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue';
import { onMounted, onShow } from '@dcloudio/uni-app';
import { getHomeworkList, deleteHomework } from '@/api/teacher-evaluation';
import { useAuthStore } from '@/stores/auth';
import EmptyState from '@/components/empty-state.vue';
import LoadingSkeleton from '@/components/loading-skeleton.vue';

const authStore = useAuthStore();
const list = ref([]);
const loading = ref(true);
const error = ref(false);
const page = ref(1);
const hasMore = ref(true);

async function loadData(isRefresh) {
  if (isRefresh) { page.value = 1; hasMore.value = true; error.value = false; }
  if (!hasMore.value && !isRefresh) return;
  loading.value = true;
  try {
    const res = await getHomeworkList({ page: page.value, pageSize: 10 });
    if (res.code === 200) {
      const data = res.data?.records || res.data || [];
      if (isRefresh || page.value === 1) list.value = [];
      list.value.push(...data);
      hasMore.value = data.length >= 10;
      page.value++;
    }
  } catch { if (page.value === 1 || isRefresh) error.value = true; }
  finally { loading.value = false; }
}

function goCreate() { uni.navigateTo({ url: '/pages/teacher/teaching/homework-form' }); }

async function handleDelete(id) {
  const confirmed = await new Promise(r => uni.showModal({ title: '删除确认', content: '确定删除该作业？', success: res => r(res.confirm) }));
  if (!confirmed) return;
  try {
    const res = await deleteHomework(id, authStore.userInfo?.userId);
    if (res.code === 200) { uni.showToast({ title: '已删除', icon: 'success' }); loadData(true); }
  } catch { uni.showToast({ title: '删除失败', icon: 'none' }); }
}

onMounted(() => loadData(true));
onShow(() => loadData(true));
</script>

<style scoped lang="scss">
.hw-list-page { min-height: 100vh; padding-bottom: 140rpx; }
.hw-list { padding: 16rpx 24rpx; }
.hw-card { background: #fff; border-radius: 12rpx; padding: 24rpx; margin-bottom: 12rpx; }
.hwc-header { display: flex; justify-content: space-between; margin-bottom: 8rpx; }
.hwc-title { font-size: 30rpx; font-weight: 600; color: #303133; flex: 1; }
.hwc-time { font-size: 22rpx; color: #c0c4cc; }
.hwc-content { font-size: 26rpx; color: #606266; line-height: 1.5; display: block; margin-bottom: 8rpx; }
.hwc-meta { display: flex; justify-content: space-between; font-size: 24rpx; color: #909399; }
.hwc-delete { color: #f56c6c; padding: 4rpx 8rpx; }
.load-more { text-align: center; padding: 24rpx; color: #409eff; font-size: 26rpx; }

.fab-wrap { position: fixed; bottom: 24rpx; left: 0; right: 0; display: flex; justify-content: center; }
.fab-btn { background: #409eff; color: #fff; border-radius: 44rpx; padding: 20rpx 48rpx; font-size: 30rpx; border: none; box-shadow: 0 8rpx 24rpx rgba(64,158,255,.3); }
.fab-btn::after { border: none; }
</style>
```

- [ ] **Step 5: 创建 homework-form.vue（布置作业）**

```vue
<template>
  <view class="hw-form-page">
    <view class="form-card">
      <view class="form-item">
        <text class="form-label">作业内容</text>
        <textarea v-model="form.content" class="form-textarea" placeholder="请输入作业内容（必填）" maxlength="5000" />
        <text class="form-count">{{ form.content.length }}/5000</text>
      </view>

      <view class="form-item">
        <text class="form-label">关联班级</text>
        <picker mode="selector" :range="classNames" :value="classIndex" @change="onClassChange">
          <view class="form-picker">{{ classNames[classIndex] || '请选择班级' }}</view>
        </picker>
      </view>
    </view>

    <view class="submit-btn-wrap">
      <button class="submit-btn" type="primary" :loading="submitting" @click="handleSubmit">发布作业</button>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { onMounted } from '@dcloudio/uni-app';
import { createHomework } from '@/api/teacher-evaluation';
import { getTodaySchedule } from '@/api/teacher';

const submitting = ref(false);
const classNames = ref(['']);
const classIndex = ref(0);

const form = reactive({ content: '', classId: null });

onMounted(async () => {
  try {
    const res = await getTodaySchedule();
    if (res.code === 200) {
      const classes = [...new Set((res.data || []).map(s => s.className).filter(Boolean))];
      classNames.value = classes.length ? classes : [''];
    }
  } catch { /* ignore */ }
});

function onClassChange(e) {
  classIndex.value = e.detail.value;
  form.classId = e.detail.value;
}

async function handleSubmit() {
  if (!form.content.trim()) { uni.showToast({ title: '请输入作业内容', icon: 'none' }); return; }
  submitting.value = true;
  try {
    const res = await createHomework({ content: form.content, classId: form.classId });
    if (res.code === 200) {
      uni.showToast({ title: '发布成功', icon: 'success' });
      setTimeout(() => uni.navigateBack(), 1500);
    } else {
      uni.showToast({ title: res.msg || '发布失败', icon: 'none' });
    }
  } catch { uni.showToast({ title: '网络错误', icon: 'none' }); }
  finally { submitting.value = false; }
}
</script>

<style scoped lang="scss">
.hw-form-page { padding: 24rpx; min-height: 100vh; }
.form-card { background: #fff; border-radius: 12rpx; padding: 24rpx; margin-bottom: 48rpx; }
.form-item { margin-bottom: 32rpx; }
.form-label { font-size: 28rpx; color: #303133; margin-bottom: 12rpx; display: block; }
.form-textarea { width: 100%; height: 300rpx; padding: 16rpx; background: #f5f7fa; border-radius: 8rpx; font-size: 28rpx; box-sizing: border-box; }
.form-count { text-align: right; font-size: 22rpx; color: #c0c4cc; margin-top: 4rpx; display: block; }
.form-picker { padding: 20rpx 16rpx; background: #f5f7fa; border-radius: 8rpx; font-size: 28rpx; color: #303133; }
.submit-btn { width: 100%; height: 88rpx; line-height: 88rpx; background: #409eff; border-radius: 44rpx; font-size: 32rpx; border: none; }
.submit-btn::after { border: none; }
</style>
```

- [ ] **Step 6: Verify** — 教学模块所有页面编译通过。

---

### Task 8: Tab4 我的 + 子页面（profile + leave/list + leave/submit + settings）

**Files:**
- Create: `education-store-mini/pages/teacher/profile/index.vue`
- Create: `education-store-mini/pages/teacher/leave/list.vue`
- Create: `education-store-mini/pages/teacher/leave/submit.vue`
- Create: `education-store-mini/pages/teacher/settings/index.vue`

- [ ] **Step 1: 创建 profile/index.vue（教师个人中心）**

```vue
<template>
  <view class="profile-page">
    <!-- 个人头部 -->
    <view class="profile-header">
      <view class="ph-avatar">👨‍🏫</view>
      <view class="ph-info">
        <text class="ph-name">{{ teacherStore.teacherName }}</text>
        <text class="ph-subject">{{ teacherStore.userInfo?.subject || '教师' }}</text>
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
    </view>

    <!-- 菜单 -->
    <view class="menu-section">
      <view class="menu-item" @click="goPage('/pages/teacher/schedule/index', true)">
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

function goPage(url, isTab) {
  if (isTab) { uni.switchTab({ url }); } else { uni.navigateTo({ url }); }
}

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
.ph-subject { font-size: 24rpx; color: #909399; margin-top: 4rpx; display: block; }

.stats-row { display: grid; grid-template-columns: 1fr 1fr; gap: 12rpx; margin-bottom: 24rpx; }
.stat-item { background: #fff; border-radius: 12rpx; padding: 24rpx; text-align: center; }
.stat-num { font-size: 40rpx; font-weight: 700; color: #409eff; display: block; }
.stat-label { font-size: 22rpx; color: #909399; margin-top: 4rpx; display: block; }

.menu-section { background: #fff; border-radius: 12rpx; margin-bottom: 24rpx; overflow: hidden; }
.menu-item { display: flex; justify-content: space-between; align-items: center; padding: 28rpx 24rpx; border-bottom: 1rpx solid #f0f2f5; }
.menu-item:last-child { border-bottom: none; }
.menu-label { font-size: 28rpx; color: #303133; }
.menu-arrow { font-size: 36rpx; color: #c0c4cc; }

.logout-btn { display: flex; justify-content: center; padding: 28rpx; color: #f56c6c; font-size: 30rpx; }
</style>
```

- [ ] **Step 2: 创建 leave/list.vue（教师请假记录）**

```vue
<template>
  <view class="leave-page">
    <view v-if="loading"><loading-skeleton :rows="4" /></view>
    <view v-else-if="error"><empty-state icon="⚠️" text="加载失败" :show-retry="true" @retry="loadData" /></view>
    <view v-else-if="!list.length"><empty-state icon="📋" text="暂无请假记录" /></view>

    <view v-else class="record-list">
      <view v-for="item in list" :key="item.id" class="leave-card">
        <view class="lc-header">
          <text class="lc-date">{{ item.leaveDate || item.startDate || '--' }}</text>
          <text class="lc-status" :style="{ color: statusColor(item.status) }">{{ statusText(item.status) }}</text>
        </view>
        <text class="lc-type">{{ typeText(item.leaveType) }}</text>
        <text v-if="item.reason" class="lc-reason">{{ item.reason }}</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue';
import { onMounted } from '@dcloudio/uni-app';
import { getLeaveList } from '@/api/teacher-leave';
import EmptyState from '@/components/empty-state.vue';
import LoadingSkeleton from '@/components/loading-skeleton.vue';

const list = ref([]);
const loading = ref(true);
const error = ref(false);

const typeText = (t) => ({ 1: '事假', 2: '病假', 3: '调休', 4: '其他' }[t] || '--');
const statusText = (s) => ({ 0: '待审批', 1: '已通过', 2: '已拒绝' }[s] || '--');
const statusColor = (s) => ({ 0: '#e6a23c', 1: '#67c23a', 2: '#f56c6c' }[s] || '#909399');

async function loadData() {
  loading.value = true; error.value = false;
  try {
    const res = await getLeaveList({ page: 1, pageSize: 50 });
    if (res.code === 200) list.value = res.data?.records || res.data || [];
  } catch { error.value = true; }
  finally { loading.value = false; }
}

onMounted(() => loadData());
</script>

<style scoped lang="scss">
.leave-page { padding: 24rpx; }
.record-list { }
.leave-card { background: #fff; border-radius: 12rpx; padding: 24rpx; margin-bottom: 12rpx; }
.lc-header { display: flex; justify-content: space-between; margin-bottom: 8rpx; }
.lc-date { font-size: 28rpx; font-weight: 600; color: #303133; }
.lc-status { font-size: 24rpx; font-weight: 500; }
.lc-type { font-size: 24rpx; color: #606266; display: block; }
.lc-reason { font-size: 24rpx; color: #909399; margin-top: 4rpx; display: block; }
</style>
```

- [ ] **Step 3: 创建 leave/submit.vue（教师发起请假）**

```vue
<template>
  <view class="submit-page">
    <view class="form-card">
      <view class="form-item">
        <text class="form-label">请假日期</text>
        <picker mode="date" :value="form.leaveDate" @change="e => form.leaveDate = e.detail.value">
          <view class="form-picker">{{ form.leaveDate || '请选择日期' }}</view>
        </picker>
      </view>

      <view class="form-item">
        <text class="form-label">请假类型</text>
        <picker mode="selector" :range="types" :value="typeIndex" @change="e => { typeIndex = e.detail.value; form.leaveType = e.detail.value + 1; }">
          <view class="form-picker">{{ types[typeIndex] }}</view>
        </picker>
      </view>

      <view class="form-item">
        <text class="form-label">请假原因</text>
        <textarea v-model="form.reason" class="form-textarea" placeholder="请输入请假原因（必填）" maxlength="200" />
      </view>
    </view>

    <view class="submit-wrap">
      <button class="submit-btn" type="primary" :loading="submitting" @click="handleSubmit">提交申请</button>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { submitLeave } from '@/api/teacher-leave';

const types = ['事假', '病假', '调休', '其他'];
const typeIndex = ref(0);
const submitting = ref(false);

const form = reactive({ leaveDate: '', leaveType: 1, reason: '' });

async function handleSubmit() {
  if (!form.leaveDate) { uni.showToast({ title: '请选择日期', icon: 'none' }); return; }
  if (!form.reason.trim()) { uni.showToast({ title: '请填写原因', icon: 'none' }); return; }

  submitting.value = true;
  try {
    const res = await submitLeave({ leaveDate: form.leaveDate, leaveType: form.leaveType, reason: form.reason });
    if (res.code === 200) {
      uni.showToast({ title: '提交成功', icon: 'success' });
      setTimeout(() => uni.navigateBack(), 1500);
    } else { uni.showToast({ title: res.msg || '提交失败', icon: 'none' }); }
  } catch { uni.showToast({ title: '网络错误', icon: 'none' }); }
  finally { submitting.value = false; }
}
</script>

<style scoped lang="scss">
.submit-page { padding: 24rpx; min-height: 100vh; }
.form-card { background: #fff; border-radius: 12rpx; padding: 24rpx; margin-bottom: 48rpx; }
.form-item { margin-bottom: 32rpx; }
.form-label { font-size: 28rpx; color: #303133; margin-bottom: 12rpx; display: block; }
.form-picker { padding: 20rpx 16rpx; background: #f5f7fa; border-radius: 8rpx; font-size: 28rpx; color: #303133; }
.form-textarea { width: 100%; height: 200rpx; padding: 16rpx; background: #f5f7fa; border-radius: 8rpx; font-size: 28rpx; box-sizing: border-box; }
.submit-btn { width: 100%; height: 88rpx; line-height: 88rpx; background: #409eff; border-radius: 44rpx; font-size: 32rpx; border: none; }
.submit-btn::after { border: none; }
</style>
```

- [ ] **Step 4: 创建 settings/index.vue（通知设置）**

```vue
<template>
  <view class="settings-page">
    <view class="section">
      <view class="section-title">通知偏好</view>
      <view v-for="item in settings" :key="item.key" class="setting-item">
        <view class="si-info">
          <text class="si-label">{{ item.label }}</text>
          <text class="si-desc">{{ item.desc }}</text>
        </view>
        <switch :checked="item.value" :disabled="saving" @change="(e) => onToggle(item.key, e.detail.value)" color="#409eff" />
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { getNotificationSettings, updateNotificationSettings } from '@/api/notification';

const saving = ref(false);

const settingDefs = [
  { key: 'SCHEDULE_REMINDER', label: '课表提醒', desc: '上课前推送提醒' },
  { key: 'CHECKIN_REMINDER', label: '签到提醒', desc: '需要签到时推送' },
  { key: 'LEAVE_RESULT', label: '请假审批结果', desc: '审批结果推送' },
  { key: 'EVALUATION_REMINDER', label: '评价提醒', desc: '课后评价提醒' },
  { key: 'SYSTEM_NOTICE', label: '系统通知', desc: '机构公告和系统通知' },
];

const settings = reactive(settingDefs.map(d => ({ ...d, value: true })));

async function loadSettings() {
  try {
    const res = await getNotificationSettings();
    if (res.code === 200 && res.data) {
      for (const item of settings) {
        if (res.data[item.key] !== undefined) item.value = res.data[item.key];
      }
    }
  } catch { /* ignore */ }
}

async function onToggle(key, value) {
  saving.value = true;
  try {
    const res = await updateNotificationSettings({ [key]: value });
    if (res.code === 200) {
      const item = settings.find(s => s.key === key);
      if (item) item.value = value;
    }
  } catch { /* ignore */ }
  saving.value = false;
}

onMounted(() => loadSettings());
</script>

<style scoped lang="scss">
.settings-page { padding: 24rpx; }
.section { background: #fff; border-radius: 12rpx; overflow: hidden; }
.section-title { font-size: 30rpx; font-weight: 600; color: #303133; padding: 24rpx 24rpx 0; margin-bottom: 16rpx; }
.setting-item { display: flex; justify-content: space-between; align-items: center; padding: 24rpx; border-bottom: 1rpx solid #f0f2f5; }
.setting-item:last-child { border-bottom: none; }
.si-label { font-size: 28rpx; color: #303133; display: block; }
.si-desc { font-size: 24rpx; color: #909399; margin-top: 4rpx; display: block; }
</style>
```

- [ ] **Step 5: Verify** — 所有我的页编译通过。

---

### Task 9: Final — 构建验证

- [ ] **Step 1: 运行构建**

```bash
cd D:/Work/Personal/education/education-store/education-store-mini
npx uni build -p mp-weixin
```

Expected: Compiles successfully, no errors.

- [ ] **Step 2: 检查文件清单**

```bash
# 新文件数应为 20
find pages/teacher -type f | wc -l   # 12 pages
find src/api -name "teacher*" -o -name "checkin*"   # 4 API files
find src/stores -name "teacher*"    # 1 store
find src/components -name "schedule-card*"  # 1 component
find src/custom-tab-bar -type f     # 1 tab bar
# Modified: pages.json, App.vue, stores/auth.js
```

- [ ] **Step 3: Commit**

```bash
git add education-store-mini/
git commit -m "feat: 小程序教师端前端 — 4 Tab + 8 子页面 (20 文件)"
```

---

## Summary

| Task | Files | Description |
|:--:|------|------|
| 1 | 4 (3M+1C) | 自定义 TabBar + 角色路由 + auth store 改造 |
| 2 | 4 | API 层 (teacher/checkin/teacher-leave/teacher-evaluation) |
| 3 | 1 | Store (teacher.js) |
| 4 | 1 | 新组件 (schedule-card.vue) |
| 5 | 1 | Tab1 课表 (周/月视图) |
| 6 | 2 | Tab2 签到 (列表+批量) |
| 7 | 5 | Tab3 教学 (容器+评价+作业) |
| 8 | 4 | Tab4 我的 + 子页面 |
| 9 | 0 | 构建验证 + 提交 |
| **Total** | **22** | **20 new + 3 modified** |
