# 小程序家长端 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 从零搭建 uni-app 微信小程序家长端，包含 3 个 Tab + 10 个子页面，对接全部 16 个后端 API 端点。

**Architecture:** uni-app Vue 3 项目，独立目录 `education-store-mini/`。使用 Pinia 管理全局状态（认证 + 孩子列表），各页面按需调用 API。底部 3 Tab（首页时间线 / 消息通知偏好 / 我的孩子列表），子页面通过 `navigateTo` 跳转。

**Tech Stack:** uni-app 3.x (Vite + Vue 3 Composition API), uView Plus 3.x, Pinia, SCSS, wx.request 封装

**Backend reference:** `docs/api/10-小程序家长端.md` — 16 endpoints 全部就绪

---

## File Structure Preview

```
education-store-mini/
├── package.json
├── vite.config.js
├── pages.json                ← 路由 + Tab + 窗口配置
├── manifest.json             ← 微信小程序 appid 等
├── uni.scss                  ← 全局 SCSS 变量
├── App.vue                   ← 应用入口
├── main.js                   ← Vue 初始化
├── src/
│   ├── api/
│   │   ├── auth.js
│   │   ├── parent.js
│   │   ├── timeline.js
│   │   ├── attendance.js
│   │   ├── homework.js
│   │   ├── leave.js
│   │   ├── enrollment.js
│   │   ├── schedule.js
│   │   ├── exam.js
│   │   ├── evaluation.js
│   │   ├── points.js
│   │   └── notification.js
│   ├── stores/
│   │   ├── auth.js
│   │   └── children.js
│   ├── utils/
│   │   ├── request.js
│   │   └── index.js
│   └── components/
│       ├── child-switcher.vue
│       ├── empty-state.vue
│       ├── loading-skeleton.vue
│       └── page-header.vue
├── pages/
│   ├── index/
│   │   └── index.vue
│   ├── messages/
│   │   └── index.vue
│   ├── profile/
│   │   ├── index.vue
│   │   └── child-detail.vue
│   ├── attendance/
│   │   └── index.vue
│   ├── homework/
│   │   └── index.vue
│   ├── leave/
│   │   ├── index.vue
│   │   └── submit.vue
│   ├── enrollment/
│   │   └── index.vue
│   ├── schedule/
│   │   └── index.vue
│   ├── exam/
│   │   └── index.vue
│   ├── evaluation/
│   │   └── index.vue
│   └── points/
│       └── index.vue
```

---

### Task 1: Project Scaffolding

**Files:**
- Create: `education-store-mini/` directory + config files

- [ ] **Step 1: Create uni-app project using Vite template**

```bash
cd D:/Work/Personal/education/education-store
npx degit dcloudio/uni-preset-vue#vite education-store-mini
cd education-store-mini
pnpm install
```

- [ ] **Step 2: Add uView Plus + Pinia**

```bash
cd D:/Work/Personal/education/education-store/education-store-mini
pnpm add uview-plus pinia dayjs
```

- [ ] **Step 3: Configure vite.config.js**

```js
import { defineConfig } from 'vite';
import uni from '@dcloudio/vite-plugin-uni';
import uv from '@uni-helper/vite-plugin-uni-uv';

export default defineConfig({
  plugins: [uni(), uv()],
});
```

- [ ] **Step 4: Configure main.js**

```js
import { createSSRApp } from 'vue';
import { createPinia } from 'pinia';
import uviewPlus from 'uview-plus';
import App from './App.vue';

export function createApp() {
  const app = createSSRApp(App);
  const pinia = createPinia();
  app.use(pinia);
  app.use(uviewPlus);
  return { app };
}
```

- [ ] **Step 5: Verify** — `pnpm dev:mp-weixin` starts without error, compiles successfully.

---

### Task 2: Core — utils/request.js (HTTP 封装)

**Files:**
- Create: `education-store-mini/src/utils/request.js`

- [ ] **Step 1: Create request.js**

```js
import { useAuthStore } from '@/stores/auth';

const BASE_URL = 'http://localhost:8080';

function request(options = {}) {
  const authStore = useAuthStore();

  return new Promise((resolve, reject) => {
    uni.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data,
      header: {
        'Content-Type': 'application/json',
        ...(authStore.token ? { Authorization: `Bearer ${authStore.token}` } : {}),
      },
      success(res) {
        const { statusCode, data } = res;
        if (statusCode === 200) {
          resolve(data);
        } else if (statusCode === 401) {
          authStore.logout();
          uni.reLaunch({ url: '/pages/login/index' });
          reject(new Error('登录已过期'));
        } else {
          uni.showToast({ title: data.msg || '请求失败', icon: 'none' });
          reject(new Error(data.msg || `HTTP ${statusCode}`));
        }
      },
      fail(err) {
        uni.showToast({ title: '网络请求失败', icon: 'none' });
        reject(err);
      },
    });
  });
}

export default request;
```

- [ ] **Step 2: Verify** — file created, syntax valid.

---

### Task 3: Core — utils/index.js

**Files:**
- Create: `education-store-mini/src/utils/index.js`

- [ ] **Step 1: Create index.js**

```js
import dayjs from 'dayjs';

/** 格式化日期 YYYY-MM-DD */
export function formatDate(date) {
  if (!date) return '';
  return dayjs(date).format('YYYY-MM-DD');
}

/** 格式化日期时间 YYYY-MM-DD HH:mm */
export function formatDateTime(date) {
  if (!date) return '';
  return dayjs(date).format('YYYY-MM-DD HH:mm');
}

/** 相对时间（今天/昨天/N天前） */
export function fromNow(date) {
  if (!date) return '';
  const d = dayjs(date);
  const now = dayjs();
  const diff = now.diff(d, 'day');
  if (diff === 0) return '今天 ' + d.format('HH:mm');
  if (diff === 1) return '昨天 ' + d.format('HH:mm');
  if (diff < 7) return diff + '天前';
  return d.format('MM-DD HH:mm');
}

/** 事件类型 → 中文 + 颜色 */
export function eventMeta(type) {
  const map = {
    SIGN_IN: { label: '签到', color: '#67c23a' },
    EVALUATION: { label: '评价', color: '#e6a23c' },
    HOMEWORK: { label: '作业', color: '#409eff' },
    EXAM: { label: '成绩', color: '#9b59b6' },
    LEAVE: { label: '请假', color: '#f56c6c' },
  };
  return map[type] || { label: type || '--', color: '#909399' };
}

/** 状态文本映射 */
export const STATUS_MAP = {
  leave: { 0: '待审批', 1: '已通过', 2: '已拒绝' },
  attendance: { PRESENT: '出勤', LATE: '迟到', ABSENT: '缺勤', LEAVE: '请假' },
};
```

- [ ] **Step 2: Verify** — file created, no syntax errors.

---

### Task 4: Core — Pinia auth store

**Files:**
- Create: `education-store-mini/src/stores/auth.js`

- [ ] **Step 1: Create auth.js**

```js
import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { wechatLogin } from '@/api/auth';

export const useAuthStore = defineStore('auth', () => {
  const token = ref(uni.getStorageSync('token') || '');
  const userInfo = ref(null);
  const isLogin = computed(() => !!token.value);

  async function login() {
    return new Promise((resolve, reject) => {
      uni.login({
        provider: 'weixin',
        success: async (loginRes) => {
          try {
            const res = await wechatLogin({ code: loginRes.code });
            if (res.code === 200) {
              token.value = res.data.token || res.data.access_token;
              userInfo.value = res.data.userInfo || res.data;
              uni.setStorageSync('token', token.value);
              resolve(res.data);
            } else {
              reject(new Error(res.msg || '登录失败'));
            }
          } catch (e) {
            reject(e);
          }
        },
        fail: (err) => {
          reject(err);
        },
      });
    });
  }

  function logout() {
    token.value = '';
    userInfo.value = null;
    uni.removeStorageSync('token');
    uni.reLaunch({ url: '/pages/login/index' });
  }

  // 检查本地 token 是否有效（启动时调用）
  function checkAuth() {
    if (!token.value) return false;
    return true;
  }

  return { token, userInfo, isLogin, login, logout, checkAuth };
});
```

- [ ] **Step 2: Verify** — file created, store methods complete.

---

### Task 5: Core — Pinia children store

**Files:**
- Create: `education-store-mini/src/stores/children.js`

- [ ] **Step 1: Create children.js**

```js
import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { getChildren } from '@/api/parent';

export const useChildrenStore = defineStore('children', () => {
  const children = ref([]);
  const currentChildId = ref(null);
  const loading = ref(false);

  const currentChild = computed(() =>
    children.value.find((c) => c.studentId === currentChildId.value) || children.value[0] || null
  );

  async function fetchChildren() {
    loading.value = true;
    try {
      const res = await getChildren();
      if (res.code === 200) {
        children.value = res.data || [];
        if (!currentChildId.value && children.value.length > 0) {
          currentChildId.value = children.value[0].studentId;
        }
      }
    } catch {
      // ignore — handled by request interceptor
    } finally {
      loading.value = false;
    }
  }

  function setCurrentChild(childId) {
    currentChildId.value = childId;
  }

  return { children, currentChildId, currentChild, loading, fetchChildren, setCurrentChild };
});
```

- [ ] **Step 2: Verify** — store methods complete, computed derivation correct.

---

### Task 6: API Layer (batch — all 12 files)

**Files:**
- Create: `education-store-mini/src/api/auth.js`
- Create: `education-store-mini/src/api/parent.js`
- Create: `education-store-mini/src/api/timeline.js`
- Create: `education-store-mini/src/api/attendance.js`
- Create: `education-store-mini/src/api/homework.js`
- Create: `education-store-mini/src/api/leave.js`
- Create: `education-store-mini/src/api/enrollment.js`
- Create: `education-store-mini/src/api/schedule.js`
- Create: `education-store-mini/src/api/exam.js`
- Create: `education-store-mini/src/api/evaluation.js`
- Create: `education-store-mini/src/api/points.js`
- Create: `education-store-mini/src/api/notification.js`

- [ ] **Step 1: Create auth.js**

```js
import request from '@/utils/request';

/** 微信静默登录（code → JWT） */
export function wechatLogin(data) {
  return request({ url: '/api/v1/auth/wechat-login', method: 'POST', data });
}

/** 微信手机号绑定登录 */
export function wechatBind(data) {
  return request({ url: '/api/v1/auth/wechat-bind', method: 'POST', data });
}

/** 刷新 Token */
export function refreshToken() {
  return request({ url: '/api/v1/auth/refresh', method: 'POST' });
}

/** 退出登录 */
export function logout() {
  return request({ url: '/api/v1/auth/logout', method: 'POST' });
}
```

- [ ] **Step 2: Create parent.js**

```js
import request from '@/utils/request';

/** 获取关联孩子列表 */
export function getChildren() {
  return request({ url: '/api/v1/parent/children', method: 'GET' });
}

/** 获取孩子详情 */
export function getChildDetail(studentId) {
  return request({ url: `/api/v1/parent/children/${studentId}`, method: 'GET' });
}
```

- [ ] **Step 3: Create timeline.js**

```js
import request from '@/utils/request';

/** 首页时间线 */
export function getTimeline(params) {
  return request({ url: '/api/v1/parent/timeline', method: 'GET', data: params });
}
```

- [ ] **Step 4: Create attendance.js**

```js
import request from '@/utils/request';

/** 考勤记录 */
export function getAttendance(studentId, params) {
  return request({ url: `/api/v1/parent/children/${studentId}/attendance`, method: 'GET', data: params });
}

/** 月考勤统计 */
export function getAttendanceStats(studentId) {
  return request({ url: `/api/v1/parent/children/${studentId}/attendance-stats`, method: 'GET' });
}
```

- [ ] **Step 5: Create homework.js**

```js
import request from '@/utils/request';

/** 作业列表 */
export function getHomework(studentId, params) {
  return request({ url: `/api/v1/parent/children/${studentId}/homework`, method: 'GET', data: params });
}
```

- [ ] **Step 6: Create leave.js**

```js
import request from '@/utils/request';

/** 请假记录 */
export function getLeaves(studentId) {
  return request({ url: `/api/v1/parent/children/${studentId}/leaves`, method: 'GET' });
}

/** 发起请假 */
export function submitLeave(studentId, data) {
  return request({ url: `/api/v1/parent/children/${studentId}/leaves`, method: 'POST', data });
}
```

- [ ] **Step 7: Create enrollment.js**

```js
import request from '@/utils/request';

/** 报名记录 */
export function getEnrollments(studentId) {
  return request({ url: `/api/v1/parent/children/${studentId}/enrollments`, method: 'GET' });
}
```

- [ ] **Step 8: Create schedule.js**

```js
import request from '@/utils/request';

/** 课表 */
export function getSchedule(studentId, params) {
  return request({ url: `/api/v1/parent/children/${studentId}/schedule`, method: 'GET', data: params });
}
```

- [ ] **Step 9: Create exam.js**

```js
import request from '@/utils/request';

/** 考试成绩 */
export function getExamScores(studentId) {
  return request({ url: `/api/v1/parent/children/${studentId}/exam-scores`, method: 'GET' });
}
```

- [ ] **Step 10: Create evaluation.js**

```js
import request from '@/utils/request';

/** 课堂评价 */
export function getEvaluations(studentId) {
  return request({ url: `/api/v1/parent/children/${studentId}/evaluations`, method: 'GET' });
}
```

- [ ] **Step 11: Create points.js**

```js
import request from '@/utils/request';

/** 积分余额 */
export function getPoints(studentId) {
  return request({ url: `/api/v1/parent/children/${studentId}/points`, method: 'GET' });
}

/** 积分日志 */
export function getPointsLog(studentId) {
  return request({ url: `/api/v1/parent/children/${studentId}/points/log`, method: 'GET' });
}
```

- [ ] **Step 12: Create notification.js**

```js
import request from '@/utils/request';

/** 获取通知偏好 */
export function getNotificationSettings() {
  return request({ url: '/api/v1/user/notification-settings', method: 'GET' });
}

/** 更新通知偏好 */
export function updateNotificationSettings(data) {
  return request({ url: '/api/v1/user/notification-settings', method: 'PUT', data });
}
```

- [ ] **Step 13: Verify** — all 12 API files created.

---

### Task 7: Components (batch — all 4)

**Files:**
- Create: `education-store-mini/src/components/child-switcher.vue`
- Create: `education-store-mini/src/components/empty-state.vue`
- Create: `education-store-mini/src/components/loading-skeleton.vue`
- Create: `education-store-mini/src/components/page-header.vue`

- [ ] **Step 1: Create child-switcher.vue**

```vue
<!-- 孩子切换下拉 -->
<template>
  <view v-if="children.length > 1" class="child-switcher">
    <picker mode="selector" :range="childNames" :value="currentIndex" @change="onChange">
      <view class="switcher-btn">
        <text class="switcher-label">{{ currentLabel }}</text>
        <text class="switcher-arrow">▼</text>
      </view>
    </picker>
  </view>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  children: { type: Array, default: () => [] },
  currentId: { type: [Number, String], default: null },
});

const emit = defineEmits(['change']);

const childNames = computed(() => props.children.map((c) => c.name || '未命名'));
const currentIndex = computed(() => {
  const idx = props.children.findIndex((c) => c.studentId === props.currentId);
  return idx >= 0 ? idx : 0;
});
const currentLabel = computed(() => {
  const c = props.children.find((c) => c.studentId === props.currentId);
  return c ? `👶 ${c.name}` : '选择孩子';
});

function onChange(e) {
  const child = props.children[e.detail.value];
  if (child) emit('change', child.studentId);
}
</script>

<style scoped lang="scss">
.child-switcher {
  display: flex;
  justify-content: center;
  padding: 16rpx 0;
}
.switcher-btn {
  display: flex;
  align-items: center;
  gap: 12rpx;
  padding: 12rpx 32rpx;
  background: #f5f7fa;
  border-radius: 40rpx;
}
.switcher-label { font-size: 28rpx; color: #303133; font-weight: 500; }
.switcher-arrow { font-size: 20rpx; color: #909399; }
</style>
```

- [ ] **Step 2: Create empty-state.vue**

```vue
<!-- 空状态/错误状态 -->
<template>
  <view class="empty-state">
    <text class="empty-icon">{{ icon }}</text>
    <text class="empty-text">{{ text }}</text>
    <view v-if="showRetry" class="empty-retry" @click="$emit('retry')">
      <text>点击重试</text>
    </view>
  </view>
</template>

<script setup>
defineProps({
  icon: { type: String, default: '📭' },
  text: { type: String, default: '暂无数据' },
  showRetry: { type: Boolean, default: false },
});
defineEmits(['retry']);
</script>

<style scoped lang="scss">
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120rpx 0;
}
.empty-icon { font-size: 80rpx; margin-bottom: 24rpx; }
.empty-text { font-size: 28rpx; color: #909399; }
.empty-retry { margin-top: 32rpx; padding: 16rpx 48rpx; background: #409eff; border-radius: 40rpx; color: #fff; font-size: 26rpx; }
</style>
```

- [ ] **Step 3: Create loading-skeleton.vue**

```vue
<!-- 列表骨架屏 -->
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
```

- [ ] **Step 4: Create page-header.vue**

```vue
<!-- 页面标题栏（自定义导航栏场景） -->
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
```

- [ ] **Step 5: Verify** — all 4 component files created.

---

### Task 8: App.vue + pages.json + manifest.json + uni.scss

**Files:**
- Modify: `education-store-mini/App.vue`
- Modify: `education-store-mini/pages.json`
- Modify: `education-store-mini/manifest.json`
- Modify: `education-store-mini/uni.scss`

- [ ] **Step 1: Write App.vue**

```vue
<script setup>
import { onLaunch } from '@dcloudio/uni-app';
import { useAuthStore } from '@/stores/auth';
import { useChildrenStore } from '@/stores/children';

onLaunch(async () => {
  const authStore = useAuthStore();
  if (authStore.checkAuth()) {
    const childrenStore = useChildrenStore();
    await childrenStore.fetchChildren();
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

- [ ] **Step 2: Write pages.json**

```json
{
  "easycom": {
    "autoscan": true,
    "custom": {
      "^u-(.*)": "uview-plus/components/u-$1/u-$1.vue"
    }
  },
  "pages": [
    {
      "path": "pages/index/index",
      "style": { "navigationBarTitleText": "首页" }
    },
    {
      "path": "pages/messages/index",
      "style": { "navigationBarTitleText": "消息" }
    },
    {
      "path": "pages/profile/index",
      "style": { "navigationBarTitleText": "我的" }
    },
    {
      "path": "pages/profile/child-detail",
      "style": { "navigationBarTitleText": "孩子详情" }
    },
    {
      "path": "pages/attendance/index",
      "style": { "navigationBarTitleText": "考勤记录" }
    },
    {
      "path": "pages/homework/index",
      "style": { "navigationBarTitleText": "课后作业" }
    },
    {
      "path": "pages/leave/index",
      "style": { "navigationBarTitleText": "请假记录" }
    },
    {
      "path": "pages/leave/submit",
      "style": { "navigationBarTitleText": "发起请假" }
    },
    {
      "path": "pages/enrollment/index",
      "style": { "navigationBarTitleText": "报名记录" }
    },
    {
      "path": "pages/schedule/index",
      "style": { "navigationBarTitleText": "课程表" }
    },
    {
      "path": "pages/exam/index",
      "style": { "navigationBarTitleText": "考试成绩" }
    },
    {
      "path": "pages/evaluation/index",
      "style": { "navigationBarTitleText": "课堂评价" }
    },
    {
      "path": "pages/points/index",
      "style": { "navigationBarTitleText": "积分中心" }
    }
  ],
  "globalStyle": {
    "navigationBarTextStyle": "black",
    "navigationBarTitleText": "教育管理",
    "navigationBarBackgroundColor": "#ffffff",
    "backgroundColor": "#f5f7fa"
  },
  "tabBar": {
    "color": "#909399",
    "selectedColor": "#409eff",
    "borderStyle": "black",
    "backgroundColor": "#ffffff",
    "list": [
      {
        "pagePath": "pages/index/index",
        "text": "首页",
        "iconPath": "static/tab/home.png",
        "selectedIconPath": "static/tab/home-active.png"
      },
      {
        "pagePath": "pages/messages/index",
        "text": "消息",
        "iconPath": "static/tab/bell.png",
        "selectedIconPath": "static/tab/bell-active.png"
      },
      {
        "pagePath": "pages/profile/index",
        "text": "我的",
        "iconPath": "static/tab/user.png",
        "selectedIconPath": "static/tab/user-active.png"
      }
    ]
  }
}
```

- [ ] **Step 3: Write manifest.json**

```json
{
  "name": "教育管理",
  "appid": "__UNI__XXXXXXX",
  "description": "在线教育课程管理平台",
  "versionName": "1.0.0",
  "versionCode": "100",
  "transformPx": false,
  "mp-weixin": {
    "appid": "wx0000000000000000",
    "setting": {
      "urlCheck": false,
      "es6": true,
      "postcss": false,
      "minified": false
    },
    "usingComponents": true,
    "permission": {}
  }
}
```

- [ ] **Step 4: Write uni.scss**

```scss
/* 全局 SCSS 变量 */
$primary-color: #409eff;
$success-color: #67c23a;
$warning-color: #e6a23c;
$danger-color: #f56c6c;
$info-color: #909399;

$text-primary: #303133;
$text-regular: #606266;
$text-secondary: #909399;
$text-placeholder: #c0c4cc;

$border-color: #ebeef5;
$bg-color: #f5f7fa;
$bg-white: #ffffff;

$font-size-xs: 22rpx;
$font-size-sm: 24rpx;
$font-size-base: 28rpx;
$font-size-lg: 32rpx;
$font-size-xl: 36rpx;

$spacing-xs: 8rpx;
$spacing-sm: 16rpx;
$spacing-base: 24rpx;
$spacing-lg: 32rpx;
$spacing-xl: 48rpx;

$border-radius: 12rpx;
```

- [ ] **Step 5: Create tab bar icon placeholders**

```bash
mkdir -p D:/Work/Personal/education/education-store/education-store-mini/static/tab
```

Create simple PNG icons for tab bar (6 files: home.png, home-active.png, bell.png, bell-active.png, user.png, user-active.png). These are placeholder 81×81px PNGs — any solid-color icon will work; replace with actual designs later.

- [ ] **Step 6: Verify** — `pnpm dev:mp-weixin` compiles, tab bar visible, pages navigable.

---

### Task 9: Login Page

**Files:**
- Create: `education-store-mini/pages/login/index.vue`
- Modify: `education-store-mini/pages.json` — add login page entry

- [ ] **Step 1: Add login page to pages.json**

In `pages.json`, add as first entry in `pages` array (before index):

```json
{
  "path": "pages/login/index",
  "style": { "navigationBarTitleText": "" }
}
```

- [ ] **Step 2: Create login/index.vue**

```vue
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
.login-btn { width: 100%; height: 88rpx; line-height: 88rpx; border-radius: 44rpx; background: #07c160; font-size: 32rpx; }
.login-btn::after { border: none; }
.login-tip { font-size: 22rpx; color: #c0c4cc; margin-top: 24rpx; text-align: center; }
</style>
```

- [ ] **Step 3: Verify** — login page renders, button triggers wx.login flow.

---

### Task 10: 首页 — pages/index/index.vue

**Files:**
- Create: `education-store-mini/pages/index/index.vue`

- [ ] **Step 1: Create index.vue**

```vue
<template>
  <view class="index-page">
    <!-- 孩子切换 -->
    <child-switcher :children="childrenStore.children" :current-id="childrenStore.currentChildId" @change="onChildChange" />

    <!-- 时间线 -->
    <view v-if="loading" class="timeline-wrap">
      <loading-skeleton :rows="3" />
    </view>

    <view v-else-if="error" class="timeline-wrap">
      <empty-state icon="⚠️" text="加载失败" :show-retry="true" @retry="loadTimeline" />
    </view>

    <view v-else-if="!events.length" class="timeline-wrap">
      <empty-state icon="📭" text="暂无动态" />
    </view>

    <view v-else class="timeline-wrap">
      <view v-for="group in groupedEvents" :key="group.childId" class="tl-group">
        <view v-if="groupedEvents.length > 1" class="tl-child-label">{{ group.childName }}</view>
        <view v-for="evt in group.events" :key="evt.id || evt.createTime" class="tl-card">
          <view class="tl-left">
            <view class="tl-dot" :style="{ background: eventMeta(evt.eventType).color }" />
            <view v-if="evt !== group.events[group.events.length - 1]" class="tl-line" />
          </view>
          <view class="tl-right">
            <view class="tl-header">
              <text class="tl-type" :style="{ color: eventMeta(evt.eventType).color }">{{ eventMeta(evt.eventType).label }}</text>
              <text class="tl-time">{{ fromNow(evt.createTime) }}</text>
            </view>
            <text class="tl-content">{{ evt.summary || evt.title || evt.content || '--' }}</text>
            <view v-if="evt.starRating" class="tl-stars">
              <text v-for="i in 5" :key="i">{{ i <= evt.starRating ? '⭐' : '☆' }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue';
import { onShow, onPullDownRefresh, onReachBottom } from '@dcloudio/uni-app';
import { useChildrenStore } from '@/stores/children';
import { getTimeline } from '@/api/timeline';
import { eventMeta, fromNow } from '@/utils/index';
import ChildSwitcher from '@/components/child-switcher.vue';
import EmptyState from '@/components/empty-state.vue';
import LoadingSkeleton from '@/components/loading-skeleton.vue';

const childrenStore = useChildrenStore();
const events = ref([]);
const loading = ref(true);
const error = ref(false);
const page = ref(1);
const hasMore = ref(true);

const groupedEvents = computed(() => {
  const map = {};
  for (const evt of events.value) {
    const cid = evt.studentId || evt.childId || 0;
    if (!map[cid]) map[cid] = { childId: cid, childName: evt.studentName || '孩子' + cid, events: [] };
    map[cid].events.push(evt);
  }
  return Object.values(map);
});

async function loadTimeline(isRefresh = false) {
  if (isRefresh) {
    page.value = 1;
    hasMore.value = true;
    error.value = false;
  }
  if (!hasMore.value && !isRefresh) return;

  loading.value = true;
  try {
    const res = await getTimeline({ days: 30, limit: 20, page: page.value });
    if (res.code === 200) {
      const data = res.data || [];
      if (isRefresh || page.value === 1) events.value = [];
      events.value.push(...data);
      hasMore.value = data.length >= 20;
      page.value++;
    }
  } catch {
    if (page.value === 1 || isRefresh) error.value = true;
  } finally {
    loading.value = false;
  }
}

function onChildChange(childId) {
  childrenStore.setCurrentChild(childId);
}

onShow(() => {
  if (!events.value.length) loadTimeline(true);
});

onPullDownRefresh(async () => {
  await loadTimeline(true);
  uni.stopPullDownRefresh();
});

onReachBottom(() => {
  if (!loading.value && hasMore.value) loadTimeline();
});
</script>

<style scoped lang="scss">
.index-page { min-height: 100vh; }
.timeline-wrap { padding: 0 24rpx 24rpx; }

.tl-group { margin-bottom: 8rpx; }
.tl-child-label {
  font-size: 24rpx;
  color: #909399;
  padding: 16rpx 0 8rpx 56rpx;
}

.tl-card { display: flex; }
.tl-left {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 56rpx;
  flex-shrink: 0;
}
.tl-dot {
  width: 16rpx;
  height: 16rpx;
  border-radius: 50%;
  margin-top: 8rpx;
}
.tl-line {
  flex: 1;
  width: 2rpx;
  background: #ebeef5;
}
.tl-right {
  flex: 1;
  padding: 8rpx 0 32rpx 16rpx;
  min-width: 0;
}
.tl-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8rpx;
}
.tl-type { font-size: 26rpx; font-weight: 600; }
.tl-time { font-size: 22rpx; color: #c0c4cc; }
.tl-content { font-size: 28rpx; color: #303133; line-height: 1.6; }
.tl-stars { margin-top: 8rpx; font-size: 24rpx; }
</style>
```

- [ ] **Step 2: Verify** — timeline renders grouped events, pull-to-refresh works, pagination works.

---

### Task 11: 消息 — pages/messages/index.vue

**Files:**
- Create: `education-store-mini/pages/messages/index.vue`

- [ ] **Step 1: Create messages/index.vue**

```vue
<template>
  <view class="messages-page">
    <!-- 通知偏好设置 -->
    <view class="section">
      <view class="section-title">通知偏好设置</view>
      <view class="setting-list">
        <view v-for="item in settings" :key="item.key" class="setting-item">
          <view class="setting-info">
            <text class="setting-label">{{ item.label }}</text>
            <text class="setting-desc">{{ item.desc }}</text>
          </view>
          <switch :checked="item.value" :disabled="saving" @change="(e) => onToggle(item.key, e.detail.value)" color="#409eff" />
        </view>
      </view>
    </view>

    <!-- 占位提示 -->
    <view class="section">
      <view class="placeholder-card">
        <text class="placeholder-icon">📬</text>
        <text class="placeholder-text">消息推送功能即将上线</text>
        <text class="placeholder-sub">开通后可在微信中接收孩子的学习动态通知</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { getNotificationSettings, updateNotificationSettings } from '@/api/notification';

const saving = ref(false);

const settingDefs = [
  { key: 'SIGN_IN', label: '签到通知', desc: '孩子上课签到后推送' },
  { key: 'EVALUATION', label: '评价通知', desc: '收到课堂评价时推送' },
  { key: 'HOMEWORK', label: '作业通知', desc: '新作业发布时推送' },
  { key: 'EXAM', label: '成绩通知', desc: '新成绩录入时推送' },
  { key: 'LEAVE', label: '请假通知', desc: '请假审批结果推送' },
  { key: 'REDEMPTION', label: '兑换通知', desc: '积分兑换状态推送' },
];

const settings = reactive(settingDefs.map((d) => ({ ...d, value: true })));

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
  const patch = { [key]: value };
  try {
    const res = await updateNotificationSettings(patch);
    if (res.code === 200) {
      const item = settings.find((s) => s.key === key);
      if (item) item.value = value;
    }
  } catch { /* ignore */ }
  saving.value = false;
}

onMounted(() => { loadSettings(); });
</script>

<style scoped lang="scss">
.messages-page { padding: 24rpx; min-height: 100vh; }

.section {
  background: #fff;
  border-radius: 12rpx;
  margin-bottom: 24rpx;
  overflow: hidden;
}
.section-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #303133;
  padding: 24rpx 24rpx 0;
  margin-bottom: 16rpx;
}

.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx;
  border-bottom: 1rpx solid #f0f2f5;
  &:last-child { border-bottom: none; }
}
.setting-info { flex: 1; }
.setting-label { font-size: 28rpx; color: #303133; }
.setting-desc { font-size: 24rpx; color: #909399; margin-top: 4rpx; display: block; }

.placeholder-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 64rpx 48rpx;
}
.placeholder-icon { font-size: 80rpx; margin-bottom: 24rpx; }
.placeholder-text { font-size: 30rpx; color: #303133; font-weight: 500; }
.placeholder-sub { font-size: 24rpx; color: #909399; margin-top: 8rpx; text-align: center; }
</style>
```

- [ ] **Step 2: Verify** — settings load, toggles work, PUT updates backend.

---

### Task 12: 我的 — pages/profile/index.vue

**Files:**
- Create: `education-store-mini/pages/profile/index.vue`

- [ ] **Step 1: Create profile/index.vue**

```vue
<template>
  <view class="profile-page">
    <!-- 孩子列表 -->
    <view v-if="childrenStore.loading" class="section">
      <loading-skeleton :rows="2" />
    </view>

    <view v-else-if="!childrenStore.children.length" class="section">
      <empty-state icon="👶" text="暂无关联孩子" />
    </view>

    <view v-else class="section">
      <view class="section-title">我的孩子</view>
      <view v-for="child in childrenStore.children" :key="child.studentId" class="child-card" @click="goChildDetail(child.studentId)">
        <image class="child-avatar" :src="child.avatar || '/static/default-avatar.png'" mode="aspectFill" />
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

    <!-- 快捷入口 -->
    <view class="section">
      <view class="section-title">快捷入口</view>
      <view class="menu-list">
        <view class="menu-item" @click="goPage('/pages/attendance/index')">
          <text class="menu-label">📋 考勤记录</text>
          <text class="menu-arrow">›</text>
        </view>
        <view class="menu-item" @click="goPage('/pages/homework/index')">
          <text class="menu-label">📝 课后作业</text>
          <text class="menu-arrow">›</text>
        </view>
        <view class="menu-item" @click="goPage('/pages/schedule/index')">
          <text class="menu-label">📅 课程表</text>
          <text class="menu-arrow">›</text>
        </view>
        <view class="menu-item" @click="goPage('/pages/exam/index')">
          <text class="menu-label">📊 考试成绩</text>
          <text class="menu-arrow">›</text>
        </view>
        <view class="menu-item" @click="goPage('/pages/points/index')">
          <text class="menu-label">⭐ 积分中心</text>
          <text class="menu-arrow">›</text>
        </view>
      </view>
    </view>

    <!-- 退出登录 -->
    <view class="section">
      <view class="logout-btn" @click="handleLogout">
        <text>退出登录</text>
      </view>
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

.section {
  background: #fff;
  border-radius: 12rpx;
  margin-bottom: 24rpx;
  overflow: hidden;
}
.section-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #303133;
  padding: 24rpx 24rpx 16rpx;
}

.child-card {
  display: flex;
  align-items: center;
  padding: 24rpx;
  border-bottom: 1rpx solid #f0f2f5;
  &:last-child { border-bottom: none; }
}
.child-avatar { width: 96rpx; height: 96rpx; border-radius: 50%; background: #f0f2f5; flex-shrink: 0; }
.child-info { flex: 1; margin-left: 20rpx; min-width: 0; }
.child-name { font-size: 32rpx; font-weight: 600; color: #303133; display: block; }
.child-meta { font-size: 24rpx; color: #909399; margin-top: 4rpx; display: block; }
.child-hours { font-size: 24rpx; color: #606266; margin-top: 6rpx; }
.hours-num { color: #409eff; font-weight: 600; font-size: 28rpx; }
.child-arrow { font-size: 40rpx; color: #c0c4cc; padding: 0 8rpx; }

.menu-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 28rpx 24rpx;
  border-bottom: 1rpx solid #f0f2f5;
  &:last-child { border-bottom: none; }
}
.menu-label { font-size: 28rpx; color: #303133; }
.menu-arrow { font-size: 36rpx; color: #c0c4cc; }

.logout-btn {
  display: flex;
  justify-content: center;
  padding: 28rpx;
  color: #f56c6c;
  font-size: 30rpx;
}
</style>
```

- [ ] **Step 2: Verify** — children list renders, menu navigation works, logout works.

---

### Task 13: 我的 — pages/profile/child-detail.vue

**Files:**
- Create: `education-store-mini/pages/profile/child-detail.vue`

- [ ] **Step 1: Create child-detail.vue**

```vue
<template>
  <view class="child-detail-page">
    <view v-if="loading"><loading-skeleton :rows="3" /></view>

    <view v-else-if="error">
      <empty-state icon="⚠️" text="加载失败" :show-retry="true" @retry="loadDetail" />
    </view>

    <view v-else-if="!detail" class="section">
      <empty-state icon="👶" text="孩子信息不存在" />
    </view>

    <template v-else>
      <!-- 基本信息 -->
      <view class="section">
        <view class="info-header">
          <image class="info-avatar" :src="detail.avatar || '/static/default-avatar.png'" mode="aspectFill" />
          <view class="info-main">
            <text class="info-name">{{ detail.name }}</text>
            <text class="info-grade">{{ detail.grade || '--' }}</text>
          </view>
        </view>
        <view class="info-grid">
          <view class="info-item">
            <text class="info-label">性别</text>
            <text class="info-value">{{ detail.gender === 1 ? '男' : detail.gender === 2 ? '女' : '--' }}</text>
          </view>
          <view class="info-item">
            <text class="info-label">学校</text>
            <text class="info-value">{{ detail.school || '--' }}</text>
          </view>
          <view class="info-item">
            <text class="info-label">总课时</text>
            <text class="info-value">{{ detail.totalHours || 0 }}</text>
          </view>
          <view class="info-item">
            <text class="info-label">剩余课时</text>
            <text class="info-value primary">{{ detail.remainingHours || 0 }}</text>
          </view>
        </view>
      </view>

      <!-- 在读班级 -->
      <view v-if="detail.classes && detail.classes.length" class="section">
        <view class="section-title">在读班级</view>
        <view v-for="cls in detail.classes" :key="cls.classId || cls.id" class="class-item">
          <text class="class-name">{{ cls.className || cls.name || '--' }}</text>
          <text class="class-course">{{ cls.courseName || '--' }}</text>
        </view>
      </view>
    </template>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { getChildDetail } from '@/api/parent';
import EmptyState from '@/components/empty-state.vue';
import LoadingSkeleton from '@/components/loading-skeleton.vue';

const loading = ref(true);
const error = ref(false);
const detail = ref(null);
let studentId = null;

onLoad((options) => {
  studentId = options.studentId;
  loadDetail();
});

async function loadDetail() {
  if (!studentId) return;
  loading.value = true;
  error.value = false;
  try {
    const res = await getChildDetail(studentId);
    if (res.code === 200) {
      detail.value = res.data;
    }
  } catch {
    error.value = true;
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped lang="scss">
.child-detail-page { padding: 24rpx; min-height: 100vh; }

.section {
  background: #fff;
  border-radius: 12rpx;
  margin-bottom: 24rpx;
  overflow: hidden;
}
.section-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #303133;
  padding: 24rpx 24rpx 16rpx;
}

.info-header {
  display: flex;
  align-items: center;
  padding: 32rpx 24rpx 24rpx;
}
.info-avatar { width: 120rpx; height: 120rpx; border-radius: 50%; background: #f0f2f5; }
.info-main { margin-left: 24rpx; }
.info-name { font-size: 36rpx; font-weight: 700; color: #303133; display: block; }
.info-grade { font-size: 26rpx; color: #909399; margin-top: 8rpx; display: block; }

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  padding: 0 24rpx 24rpx;
  gap: 24rpx;
}
.info-item { display: flex; flex-direction: column; }
.info-label { font-size: 24rpx; color: #909399; margin-bottom: 8rpx; }
.info-value { font-size: 30rpx; color: #303133; font-weight: 500; }

.class-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 24rpx;
  border-top: 1rpx solid #f0f2f5;
}
.class-name { font-size: 28rpx; color: #303133; font-weight: 500; }
.class-course { font-size: 24rpx; color: #909399; }
</style>
```

- [ ] **Step 2: Verify** — child detail loads, basic info + class list displayed.

---

### Task 14: 考勤 — pages/attendance/index.vue

**Files:**
- Create: `education-store-mini/pages/attendance/index.vue`

- [ ] **Step 1: Create attendance/index.vue**

```vue
<template>
  <view class="sub-page">
    <child-switcher v-if="childrenStore.children.length > 1" :children="childrenStore.children" :current-id="currentChildId" @change="onChildChange" />

    <!-- 统计卡片 -->
    <view v-if="stats" class="stats-row">
      <view class="stat-card stat-present">
        <text class="stat-num">{{ stats.present }}</text>
        <text class="stat-label">出勤</text>
      </view>
      <view class="stat-card stat-late">
        <text class="stat-num">{{ stats.late }}</text>
        <text class="stat-label">迟到</text>
      </view>
      <view class="stat-card stat-absent">
        <text class="stat-num">{{ stats.absent }}</text>
        <text class="stat-label">缺勤</text>
      </view>
      <view class="stat-card stat-leave">
        <text class="stat-num">{{ stats.leave }}</text>
        <text class="stat-label">请假</text>
      </view>
    </view>

    <!-- 月份切换 -->
    <view class="month-bar">
      <text class="month-arrow" @click="prevMonth">‹</text>
      <text class="month-label">{{ yearMonth }}</text>
      <text class="month-arrow" @click="nextMonth">›</text>
    </view>

    <!-- 考勤列表 -->
    <view v-if="loading"><loading-skeleton :rows="4" /></view>
    <view v-else-if="error"><empty-state icon="⚠️" text="加载失败" :show-retry="true" @retry="loadData" /></view>
    <view v-else-if="!list.length"><empty-state icon="📋" text="本月暂无考勤记录" /></view>

    <view v-else class="record-list">
      <view v-for="item in list" :key="item.id" class="record-card">
        <view class="rc-left">
          <text class="rc-date">{{ item.attendanceDate || item.date || '--' }}</text>
          <text class="rc-time" v-if="item.signinTime">{{ item.signinTime }}</text>
        </view>
        <view class="rc-right">
          <text class="rc-status" :style="{ color: statusColor(item.statusText || item.status) }">
            {{ item.statusText || statusMap[item.status] || item.status || '--' }}
          </text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue';
import { onLoad, onReachBottom, onPullDownRefresh } from '@dcloudio/uni-app';
import { useChildrenStore } from '@/stores/children';
import { getAttendance, getAttendanceStats } from '@/api/attendance';
import { STATUS_MAP } from '@/utils/index';
import ChildSwitcher from '@/components/child-switcher.vue';
import EmptyState from '@/components/empty-state.vue';
import LoadingSkeleton from '@/components/loading-skeleton.vue';

const childrenStore = useChildrenStore();
const statusMap = STATUS_MAP.attendance;

const currentChildId = ref(null);
const stats = ref(null);
const list = ref([]);
const loading = ref(true);
const error = ref(false);
const page = ref(1);
const hasMore = ref(true);

const now = new Date();
const yearMonth = ref(now.getFullYear() + '-' + String(now.getMonth() + 1).padStart(2, '0'));

function statusColor(status) {
  const map = { 出勤: '#67c23a', 迟到: '#e6a23c', 缺勤: '#f56c6c', 请假: '#409eff' };
  return map[status] || '#909399';
}

onLoad((options) => {
  if (options.studentId) currentChildId.value = Number(options.studentId);
  else currentChildId.value = childrenStore.currentChildId;
  loadData(true);
});

async function loadData(isRefresh) {
  if (isRefresh) { page.value = 1; hasMore.value = true; error.value = false; }
  if (!currentChildId.value) return;

  loading.value = true;
  try {
    const [attRes, statsRes] = await Promise.all([
      getAttendance(currentChildId.value, { page: page.value, pageSize: 20, yearMonth: yearMonth.value }),
      page.value === 1 ? getAttendanceStats(currentChildId.value) : Promise.resolve(null),
    ]);

    if (attRes.code === 200) {
      const data = attRes.data?.records || attRes.data || [];
      if (isRefresh || page.value === 1) list.value = [];
      list.value.push(...data);
      hasMore.value = data.length >= 20;
      page.value++;
    }
    if (statsRes && statsRes.code === 200) {
      stats.value = statsRes.data;
    }
  } catch {
    if (page.value === 1 || isRefresh) error.value = true;
  } finally {
    loading.value = false;
  }
}

function prevMonth() {
  const [y, m] = yearMonth.value.split('-').map(Number);
  const d = new Date(y, m - 2, 1);
  yearMonth.value = d.getFullYear() + '-' + String(d.getMonth() + 1).padStart(2, '0');
  loadData(true);
}

function nextMonth() {
  const [y, m] = yearMonth.value.split('-').map(Number);
  const d = new Date(y, m, 1);
  yearMonth.value = d.getFullYear() + '-' + String(d.getMonth() + 1).padStart(2, '0');
  loadData(true);
}

function onChildChange(childId) {
  currentChildId.value = childId;
  loadData(true);
}

onPullDownRefresh(async () => { await loadData(true); uni.stopPullDownRefresh(); });
onReachBottom(() => { if (!loading.value && hasMore.value) loadData(); });
</script>

<style scoped lang="scss">
.sub-page { min-height: 100vh; padding-bottom: 24rpx; }

.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12rpx;
  padding: 24rpx;
}
.stat-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 24rpx 8rpx;
  border-radius: 12rpx;
  background: #fff;
}
.stat-num { font-size: 40rpx; font-weight: 700; }
.stat-label { font-size: 22rpx; color: #909399; margin-top: 4rpx; }
.stat-present .stat-num { color: #67c23a; }
.stat-late .stat-num { color: #e6a23c; }
.stat-absent .stat-num { color: #f56c6c; }
.stat-leave .stat-num { color: #409eff; }

.month-bar {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 16rpx 0;
  background: #fff;
  margin-bottom: 16rpx;
}
.month-label { font-size: 32rpx; font-weight: 600; color: #303133; margin: 0 32rpx; }
.month-arrow { font-size: 48rpx; color: #409eff; padding: 8rpx 24rpx; }

.record-list { padding: 0 24rpx; }
.record-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx;
  background: #fff;
  border-radius: 12rpx;
  margin-bottom: 12rpx;
}
.rc-date { font-size: 28rpx; color: #303133; display: block; }
.rc-time { font-size: 24rpx; color: #909399; margin-top: 4rpx; display: block; }
.rc-status { font-size: 28rpx; font-weight: 500; }
</style>
```

- [ ] **Step 2: Verify** — attendance list with stats card, month navigation, pull-refresh.

---

### Task 15: 作业 — pages/homework/index.vue

**Files:**
- Create: `education-store-mini/pages/homework/index.vue`

- [ ] **Step 1: Create homework/index.vue**

```vue
<template>
  <view class="sub-page">
    <child-switcher v-if="childrenStore.children.length > 1" :children="childrenStore.children" :current-id="currentChildId" @change="onChildChange" />

    <view v-if="loading"><loading-skeleton :rows="4" /></view>
    <view v-else-if="error"><empty-state icon="⚠️" text="加载失败" :show-retry="true" @retry="() => loadData(true)" /></view>
    <view v-else-if="!list.length"><empty-state icon="📝" text="暂无课后作业" /></view>

    <view v-else class="record-list">
      <view v-for="item in list" :key="item.id" class="hw-card">
        <view class="hw-header">
          <text class="hw-title">{{ item.title || '无标题' }}</text>
          <text class="hw-time">{{ item.createTime || '' }}</text>
        </view>
        <text class="hw-content">{{ item.content || '' }}</text>
        <view v-if="item.teacherName" class="hw-teacher">
          <text>布置老师：{{ item.teacherName }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue';
import { onLoad, onReachBottom, onPullDownRefresh } from '@dcloudio/uni-app';
import { useChildrenStore } from '@/stores/children';
import { getHomework } from '@/api/homework';
import ChildSwitcher from '@/components/child-switcher.vue';
import EmptyState from '@/components/empty-state.vue';
import LoadingSkeleton from '@/components/loading-skeleton.vue';

const childrenStore = useChildrenStore();
const currentChildId = ref(null);
const list = ref([]);
const loading = ref(true);
const error = ref(false);
const page = ref(1);
const hasMore = ref(true);

onLoad((options) => {
  currentChildId.value = Number(options.studentId) || childrenStore.currentChildId;
  loadData(true);
});

async function loadData(isRefresh) {
  if (isRefresh) { page.value = 1; hasMore.value = true; error.value = false; }
  if (!currentChildId.value) return;
  loading.value = true;
  try {
    const res = await getHomework(currentChildId.value, { page: page.value, pageSize: 10 });
    if (res.code === 200) {
      const data = res.data?.records || res.data || [];
      if (isRefresh || page.value === 1) list.value = [];
      list.value.push(...data);
      hasMore.value = data.length >= 10;
      page.value++;
    }
  } catch {
    if (page.value === 1 || isRefresh) error.value = true;
  } finally { loading.value = false; }
}

function onChildChange(id) { currentChildId.value = id; loadData(true); }

onPullDownRefresh(async () => { await loadData(true); uni.stopPullDownRefresh(); });
onReachBottom(() => { if (!loading.value && hasMore.value) loadData(); });
</script>

<style scoped lang="scss">
.sub-page { min-height: 100vh; padding-bottom: 24rpx; }
.record-list { padding: 0 24rpx; }
.hw-card {
  background: #fff;
  border-radius: 12rpx;
  padding: 24rpx;
  margin-bottom: 12rpx;
}
.hw-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12rpx; }
.hw-title { font-size: 30rpx; font-weight: 600; color: #303133; flex: 1; }
.hw-time { font-size: 22rpx; color: #c0c4cc; }
.hw-content { font-size: 28rpx; color: #606266; line-height: 1.6; }
.hw-teacher { margin-top: 12rpx; font-size: 24rpx; color: #909399; }
</style>
```

- [ ] **Step 2: Verify** — homework list with pagination, child switching.

---

### Task 16: 请假 — pages/leave/index.vue + pages/leave/submit.vue

**Files:**
- Create: `education-store-mini/pages/leave/index.vue`
- Create: `education-store-mini/pages/leave/submit.vue`

- [ ] **Step 1: Create leave/index.vue**

```vue
<template>
  <view class="sub-page">
    <child-switcher v-if="childrenStore.children.length > 1" :children="childrenStore.children" :current-id="currentChildId" @change="onChildChange" />

    <view v-if="loading"><loading-skeleton :rows="3" /></view>
    <view v-else-if="error"><empty-state icon="⚠️" text="加载失败" :show-retry="true" @retry="() => loadData()" /></view>
    <view v-else-if="!list.length"><empty-state icon="📋" text="暂无请假记录" /></view>

    <view v-else class="record-list">
      <view v-for="item in list" :key="item.id" class="leave-card">
        <view class="leave-header">
          <text class="leave-date">{{ item.leaveDate || '--' }}</text>
          <text class="leave-status" :style="{ color: leaveStatusColor(item.status) }">
            {{ leaveStatusText(item.status) }}
          </text>
        </view>
        <text class="leave-type">{{ leaveTypeText(item.leaveType) }}</text>
        <text v-if="item.reason" class="leave-reason">{{ item.reason }}</text>
        <text v-if="item.rejectReason" class="leave-reject">驳回原因：{{ item.rejectReason }}</text>
      </view>
    </view>

    <!-- 发起请假按钮 -->
    <view class="fab-wrap">
      <button class="fab-btn" @click="goSubmit">+ 发起请假</button>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue';
import { onShow, onPullDownRefresh } from '@dcloudio/uni-app';
import { useChildrenStore } from '@/stores/children';
import { getLeaves } from '@/api/leave';
import ChildSwitcher from '@/components/child-switcher.vue';
import EmptyState from '@/components/empty-state.vue';
import LoadingSkeleton from '@/components/loading-skeleton.vue';

const childrenStore = useChildrenStore();
const currentChildId = ref(null);
const list = ref([]);
const loading = ref(true);
const error = ref(false);

const leaveStatusText = (s) => ({ 0: '待审批', 1: '已通过', 2: '已拒绝' }[s] || '--');
const leaveStatusColor = (s) => ({ 0: '#e6a23c', 1: '#67c23a', 2: '#f56c6c' }[s] || '#909399');
const leaveTypeText = (t) => ({ 1: '事假', 2: '病假', 3: '其他' }[t] || '--');

onShow(() => {
  currentChildId.value = childrenStore.currentChildId;
  loadData();
});

async function loadData() {
  if (!currentChildId.value) return;
  loading.value = true;
  error.value = false;
  try {
    const res = await getLeaves(currentChildId.value);
    if (res.code === 200) {
      list.value = res.data || [];
    }
  } catch {
    error.value = true;
  } finally { loading.value = false; }
}

function onChildChange(id) { currentChildId.value = id; loadData(); }
function goSubmit() {
  uni.navigateTo({ url: `/pages/leave/submit?studentId=${currentChildId.value}` });
}

onPullDownRefresh(async () => { await loadData(); uni.stopPullDownRefresh(); });
</script>

<style scoped lang="scss">
.sub-page { min-height: 100vh; padding-bottom: 120rpx; }
.record-list { padding: 0 24rpx; }
.leave-card {
  background: #fff;
  border-radius: 12rpx;
  padding: 24rpx;
  margin-bottom: 12rpx;
}
.leave-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8rpx; }
.leave-date { font-size: 30rpx; font-weight: 600; color: #303133; }
.leave-status { font-size: 26rpx; font-weight: 500; }
.leave-type { font-size: 26rpx; color: #606266; display: block; }
.leave-reason { font-size: 26rpx; color: #606266; margin-top: 8rpx; display: block; }
.leave-reject { font-size: 24rpx; color: #f56c6c; margin-top: 8rpx; display: block; }

.fab-wrap { position: fixed; bottom: 48rpx; left: 0; right: 0; display: flex; justify-content: center; }
.fab-btn {
  background: #409eff;
  color: #fff;
  border-radius: 44rpx;
  padding: 20rpx 48rpx;
  font-size: 30rpx;
  border: none;
  box-shadow: 0 8rpx 24rpx rgba(64, 158, 255, 0.3);
}
.fab-btn::after { border: none; }
</style>
```

- [ ] **Step 2: Create leave/submit.vue**

```vue
<template>
  <view class="submit-page">
    <view class="form-card">
      <view class="form-item">
        <text class="form-label">请假日期</text>
        <picker mode="date" :value="form.leaveDate" @change="onDateChange">
          <view class="form-picker">{{ form.leaveDate || '请选择日期' }}</view>
        </picker>
      </view>

      <view class="form-item">
        <text class="form-label">请假类型</text>
        <picker mode="selector" :range="leaveTypes" :value="typeIndex" @change="onTypeChange">
          <view class="form-picker">{{ leaveTypes[typeIndex] }}</view>
        </picker>
      </view>

      <view class="form-item">
        <text class="form-label">请假原因</text>
        <textarea v-model="form.reason" class="form-textarea" placeholder="请输入请假原因（选填）" maxlength="200" />
      </view>
    </view>

    <view class="submit-btn-wrap">
      <button class="submit-btn" type="primary" :loading="submitting" @click="handleSubmit">提交请假申请</button>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { submitLeave } from '@/api/leave';

let studentId = null;
const submitting = ref(false);
const leaveTypes = ['事假', '病假', '其他'];
const typeIndex = ref(0);

const form = reactive({
  leaveDate: '',
  leaveType: 1,
  reason: '',
});

onLoad((options) => {
  studentId = options.studentId;
});

function onDateChange(e) {
  form.leaveDate = e.detail.value;
}

function onTypeChange(e) {
  typeIndex.value = e.detail.value;
  form.leaveType = e.detail.value + 1;
}

async function handleSubmit() {
  if (!form.leaveDate) {
    uni.showToast({ title: '请选择请假日期', icon: 'none' });
    return;
  }
  if (!studentId) {
    uni.showToast({ title: '参数错误', icon: 'none' });
    return;
  }

  submitting.value = true;
  try {
    const res = await submitLeave(studentId, {
      leaveDate: form.leaveDate,
      leaveType: form.leaveType,
      reason: form.reason,
    });
    if (res.code === 200) {
      uni.showToast({ title: '提交成功', icon: 'success' });
      setTimeout(() => uni.navigateBack(), 1500);
    } else {
      uni.showToast({ title: res.msg || '提交失败', icon: 'none' });
    }
  } catch {
    uni.showToast({ title: '提交失败，请检查网络', icon: 'none' });
  } finally {
    submitting.value = false;
  }
}
</script>

<style scoped lang="scss">
.submit-page { padding: 24rpx; min-height: 100vh; }
.form-card { background: #fff; border-radius: 12rpx; padding: 24rpx; }

.form-item { margin-bottom: 32rpx; }
.form-label { font-size: 28rpx; color: #303133; margin-bottom: 12rpx; display: block; }
.form-picker { padding: 20rpx 16rpx; background: #f5f7fa; border-radius: 8rpx; font-size: 28rpx; color: #303133; }
.form-textarea { width: 100%; height: 200rpx; padding: 20rpx 16rpx; background: #f5f7fa; border-radius: 8rpx; font-size: 28rpx; box-sizing: border-box; }

.submit-btn-wrap { margin-top: 48rpx; }
.submit-btn { width: 100%; height: 88rpx; line-height: 88rpx; background: #409eff; border-radius: 44rpx; font-size: 32rpx; color: #fff; }
.submit-btn::after { border: none; }
</style>
```

- [ ] **Step 3: Verify** — leave list loads, submit form works, returns to list after submission.

---

### Task 17: 报名 — pages/enrollment/index.vue

**Files:**
- Create: `education-store-mini/pages/enrollment/index.vue`

- [ ] **Step 1: Create enrollment/index.vue**

```vue
<template>
  <view class="sub-page">
    <child-switcher v-if="childrenStore.children.length > 1" :children="childrenStore.children" :current-id="currentChildId" @change="onChildChange" />

    <view v-if="loading"><loading-skeleton :rows="4" /></view>
    <view v-else-if="error"><empty-state icon="⚠️" text="加载失败" :show-retry="true" @retry="() => loadData()" /></view>
    <view v-else-if="!list.length"><empty-state icon="📋" text="暂无报名记录" /></view>

    <view v-else class="record-list">
      <view v-for="item in list" :key="item.id" class="enroll-card">
        <view class="enroll-header">
          <text class="enroll-course">{{ item.courseName || '--' }}</text>
          <text class="enroll-status" :style="{ color: payColor(item.paymentStatus) }">
            {{ payText(item.paymentStatus) }}
          </text>
        </view>
        <view class="enroll-meta">
          <text v-if="item.enrollmentDate">报名日期：{{ item.enrollmentDate }}</text>
          <text v-if="item.amount != null" class="enroll-amount">¥{{ item.amount }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue';
import { onLoad, onPullDownRefresh } from '@dcloudio/uni-app';
import { useChildrenStore } from '@/stores/children';
import { getEnrollments } from '@/api/enrollment';
import ChildSwitcher from '@/components/child-switcher.vue';
import EmptyState from '@/components/empty-state.vue';
import LoadingSkeleton from '@/components/loading-skeleton.vue';

const childrenStore = useChildrenStore();
const currentChildId = ref(null);
const list = ref([]);
const loading = ref(true);
const error = ref(false);

const payText = (s) => ({ PAID: '已缴费', UNPAID: '未缴费', REFUNDED: '已退费' }[s] || s || '--');
const payColor = (s) => ({ PAID: '#67c23a', UNPAID: '#e6a23c', REFUNDED: '#f56c6c' }[s] || '#909399');

onLoad((options) => {
  currentChildId.value = Number(options.studentId) || childrenStore.currentChildId;
  loadData();
});

async function loadData() {
  if (!currentChildId.value) return;
  loading.value = true; error.value = false;
  try {
    const res = await getEnrollments(currentChildId.value);
    if (res.code === 200) list.value = res.data || [];
  } catch { error.value = true; }
  finally { loading.value = false; }
}

function onChildChange(id) { currentChildId.value = id; loadData(); }

onPullDownRefresh(async () => { await loadData(); uni.stopPullDownRefresh(); });
</script>

<style scoped lang="scss">
.sub-page { min-height: 100vh; padding-bottom: 24rpx; }
.record-list { padding: 0 24rpx; }
.enroll-card { background: #fff; border-radius: 12rpx; padding: 24rpx; margin-bottom: 12rpx; }
.enroll-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8rpx; }
.enroll-course { font-size: 30rpx; font-weight: 600; color: #303133; }
.enroll-status { font-size: 26rpx; font-weight: 500; }
.enroll-meta { display: flex; justify-content: space-between; font-size: 24rpx; color: #909399; margin-top: 8rpx; }
.enroll-amount { font-size: 32rpx; font-weight: 600; color: #f56c6c; }
</style>
```

- [ ] **Step 2: Verify** — enrollment list loads, payment status labels rendered.

---

### Task 18: 课表 — pages/schedule/index.vue

**Files:**
- Create: `education-store-mini/pages/schedule/index.vue`

- [ ] **Step 1: Create schedule/index.vue**

```vue
<template>
  <view class="sub-page">
    <child-switcher v-if="childrenStore.children.length > 1" :children="childrenStore.children" :current-id="currentChildId" @change="onChildChange" />

    <!-- 日期范围选择 -->
    <view class="date-range-bar">
      <view class="dr-item" @click="showDatePicker('start')">
        <text class="dr-label">开始</text>
        <text class="dr-value">{{ startDate || '选择' }}</text>
      </view>
      <text class="dr-sep">至</text>
      <view class="dr-item" @click="showDatePicker('end')">
        <text class="dr-label">结束</text>
        <text class="dr-value">{{ endDate || '选择' }}</text>
      </view>
      <button class="dr-btn" size="mini" @click="loadData">查询</button>
    </view>

    <view v-if="loading"><loading-skeleton :rows="4" /></view>
    <view v-else-if="error"><empty-state icon="⚠️" text="加载失败" :show-retry="true" @retry="() => loadData()" /></view>
    <view v-else-if="!list.length"><empty-state icon="📅" text="当前日期范围暂无课程" /></view>

    <view v-else class="record-list">
      <view v-for="item in list" :key="item.id" class="sched-card">
        <view class="sched-top">
          <text class="sched-date">{{ item.scheduleDate || '--' }}</text>
          <text class="sched-time">{{ item.startTime || '' }} - {{ item.endTime || '' }}</text>
        </view>
        <text class="sched-course">{{ item.courseName || '--' }}</text>
        <view class="sched-meta">
          <text v-if="item.roomName">📍 {{ item.roomName }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue';
import { onLoad, onPullDownRefresh } from '@dcloudio/uni-app';
import { useChildrenStore } from '@/stores/children';
import { getSchedule } from '@/api/schedule';
import ChildSwitcher from '@/components/child-switcher.vue';
import EmptyState from '@/components/empty-state.vue';
import LoadingSkeleton from '@/components/loading-skeleton.vue';

const childrenStore = useChildrenStore();
const currentChildId = ref(null);
const list = ref([]);
const loading = ref(true);
const error = ref(false);

// 默认本周
const now = new Date();
const dayOfWeek = now.getDay() || 7;
const monday = new Date(now); monday.setDate(now.getDate() - dayOfWeek + 1);
const sunday = new Date(now); sunday.setDate(now.getDate() + (7 - dayOfWeek));
const fmt = (d) => d.getFullYear() + '-' + String(d.getMonth() + 1).padStart(2, '0') + '-' + String(d.getDate()).padStart(2, '0');
const startDate = ref(fmt(monday));
const endDate = ref(fmt(sunday));

let picking = 'start';

function showDatePicker(type) {
  picking = type;
  // 触发 uni date picker
}

onLoad((options) => {
  currentChildId.value = Number(options.studentId) || childrenStore.currentChildId;
  loadData();
});

async function loadData() {
  if (!currentChildId.value) return;
  loading.value = true; error.value = false;
  try {
    const res = await getSchedule(currentChildId.value, { startDate: startDate.value, endDate: endDate.value });
    if (res.code === 200) {
      list.value = res.data || [];
    }
  } catch { error.value = true; }
  finally { loading.value = false; }
}

function onChildChange(id) { currentChildId.value = id; loadData(); }

onPullDownRefresh(async () => { await loadData(); uni.stopPullDownRefresh(); });
</script>

<style scoped lang="scss">
.sub-page { min-height: 100vh; padding-bottom: 24rpx; }
.date-range-bar { display: flex; align-items: center; gap: 12rpx; padding: 24rpx; background: #fff; margin-bottom: 16rpx; }
.dr-item { flex: 1; }
.dr-label { font-size: 22rpx; color: #909399; display: block; }
.dr-value { font-size: 28rpx; color: #409eff; font-weight: 500; }
.dr-sep { color: #909399; font-size: 24rpx; }
.dr-btn { background: #409eff; color: #fff; font-size: 24rpx; }

.record-list { padding: 0 24rpx; }
.sched-card { background: #fff; border-radius: 12rpx; padding: 24rpx; margin-bottom: 12rpx; }
.sched-top { display: flex; justify-content: space-between; margin-bottom: 8rpx; }
.sched-date { font-size: 28rpx; color: #303133; font-weight: 500; }
.sched-time { font-size: 26rpx; color: #409eff; font-weight: 500; }
.sched-course { font-size: 30rpx; font-weight: 600; color: #303133; display: block; margin-bottom: 8rpx; }
.sched-meta { font-size: 24rpx; color: #909399; }
</style>
```

- [ ] **Step 2: Verify** — schedule loads with date range, course cards rendered.

---

### Task 19: 成绩 — pages/exam/index.vue

**Files:**
- Create: `education-store-mini/pages/exam/index.vue`

- [ ] **Step 1: Create exam/index.vue**

```vue
<template>
  <view class="sub-page">
    <child-switcher v-if="childrenStore.children.length > 1" :children="childrenStore.children" :current-id="currentChildId" @change="onChildChange" />

    <view v-if="loading"><loading-skeleton :rows="4" /></view>
    <view v-else-if="error"><empty-state icon="⚠️" text="加载失败" :show-retry="true" @retry="() => loadData()" /></view>
    <view v-else-if="!list.length"><empty-state icon="📊" text="暂无考试成绩" /></view>

    <view v-else class="record-list">
      <view v-for="item in list" :key="item.id" class="exam-card">
        <view class="exam-header">
          <text class="exam-name">{{ item.examName || '--' }}</text>
          <text class="exam-score">{{ item.totalScore != null ? item.totalScore + '分' : '--' }}</text>
        </view>
        <view class="exam-meta">
          <text>{{ item.examType || '--' }}</text>
          <text>{{ item.examDate || '' }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue';
import { onLoad, onPullDownRefresh } from '@dcloudio/uni-app';
import { useChildrenStore } from '@/stores/children';
import { getExamScores } from '@/api/exam';
import ChildSwitcher from '@/components/child-switcher.vue';
import EmptyState from '@/components/empty-state.vue';
import LoadingSkeleton from '@/components/loading-skeleton.vue';

const childrenStore = useChildrenStore();
const currentChildId = ref(null);
const list = ref([]);
const loading = ref(true);
const error = ref(false);

onLoad((options) => {
  currentChildId.value = Number(options.studentId) || childrenStore.currentChildId;
  loadData();
});

async function loadData() {
  if (!currentChildId.value) return;
  loading.value = true; error.value = false;
  try {
    const res = await getExamScores(currentChildId.value);
    if (res.code === 200) list.value = res.data || [];
  } catch { error.value = true; }
  finally { loading.value = false; }
}

function onChildChange(id) { currentChildId.value = id; loadData(); }

onPullDownRefresh(async () => { await loadData(); uni.stopPullDownRefresh(); });
</script>

<style scoped lang="scss">
.sub-page { min-height: 100vh; padding-bottom: 24rpx; }
.record-list { padding: 0 24rpx; }
.exam-card { background: #fff; border-radius: 12rpx; padding: 24rpx; margin-bottom: 12rpx; }
.exam-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8rpx; }
.exam-name { font-size: 30rpx; font-weight: 600; color: #303133; }
.exam-score { font-size: 36rpx; font-weight: 700; color: #409eff; }
.exam-meta { display: flex; justify-content: space-between; font-size: 24rpx; color: #909399; }
</style>
```

- [ ] **Step 2: Verify** — exam scores list loads, score highlighted.

---

### Task 20: 评价 — pages/evaluation/index.vue

**Files:**
- Create: `education-store-mini/pages/evaluation/index.vue`

- [ ] **Step 1: Create evaluation/index.vue**

```vue
<template>
  <view class="sub-page">
    <child-switcher v-if="childrenStore.children.length > 1" :children="childrenStore.children" :current-id="currentChildId" @change="onChildChange" />

    <view v-if="loading"><loading-skeleton :rows="4" /></view>
    <view v-else-if="error"><empty-state icon="⚠️" text="加载失败" :show-retry="true" @retry="() => loadData()" /></view>
    <view v-else-if="!list.length"><empty-state icon="⭐" text="暂无课堂评价" /></view>

    <view v-else class="record-list">
      <view v-for="item in list" :key="item.evaluationId || item.id" class="eval-card">
        <view class="eval-stars">
          <text v-for="i in 5" :key="i">{{ i <= (item.starRating || 0) ? '⭐' : '☆' }}</text>
          <text class="eval-time">{{ item.createTime || '' }}</text>
        </view>
        <text v-if="item.summary" class="eval-summary">{{ item.summary }}</text>
        <text v-if="item.comment" class="eval-comment">{{ item.comment }}</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue';
import { onLoad, onPullDownRefresh } from '@dcloudio/uni-app';
import { useChildrenStore } from '@/stores/children';
import { getEvaluations } from '@/api/evaluation';
import ChildSwitcher from '@/components/child-switcher.vue';
import EmptyState from '@/components/empty-state.vue';
import LoadingSkeleton from '@/components/loading-skeleton.vue';

const childrenStore = useChildrenStore();
const currentChildId = ref(null);
const list = ref([]);
const loading = ref(true);
const error = ref(false);

onLoad((options) => {
  currentChildId.value = Number(options.studentId) || childrenStore.currentChildId;
  loadData();
});

async function loadData() {
  if (!currentChildId.value) return;
  loading.value = true; error.value = false;
  try {
    const res = await getEvaluations(currentChildId.value);
    if (res.code === 200) list.value = res.data || [];
  } catch { error.value = true; }
  finally { loading.value = false; }
}

function onChildChange(id) { currentChildId.value = id; loadData(); }

onPullDownRefresh(async () => { await loadData(); uni.stopPullDownRefresh(); });
</script>

<style scoped lang="scss">
.sub-page { min-height: 100vh; padding-bottom: 24rpx; }
.record-list { padding: 0 24rpx; }
.eval-card { background: #fff; border-radius: 12rpx; padding: 24rpx; margin-bottom: 12rpx; }
.eval-stars { display: flex; align-items: center; gap: 4rpx; margin-bottom: 8rpx; font-size: 28rpx; }
.eval-time { font-size: 22rpx; color: #c0c4cc; margin-left: auto; }
.eval-summary { font-size: 28rpx; color: #303133; font-weight: 500; display: block; margin-bottom: 8rpx; }
.eval-comment { font-size: 26rpx; color: #606266; line-height: 1.5; display: block; }
</style>
```

- [ ] **Step 2: Verify** — evaluations list with star ratings.

---

### Task 21: 积分 — pages/points/index.vue

**Files:**
- Create: `education-store-mini/pages/points/index.vue`

- [ ] **Step 1: Create points/index.vue**

```vue
<template>
  <view class="sub-page">
    <child-switcher v-if="childrenStore.children.length > 1" :children="childrenStore.children" :current-id="currentChildId" @change="onChildChange" />

    <!-- 积分余额卡片 -->
    <view v-if="balance" class="balance-card">
      <text class="balance-num">{{ balance.balance || 0 }}</text>
      <text class="balance-label">当前积分</text>
      <view class="balance-total">
        <text>累计获得：{{ balance.totalEarned || 0 }}</text>
      </view>
    </view>

    <!-- 积分日志 -->
    <view class="section-title">积分明细</view>
    <view v-if="loading"><loading-skeleton :rows="4" /></view>
    <view v-else-if="error"><empty-state icon="⚠️" text="加载失败" :show-retry="true" @retry="() => loadData()" /></view>
    <view v-else-if="!logList.length"><empty-state icon="📋" text="暂无积分记录" /></view>

    <view v-else class="record-list">
      <view v-for="item in logList" :key="item.id" class="log-card">
        <view class="log-left">
          <text class="log-type">{{ item.changeType || item.sourceType || '--' }}</text>
          <text class="log-remark" v-if="item.remark">{{ item.remark }}</text>
          <text class="log-time">{{ item.createTime || '' }}</text>
        </view>
        <text class="log-amount" :class="item.amount > 0 ? 'positive' : 'negative'">
          {{ item.amount > 0 ? '+' : '' }}{{ item.amount }}
        </text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue';
import { onLoad, onPullDownRefresh } from '@dcloudio/uni-app';
import { useChildrenStore } from '@/stores/children';
import { getPoints, getPointsLog } from '@/api/points';
import ChildSwitcher from '@/components/child-switcher.vue';
import EmptyState from '@/components/empty-state.vue';
import LoadingSkeleton from '@/components/loading-skeleton.vue';

const childrenStore = useChildrenStore();
const currentChildId = ref(null);
const balance = ref(null);
const logList = ref([]);
const loading = ref(true);
const error = ref(false);

onLoad((options) => {
  currentChildId.value = Number(options.studentId) || childrenStore.currentChildId;
  loadData();
});

async function loadData() {
  if (!currentChildId.value) return;
  loading.value = true; error.value = false;
  try {
    const [pointsRes, logRes] = await Promise.all([
      getPoints(currentChildId.value),
      getPointsLog(currentChildId.value),
    ]);
    if (pointsRes.code === 200) balance.value = pointsRes.data;
    if (logRes.code === 200) logList.value = logRes.data || [];
  } catch { error.value = true; }
  finally { loading.value = false; }
}

function onChildChange(id) { currentChildId.value = id; loadData(); }

onPullDownRefresh(async () => { await loadData(); uni.stopPullDownRefresh(); });
</script>

<style scoped lang="scss">
.sub-page { min-height: 100vh; padding-bottom: 24rpx; }

.balance-card {
  margin: 24rpx;
  padding: 40rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16rpx;
  color: #fff;
  text-align: center;
}
.balance-num { font-size: 72rpx; font-weight: 700; display: block; }
.balance-label { font-size: 26rpx; opacity: 0.85; margin-top: 8rpx; display: block; }
.balance-total { font-size: 24rpx; opacity: 0.7; margin-top: 12rpx; }

.section-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #303133;
  padding: 16rpx 24rpx;
}

.record-list { padding: 0 24rpx; }
.log-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  border-radius: 12rpx;
  padding: 24rpx;
  margin-bottom: 12rpx;
}
.log-left { flex: 1; min-width: 0; }
.log-type { font-size: 28rpx; color: #303133; font-weight: 500; display: block; }
.log-remark { font-size: 24rpx; color: #909399; margin-top: 4rpx; display: block; }
.log-time { font-size: 22rpx; color: #c0c4cc; margin-top: 4rpx; display: block; }
.log-amount { font-size: 32rpx; font-weight: 600; margin-left: 16rpx; }
.positive { color: #67c23a; }
.negative { color: #f56c6c; }
</style>
```

- [ ] **Step 2: Verify** — points balance + log list loads with color-coded amounts.

---

### Task 22: Tab Bar Icons + Final Integration

**Files:**
- Create: 6 PNG icon files in `static/tab/`
- Verify: All pages compile and navigate correctly.

- [ ] **Step 1: Generate placeholder tab icons**

Since we can't create actual PNG files from the CLI, add a note in the project README and use the project's existing logo as fallback. Configure pages.json tab icons to use a simple approach.

Update `pages.json` tab bar to remove icon requirements temporarily:

```json
{
  "tabBar": {
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

- [ ] **Step 2: Create static/logo.png placeholder**

Copy an existing image or create a simple text-based placeholder.

```bash
# Use a simple approach: create a 1x1 pixel PNG as placeholder
```

- [ ] **Step 3: Verify final build**

```bash
cd D:/Work/Personal/education/education-store/education-store-mini
pnpm dev:mp-weixin
```

Expected: Compiles successfully, all pages accessible, API calls work.

---

## Summary

| Task | Files | Description |
|:--:|------|------|
| 1 | 4 | Project scaffolding (vite, package.json, main.js, vite.config) |
| 2 | 1 | `utils/request.js` — HTTP client with auth interceptor |
| 3 | 1 | `utils/index.js` — date helpers, event meta, status maps |
| 4 | 1 | `stores/auth.js` — login/logout/checkAuth |
| 5 | 1 | `stores/children.js` — children list + current selection |
| 6 | 12 | API layer (all 12 files) |
| 7 | 4 | Shared components (child-switcher, empty-state, skeleton, page-header) |
| 8 | 4 | App.vue + pages.json + manifest.json + uni.scss |
| 9 | 1 | Login page |
| 10 | 1 | 首页 — timeline with child grouping |
| 11 | 1 | 消息 — notification preferences |
| 12 | 1 | 我的 — children list + menu + logout |
| 13 | 1 | 孩子详情 |
| 14 | 1 | 考勤 — stats + month list |
| 15 | 1 | 作业 — paged list |
| 16 | 2 | 请假 — list + submit form |
| 17 | 1 | 报名 — enrollment list |
| 18 | 1 | 课表 — date-range schedule |
| 19 | 1 | 成绩 — exam scores |
| 20 | 1 | 评价 — star-rating evaluations |
| 21 | 1 | 积分 — balance + log |
| 22 | 0 | Tab icons + final verification |
| **Total** | **42** | **39 new files + 3 modified config files** |
