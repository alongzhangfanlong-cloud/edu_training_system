# N09 教师端前端 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build N09 teacher portal — 4 independent pages (dashboard, homework, resource, my-schedule) with teacher-scoped data views.

**Architecture:** 13 new files: 1 API layer + 12 Vue components. N09.1 dashboard follows N01 dashboard pattern (index orchestrator + sub-components, Promise.allSettled, 5-min refresh). N09.2-N09.4 reuse existing API modules with teacher-scoped filters. Routes loaded dynamically from backend menu table — component paths must match `views/` glob.

**Tech Stack:** Vue 3 Composition API (`<script setup>`), Element Plus, Pinia, Less scoped styles.

**Backend reference:** `edu-module-teacher-dashboard` — 5 endpoints at `/api/v1/teacher/dashboard`, all pre-built.

---

### Task 1: Teacher Dashboard API Layer

**Files:**
- Create: `education-store-web/src/api/teacher/dashboard.js`

- [ ] **Step 1: Create API file**

```js
/**
 * 教师工作台 API — 对齐后端 TeacherDashboardController
 */
import request from '@/utils/request';

const BASE = '/api/v1/teacher/dashboard';

/** 获取欢迎区信息（教师姓名/上次登录/服务器日期） */
export function getUserInfo() {
  return request({ url: `${BASE}/user-info`, method: 'get' });
}

/** 获取统计卡片数据（今日课程数/待签到班级数/待评价学生数） */
export function getStatistics() {
  return request({ url: `${BASE}/statistics`, method: 'get' });
}

/** 获取今日课表（含签到状态摘要） */
export function getSchedule() {
  return request({ url: `${BASE}/schedule`, method: 'get' });
}

/** 获取某排课的班级学生名单+签到状态 */
export function getScheduleStudents(scheduleId) {
  return request({ url: `${BASE}/schedule/${scheduleId}/students`, method: 'get' });
}

/** 获取待办数据（待批改作业/待回复请假） */
export function getTodos() {
  return request({ url: `${BASE}/todos`, method: 'get' });
}
```

- [ ] **Step 2: Verify** — file created, no syntax errors.

---

### Task 2: WelcomeBar Component

**Files:**
- Create: `education-store-web/src/views/teacher/dashboard/WelcomeBar.vue`

- [ ] **Step 1: Create WelcomeBar.vue**

```vue
<!-- 教师工作台 — 欢迎区 -->
<template>
  <div class="welcome-bar">
    <div class="welcome-info">
      <h2 class="welcome-greeting">👋 欢迎回来，{{ userName }}老师</h2>
      <p class="welcome-date">{{ serverDateFormatted || '--' }}</p>
      <p v-if="lastLoginTime" class="welcome-login">上次登录：{{ lastLoginTime }}</p>
      <p v-else-if="isFirstLogin" class="welcome-login welcome-first">🎉 欢迎首次登录！</p>
    </div>
    <el-button @click="$emit('refresh')" :loading="refreshing" :icon="Refresh" circle />
  </div>
</template>

<script setup>
import { Refresh } from '@element-plus/icons-vue';

defineProps({
  userName: { type: String, default: '--' },
  serverDateFormatted: { type: String, default: '' },
  lastLoginTime: { type: String, default: '' },
  isFirstLogin: { type: Boolean, default: false },
  refreshing: { type: Boolean, default: false },
});

defineEmits(['refresh']);
</script>

<style scoped lang="less">
.welcome-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  margin-bottom: 16px;
  background: linear-gradient(135deg, #409eff 0%, #337ecc 100%);
  border-radius: 8px;
  color: #fff;

  .welcome-greeting { margin: 0 0 6px; font-size: 22px; font-weight: 600; }
  .welcome-date { margin: 0 0 2px; font-size: 14px; opacity: 0.9; }
  .welcome-login { margin: 0; font-size: 12px; opacity: 0.75; }
  .welcome-first { opacity: 1; font-size: 13px; }
}
</style>
```

- [ ] **Step 2: Verify** — component structure matches N01 WelcomeBar pattern.

---

### Task 3: StatCards Component

**Files:**
- Create: `education-store-web/src/views/teacher/dashboard/StatCards.vue`

- [ ] **Step 1: Create StatCards.vue**

```vue
<!-- 教师工作台 — 统计卡片行 -->
<template>
  <el-row :gutter="16" class="stat-row">
    <el-col :xs="24" :sm="8" v-for="card in cards" :key="card.key">
      <el-card shadow="hover" class="stat-card" @click="card.link ? $router.push(card.link) : null">
        <div class="stat-inner">
          <div class="stat-icon" :style="{ background: card.bg }">
            <el-icon :size="28"><component :is="card.icon" /></el-icon>
          </div>
          <div class="stat-body">
            <div class="stat-label">{{ card.label }}</div>
            <div class="stat-value">
              <span v-loading="loading">{{ card.formatter(stats) }}</span>
            </div>
            <div v-if="card.link" class="stat-link">{{ card.linkText }}</div>
          </div>
        </div>
      </el-card>
    </el-col>
  </el-row>
</template>

<script setup>
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import { Notebook, Checked, Star } from '@element-plus/icons-vue';

const router = useRouter();

const props = defineProps({
  stats: { type: Object, default: null },
  loading: { type: Boolean, default: false },
});

const cards = [
  {
    key: 'todayCourseCount',
    label: '今日课程',
    icon: Notebook,
    bg: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    link: '/teacher/my-schedule',
    linkText: '查看课表 →',
    formatter: (s) => s?.todayCourseCount ?? '--',
  },
  {
    key: 'todayUncheckedClassCount',
    label: '待签到班级',
    icon: Checked,
    bg: 'linear-gradient(135deg, #11998e 0%, #38ef7d 100%)',
    link: '/attendance/signin',
    linkText: '去签到 →',
    formatter: (s) => s?.todayUncheckedClassCount ?? '--',
  },
  {
    key: 'pendingEvaluationCount',
    label: '待评价学生',
    icon: Star,
    bg: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    link: '/teacher/homework',
    linkText: '去评价 →',
    formatter: (s) => s?.pendingEvaluationCount ?? '--',
  },
];
</script>

<style scoped lang="less">
.stat-row { margin-bottom: 16px; }

.stat-card {
  :deep(.el-card__body) { padding: 16px 20px; cursor: pointer; }
  transition: transform 0.2s;
  &:hover { transform: translateY(-2px); }
}

.stat-inner {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.stat-body { flex: 1; min-width: 0; }

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  line-height: 1.2;
}

.stat-link {
  font-size: 12px;
  color: #409eff;
  margin-top: 4px;
}
</style>
```

- [ ] **Step 2: Verify** — 3 cards with correct icons and formatters.

---

### Task 4: ScheduleCard Component

**Files:**
- Create: `education-store-web/src/views/teacher/dashboard/ScheduleCard.vue`

- [ ] **Step 1: Create ScheduleCard.vue**

```vue
<!-- 教师工作台 — 单条课表卡片 -->
<template>
  <div class="schedule-card" :class="'status-' + schedule.courseStatus?.toLowerCase()">
    <!-- 左侧状态条 -->
    <div class="card-bar" />

    <div class="card-body">
      <!-- 时间行 -->
      <div class="card-time-row">
        <span class="card-time">{{ schedule.startTime || '--' }} - {{ schedule.endTime || '--' }}</span>
        <el-tag
          v-if="schedule.courseStatus === 'IN_PROGRESS'"
          size="small"
          type="warning"
          effect="dark"
        >进行中</el-tag>
        <el-tag
          v-else-if="schedule.courseStatus === 'FINISHED'"
          size="small"
          type="info"
        >已结束</el-tag>
      </div>

      <!-- 课程信息 -->
      <div class="card-info">
        <span class="card-course">{{ schedule.courseName || '--' }}</span>
        <el-divider direction="vertical" />
        <span class="card-class">{{ schedule.className || '--' }}</span>
        <el-divider direction="vertical" />
        <span class="card-room">{{ schedule.classroomName || '未分配' }}</span>
      </div>

      <!-- 签到状态 + 应到人数 -->
      <div class="card-att-row">
        <span
          class="card-students"
          :class="{ 'is-clickable': schedule.expectedCount > 0 }"
          @click="schedule.expectedCount > 0 && $emit('toggleStudents', schedule.scheduleId)"
        >
          应到 <strong>{{ schedule.expectedCount || 0 }}</strong> 人
          <el-icon v-if="schedule.expectedCount > 0" class="expand-icon" :class="{ rotated: expanded }">
            <ArrowDown />
          </el-icon>
        </span>

        <!-- 签到状态标签 -->
        <template v-if="schedule.attendanceStatus === 'COMPLETED'">
          <el-tag size="small" type="success">✅ 已签到 {{ schedule.checkedCount }}/{{ schedule.expectedCount }}</el-tag>
        </template>
        <template v-else-if="schedule.attendanceStatus === 'PARTIAL'">
          <el-tag size="small" type="warning">已签到 {{ schedule.checkedCount }}/{{ schedule.expectedCount }}</el-tag>
          <el-button size="small" type="primary" @click.stop="$emit('manageSignin', schedule)">签到管理</el-button>
        </template>
        <template v-else-if="schedule.attendanceStatus === 'UNCHECKED'">
          <el-tag size="small" type="danger">未签到</el-tag>
          <el-button size="small" type="primary" @click.stop="$emit('startSignin', schedule)">可签到</el-button>
        </template>
        <template v-else>
          <span class="text-muted">--</span>
        </template>
      </div>

      <!-- 展开的学生名单（插槽） -->
      <div v-if="expanded" class="card-students-expand">
        <slot name="students" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ArrowDown } from '@element-plus/icons-vue';

defineProps({
  schedule: { type: Object, required: true },
  expanded: { type: Boolean, default: false },
});

defineEmits(['toggleStudents', 'startSignin', 'manageSignin']);
</script>

<style scoped lang="less">
.schedule-card {
  display: flex;
  border-radius: 8px;
  overflow: hidden;
  background: #fff;
  border: 1px solid #ebeef5;
  transition: all 0.2s;

  &.status-in_progress {
    border-left: none;
    .card-bar { background: #e6a23c; }
  }

  &.status-finished {
    opacity: 0.65;
    .card-bar { background: #c0c4cc; }
  }

  &.status-not_started {
    .card-bar { background: #409eff; }
  }

  &:hover { box-shadow: 0 2px 8px rgba(0,0,0,0.06); }
}

.card-bar {
  width: 4px;
  flex-shrink: 0;
  transition: background 0.2s;
}

.card-body {
  flex: 1;
  padding: 14px 16px;
  min-width: 0;
}

.card-time-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.card-time {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.card-info {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-bottom: 10px;
  font-size: 13px;
  color: #606266;

  .card-course { font-weight: 500; color: #303133; }
  .card-room { color: #909399; }

  :deep(.el-divider--vertical) { height: 14px; margin: 0 4px; }
}

.card-att-row {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.card-students {
  font-size: 13px;
  color: #606266;
  display: inline-flex;
  align-items: center;
  gap: 4px;

  &.is-clickable { cursor: pointer; color: #409eff; }
  strong { font-size: 15px; }
}

.expand-icon {
  font-size: 12px;
  transition: transform 0.2s;
  &.rotated { transform: rotate(-180deg); }
}

.card-students-expand {
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px dashed #ebeef5;
}

.text-muted { color: #c0c4cc; font-size: 13px; }
</style>
```

- [ ] **Step 2: Verify** — card handles all 4 attendance statuses and 3 course statuses.

---

### Task 5: TodaySchedule Component

**Files:**
- Create: `education-store-web/src/views/teacher/dashboard/TodaySchedule.vue`

- [ ] **Step 1: Create TodaySchedule.vue**

```vue
<!-- 教师工作台 — 今日课表时间线 -->
<template>
  <el-card shadow="hover" class="today-schedule-panel">
    <template #header>
      <span class="panel-title">📅 我的今日课表</span>
    </template>

    <!-- 加载骨架 -->
    <div v-if="loading" class="skeleton-list">
      <div v-for="i in 4" :key="i" class="skeleton-card">
        <el-skeleton :rows="2" animated />
      </div>
    </div>

    <!-- 加载失败 -->
    <div v-else-if="error" class="error-block">
      <el-result icon="error" title="课表加载失败" sub-title="请检查网络后重试">
        <template #extra>
          <el-button type="primary" @click="$emit('retry')">重试</el-button>
        </template>
      </el-result>
    </div>

    <!-- 无课程 -->
    <el-empty
      v-else-if="!schedules || schedules.length === 0"
      description="📅 今日无课程安排，好好休息吧 ☕"
      :image-size="100"
    />

    <!-- 课表列表 -->
    <div v-else class="schedule-list">
      <ScheduleCard
        v-for="s in schedules"
        :key="s.scheduleId"
        :schedule="s"
        :expanded="expandedId === s.scheduleId"
        @toggle-students="onToggleStudents"
        @start-signin="$emit('startSignin', s)"
        @manage-signin="$emit('manageSignin', s)"
      >
        <template #students>
          <div v-loading="studentLoading" class="student-list">
            <div class="student-summary">
              签到状态：已签到 {{ checkedCount }} / {{ totalCount }}
            </div>
            <div v-for="stu in currentStudents" :key="stu.studentId" class="student-item">
              <span>{{ stu.studentName }}</span>
              <el-tag
                v-if="stu.attendanceStatus === 'PRESENT'"
                size="small" type="success"
              >已签到</el-tag>
              <el-tag
                v-else-if="stu.attendanceStatus === 'LATE'"
                size="small" type="warning"
              >迟到</el-tag>
              <el-tag
                v-else-if="stu.attendanceStatus === 'ABSENT'"
                size="small" type="danger"
              >缺勤</el-tag>
              <span v-else class="text-muted">未签到</span>
            </div>
          </div>
        </template>
      </ScheduleCard>
    </div>
  </el-card>
</template>

<script setup>
import { ref, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { getScheduleStudents } from '@/api/teacher/dashboard.js';
import ScheduleCard from './ScheduleCard.vue';

defineProps({
  schedules: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false },
  error: { type: Boolean, default: false },
});

defineEmits(['retry', 'startSignin', 'manageSignin']);

// 学生名单展开状态
const expandedId = ref(null);
const studentLoading = ref(false);
const currentStudents = ref([]);

let studentReqId = 0;

async function onToggleStudents(scheduleId) {
  if (expandedId.value === scheduleId) {
    expandedId.value = null;
    return;
  }

  expandedId.value = scheduleId;
  studentLoading.value = true;
  currentStudents.value = [];

  const reqId = ++studentReqId;
  try {
    const res = await getScheduleStudents(scheduleId);
    if (reqId !== studentReqId) return;
    if (res.success) {
      currentStudents.value = res.data || [];
    } else {
      ElMessage.error(res.msg || '加载学生名单失败');
    }
  } catch {
    if (reqId !== studentReqId) return;
    ElMessage.error('加载学生名单失败');
  } finally {
    if (reqId === studentReqId) studentLoading.value = false;
  }
}

const totalCount = computed(() => currentStudents.value.length);
const checkedCount = computed(() =>
  currentStudents.value.filter(s => s.attendanceStatus).length
);
</script>

<style scoped lang="less">
.today-schedule-panel {
  height: 100%;
}

.panel-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.skeleton-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.skeleton-card {
  padding: 16px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
}

.error-block {
  padding: 20px 0;
}

.schedule-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.schedule-card + .schedule-card {
  // spacing handled by gap
}

.student-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.student-summary {
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}

.student-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 4px 8px;
  background: #f5f7fa;
  border-radius: 4px;
  font-size: 13px;
  color: #303133;
}

.text-muted { color: #c0c4cc; }
</style>
```

- [ ] **Step 2: Verify** — schedule timeline with expand/collapse student list, skeleton loading, error, empty states.

---

### Task 6: TodoPanel Component

**Files:**
- Create: `education-store-web/src/views/teacher/dashboard/TodoPanel.vue`

- [ ] **Step 1: Create TodoPanel.vue**

```vue
<!-- 教师工作台 — 待办区 -->
<template>
  <el-card shadow="hover" class="todo-panel">
    <template #header>
      <div class="panel-header">
        <span class="panel-title">📋 待办区</span>
        <el-badge v-if="totalCount" :value="totalCount" :max="99" />
      </div>
    </template>

    <div v-loading="loading">
      <!-- 无待办 -->
      <el-empty v-if="!loading && totalCount === 0" description="✓ 暂无待办事项，今天一切顺利！" :image-size="80" />

      <!-- 待批改作业 -->
      <div v-if="todos?.pendingGradingCount != null" class="todo-item" @click="handleGrading">
        <div class="todo-left">
          <el-icon color="#e6a23c" :size="20"><EditPen /></el-icon>
          <div class="todo-info">
            <div class="todo-label">待批改作业</div>
            <div class="todo-desc">{{ todos.pendingGradingCount }} 份作业等待批改</div>
          </div>
        </div>
        <el-button
          size="small"
          :disabled="todos.pendingGradingCount === 0"
          @click.stop="handleGrading"
        >查看 →</el-button>
      </div>

      <!-- 待回复请假 -->
      <div v-if="todos?.pendingLeaveReplyCount != null" class="todo-item" @click="handleLeave">
        <div class="todo-left">
          <el-icon color="#f56c6c" :size="20"><Bell /></el-icon>
          <div class="todo-info">
            <div class="todo-label">待回复请假</div>
            <div class="todo-desc">{{ todos.pendingLeaveReplyCount }} 条请假待回复</div>
          </div>
        </div>
        <el-button
          size="small"
          :disabled="todos.pendingLeaveReplyCount === 0"
          @click.stop="handleLeave"
        >处理 →</el-button>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import { EditPen, Bell } from '@element-plus/icons-vue';

const props = defineProps({
  todos: { type: Object, default: null },
  loading: { type: Boolean, default: false },
});

const router = useRouter();

const totalCount = computed(() => {
  if (!props.todos) return 0;
  return (props.todos.pendingGradingCount || 0) + (props.todos.pendingLeaveReplyCount || 0);
});

function handleGrading() {
  router.push('/teacher/homework');
}

function handleLeave() {
  router.push('/attendance/leave/student');
}
</script>

<style scoped lang="less">
.todo-panel { height: 100%; }

.panel-header {
  display: flex;
  align-items: center;
  gap: 8px;
}

.panel-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.todo-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 16px;
  margin-bottom: 10px;
  background: #fafafa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;

  &:hover { background: #f0f2f5; transform: translateY(-1px); }
  &:last-child { margin-bottom: 0; }
}

.todo-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.todo-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.todo-label {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.todo-desc {
  font-size: 12px;
  color: #909399;
}
</style>
```

- [ ] **Step 2: Verify** — todo panel handles both items, zero counts disable buttons, empty state when all zero.

---

### Task 7: QuickActions Component

**Files:**
- Create: `education-store-web/src/views/teacher/dashboard/QuickActions.vue`

- [ ] **Step 1: Create QuickActions.vue**

```vue
<!-- 教师工作台 — 快捷入口 -->
<template>
  <el-card shadow="hover" class="quick-panel">
    <template #header>
      <span class="panel-title">⚡ 快捷入口</span>
    </template>
    <div class="quick-actions">
      <div class="quick-item" @click="$router.push('/teacher/resource')">
        <el-icon :size="22"><UploadFilled /></el-icon>
        <span>课件上传</span>
      </div>
      <div class="quick-item" @click="$router.push('/teacher/my-schedule')">
        <el-icon :size="22"><Calendar /></el-icon>
        <span>完整课表</span>
      </div>
      <div class="quick-item" @click="$router.push('/teacher/homework')">
        <el-icon :size="22"><EditPen /></el-icon>
        <span>布置作业</span>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { UploadFilled, Calendar, EditPen } from '@element-plus/icons-vue';
</script>

<style scoped lang="less">
.panel-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.quick-actions {
  display: flex;
  gap: 12px;
}

.quick-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 20px 8px;
  border-radius: 8px;
  background: #f5f7fa;
  cursor: pointer;
  transition: all 0.2s;
  color: #606266;
  font-size: 13px;

  &:hover {
    background: #ecf5ff;
    color: #409eff;
    transform: translateY(-1px);
  }
}
</style>
```

- [ ] **Step 2: Verify** — 3 quick action buttons, horizontal layout.

---

### Task 8: Dashboard Index (Orchestrator)

**Files:**
- Create: `education-store-web/src/views/teacher/dashboard/index.vue`

- [ ] **Step 1: Create index.vue**

```vue
<!-- 教师工作台 — 首页 -->
<template>
  <div class="app-container teacher-dashboard">
    <!-- 欢迎区 -->
    <WelcomeBar
      :user-name="userInfo.userName"
      :server-date-formatted="userInfo.serverDateFormatted"
      :last-login-time="userInfo.lastLoginTime"
      :is-first-login="userInfo.isFirstLogin"
      :refreshing="refreshing"
      @refresh="refreshAll"
    />

    <!-- 统计卡片 -->
    <StatCards :stats="stats" :loading="statsLoading" />

    <!-- 今日课表 + 待办区 -->
    <el-row :gutter="16" class="main-row">
      <el-col :xs="24" :md="16">
        <TodaySchedule
          :schedules="schedules"
          :loading="scheduleLoading"
          :error="scheduleError"
          @retry="loadSchedule"
          @start-signin="handleStartSignin"
          @manage-signin="handleManageSignin"
        />
      </el-col>
      <el-col :xs="24" :md="8">
        <TodoPanel :todos="todos" :loading="todosLoading" />
      </el-col>
    </el-row>

    <!-- 快捷入口 -->
    <QuickActions />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { getUserInfo, getStatistics, getSchedule, getTodos } from '@/api/teacher/dashboard.js';
import WelcomeBar from './WelcomeBar.vue';
import StatCards from './StatCards.vue';
import TodaySchedule from './TodaySchedule.vue';
import TodoPanel from './TodoPanel.vue';
import QuickActions from './QuickActions.vue';

const router = useRouter();

// 数据状态
const userInfo = ref({});
const stats = ref(null);
const schedules = ref([]);
const todos = ref(null);

const statsLoading = ref(false);
const scheduleLoading = ref(false);
const scheduleError = ref(false);
const todosLoading = ref(false);
const refreshing = ref(false);

// 加载全部数据
async function loadAll() {
  const results = await Promise.allSettled([
    getUserInfo(),
    getStatistics(),
    getSchedule(),
    getTodos(),
  ]);

  const [r0, r1, r2, r3] = results;

  // userInfo
  if (r0.status === 'fulfilled' && r0.value?.success) {
    userInfo.value = r0.value.data || {};
  }

  // statistics
  statsLoading.value = true;
  if (r1.status === 'fulfilled' && r1.value?.success) {
    stats.value = r1.value.data;
  }
  statsLoading.value = false;

  // schedule
  scheduleLoading.value = true;
  scheduleError.value = false;
  if (r2.status === 'fulfilled' && r2.value?.success) {
    schedules.value = r2.value.data || [];
  } else if (r2.status === 'rejected' || !r2.value?.success) {
    scheduleError.value = true;
  }
  scheduleLoading.value = false;

  // todos
  todosLoading.value = true;
  if (r3.status === 'fulfilled' && r3.value?.success) {
    todos.value = r3.value.data;
  }
  todosLoading.value = false;
}

async function loadSchedule() {
  scheduleLoading.value = true;
  scheduleError.value = false;
  try {
    const res = await getSchedule();
    if (res.success) {
      schedules.value = res.data || [];
    } else {
      scheduleError.value = true;
    }
  } catch {
    scheduleError.value = true;
  } finally {
    scheduleLoading.value = false;
  }
}

async function refreshAll() {
  refreshing.value = true;
  await loadAll();
  refreshing.value = false;
  ElMessage.success('已刷新');
}

function handleStartSignin(schedule) {
  // 打开签到弹窗或跳转签到页面
  ElMessage.info('签到功能请在考勤管理页面操作');
  router.push('/attendance/signin');
}

function handleManageSignin(schedule) {
  router.push('/attendance/signin');
}

// 定时刷新（5分钟）
let refreshTimer = null;

onMounted(() => {
  loadAll();
  refreshTimer = setInterval(() => {
    loadAll();
  }, 5 * 60 * 1000);
});

onUnmounted(() => {
  if (refreshTimer) clearInterval(refreshTimer);
});
</script>

<style scoped lang="less">
.teacher-dashboard {
  padding: 16px;
  min-height: calc(100vh - 100px);
}

.main-row { margin-bottom: 16px; }
</style>
```

- [ ] **Step 2: Verify** — page orchestrates all sub-components, 4 API parallel load, 5-min refresh, individual error isolation.

---

### Task 9: Homework Page (N09.2)

**Files:**
- Create: `education-store-web/src/views/teacher/homework/index.vue`
- Create: `education-store-web/src/views/teacher/homework/HomeworkForm.vue`

- [ ] **Step 1: Create HomeworkForm.vue**

```vue
<!-- 教师端 — 布置/编辑作业表单弹窗 -->
<template>
  <el-dialog
    :model-value="modelValue"
    @update:model-value="$emit('update:modelValue', $event)"
    :title="isEdit ? '编辑作业' : '布置作业'"
    width="640px"
    destroy-on-close
    @closed="handleClosed"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
      <el-form-item label="关联班级" prop="classId">
        <el-select
          v-model="form.classId"
          placeholder="请选择班级"
          filterable
          remote
          :remote-method="searchClasses"
          :loading="classLoading"
          clearable
          style="width: 100%"
        >
          <el-option
            v-for="c in classOptions"
            :key="c.id"
            :label="c.name"
            :value="c.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="关联课程" prop="courseId">
        <el-select
          v-model="form.courseId"
          placeholder="请选择课程"
          filterable
          clearable
          style="width: 100%"
        >
          <el-option
            v-for="c in courseOptions"
            :key="c.id"
            :label="c.name"
            :value="c.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="作业内容" prop="content">
        <el-input
          v-model="form.content"
          type="textarea"
          :rows="6"
          placeholder="请输入作业内容（最长5000字）"
          maxlength="5000"
          show-word-limit
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="$emit('update:modelValue', false)">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">
        {{ isEdit ? '保存' : '发布' }}
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch } from 'vue';
import { ElMessage } from 'element-plus';
import { listClass } from '@/api/class/classes.js';
import { listCourse } from '@/api/course/courses.js';
import { addHomework, updateHomework } from '@/api/teaching/homework.js';

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  data: { type: Object, default: null },
});

const emit = defineEmits(['update:modelValue', 'saved']);

const isEdit = ref(false);
const formRef = ref(null);
const submitting = ref(false);

const form = reactive({
  id: null,
  classId: null,
  courseId: null,
  content: '',
});

const rules = {
  classId: [{ required: true, message: '请选择班级', trigger: 'change' }],
  courseId: [{ required: true, message: '请选择课程', trigger: 'change' }],
  content: [{ required: true, message: '请输入作业内容', trigger: 'blur' }],
};

// 班级远程搜索
const classOptions = ref([]);
const classLoading = ref(false);
let classSearchTimer = null;
let classSearchSeq = 0;

function searchClasses(keyword) {
  clearTimeout(classSearchTimer);
  if (!keyword) { classOptions.value = []; return; }

  classLoading.value = true;
  classSearchTimer = setTimeout(() => {
    classSearchSeq++;
    const seq = classSearchSeq;
    listClass({ page: 1, pageSize: 100, keyword }).then(res => {
      if (seq !== classSearchSeq) return;
      if (res.success) {
        classOptions.value = res.data?.records || [];
      }
    }).finally(() => {
      if (seq === classSearchSeq) classLoading.value = false;
    });
  }, 300);
}

// 课程选项
const courseOptions = ref([]);

watch(() => form.classId, async (val) => {
  form.courseId = null;
  courseOptions.value = [];
  if (val) {
    try {
      const res = await listCourse({ page: 1, pageSize: 200 });
      if (res.success) {
        courseOptions.value = res.data?.records || [];
      }
    } catch { /* ignore */ }
  }
});

// 编辑模式
watch(() => props.modelValue, (visible) => {
  if (visible && props.data) {
    isEdit.value = true;
    form.id = props.data.id;
    form.classId = props.data.classId;
    form.courseId = props.data.courseId;
    form.content = props.data.content || '';
  } else if (visible) {
    isEdit.value = false;
    resetForm();
  }
});

function resetForm() {
  form.id = null;
  form.classId = null;
  form.courseId = null;
  form.content = '';
  formRef.value?.resetFields();
}

function handleClosed() {
  resetForm();
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false);
  if (!valid) return;

  submitting.value = true;
  try {
    const apiCall = isEdit.value
      ? updateHomework(form.id, { ...form })
      : addHomework({ ...form });
    const res = await apiCall;
    if (res.success) {
      ElMessage.success(isEdit.value ? '保存成功' : '发布成功');
      emit('update:modelValue', false);
      emit('saved');
    } else {
      ElMessage.error(res.msg || '操作失败');
    }
  } catch {
    ElMessage.error('操作失败，请检查网络');
  } finally {
    submitting.value = false;
  }
}
</script>
```

- [ ] **Step 2: Create index.vue**

```vue
<!-- 教师端 — 课后作业列表 -->
<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <el-card shadow="never" class="search-card">
      <el-form :model="query" inline>
        <el-form-item label="作业标题">
          <el-input v-model="query.keyword" placeholder="输入关键词" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="班级">
          <el-select v-model="query.classId" placeholder="全部" clearable filterable remote :remote-method="searchClasses" :loading="classLoading" style="width: 180px">
            <el-option v-for="c in classOptions" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期范围">
          <el-date-picker v-model="query.dateRange" type="daterange" range-separator="至" start-placeholder="开始" end-placeholder="结束" value-format="YYYY-MM-DD" style="width: 260px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 工具栏 -->
    <el-card shadow="never" class="toolbar-card">
      <el-button type="primary" @click="openAdd">+ 布置作业</el-button>
    </el-card>

    <!-- 表格 -->
    <el-card shadow="never">
      <el-table v-loading="loading" :data="list" stripe border>
        <el-table-column label="布置日期" prop="createTime" width="120" />
        <el-table-column label="作业标题" prop="title" min-width="200" show-overflow-tooltip />
        <el-table-column label="班级" prop="className" width="120" />
        <el-table-column label="课程" prop="courseName" width="100" />
        <el-table-column label="附件数" width="80" align="center">
          <template #default="{ row }">
            {{ row.attachmentCount ? row.attachmentCount + '个' : '无' }}
          </template>
        </el-table-column>
        <el-table-column label="发布状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 'PUBLISHED' ? 'success' : 'warning'" size="small">
              {{ row.status === 'PUBLISHED' ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openView(row)">查看</el-button>
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @change="getList"
        />
      </div>
    </el-card>

    <!-- 作业表单弹窗 -->
    <HomeworkForm v-model="formVisible" :data="currentRow" @saved="getList" />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { listHomework, delHomework } from '@/api/teaching/homework.js';
import { listClass } from '@/api/class/classes.js';
import HomeworkForm from './HomeworkForm.vue';

const loading = ref(false);
const list = ref([]);
const total = ref(0);

const query = reactive({
  page: 1,
  pageSize: 10,
  keyword: '',
  classId: null,
  dateRange: null,
});

// 班级远程搜索
const classOptions = ref([]);
const classLoading = ref(false);
let classSearchTimer = null;
let classSearchSeq = 0;

function searchClasses(keyword) {
  clearTimeout(classSearchTimer);
  if (!keyword) { classOptions.value = []; return; }
  classLoading.value = true;
  classSearchTimer = setTimeout(() => {
    classSearchSeq++;
    const seq = classSearchSeq;
    listClass({ page: 1, pageSize: 100, keyword }).then(res => {
      if (seq !== classSearchSeq) return;
      if (res.success) classOptions.value = res.data?.records || [];
    }).finally(() => {
      if (seq === classSearchSeq) classLoading.value = false;
    });
  }, 300);
}

let querySeq = 0;

function getList() {
  loading.value = true;
  querySeq++;
  const seq = querySeq;
  const params = { page: query.page, pageSize: query.pageSize };
  if (query.keyword?.trim()) params.keyword = query.keyword.trim();
  if (query.classId) params.classId = query.classId;
  if (query.dateRange?.length === 2) {
    params.startDate = query.dateRange[0];
    params.endDate = query.dateRange[1];
  }
  listHomework(params).then(res => {
    if (seq !== querySeq) return;
    if (res.success) {
      list.value = res.data?.records || [];
      total.value = res.data?.total || 0;
    }
  }).finally(() => {
    if (seq === querySeq) loading.value = false;
  });
}

function handleQuery() {
  query.page = 1;
  getList();
}

function handleReset() {
  query.keyword = '';
  query.classId = null;
  query.dateRange = null;
  query.page = 1;
  getList();
}

// 表单弹窗
const formVisible = ref(false);
const currentRow = ref(null);

function openAdd() {
  currentRow.value = null;
  formVisible.value = true;
}

function openEdit(row) {
  currentRow.value = { ...row };
  formVisible.value = true;
}

function openView(row) {
  // 简单实现：用编辑弹窗只读查看
  currentRow.value = { ...row };
  formVisible.value = true;
}

function handleDelete(row) {
  ElMessageBox.confirm('确定删除该作业？删除后学生家长端将不可见。', '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    const res = await delHomework(row.id);
    if (res.success) {
      ElMessage.success('删除成功');
      getList();
    } else {
      ElMessage.error(res.msg || '删除失败');
    }
  }).catch(() => {});
}

onMounted(() => {
  getList();
});
</script>

<style scoped lang="less">
.search-card, .toolbar-card { margin-bottom: 12px; }

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
```

- [ ] **Step 3: Verify** — homework CRUD with teacher-scoped data (API auto-filters by current user).

---

### Task 10: Resource Page (N09.3)

**Files:**
- Create: `education-store-web/src/views/teacher/resource/index.vue`
- Create: `education-store-web/src/views/teacher/resource/UploadDialog.vue`

- [ ] **Step 1: Create UploadDialog.vue**

```vue
<!-- 教师端 — 课件上传弹窗 -->
<template>
  <el-dialog
    :model-value="modelValue"
    @update:model-value="$emit('update:modelValue', $event)"
    title="上传课件"
    width="520px"
    destroy-on-close
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
      <el-form-item label="课件名称" prop="name">
        <el-input v-model="form.name" placeholder="请输入课件名称" maxlength="50" />
      </el-form-item>
      <el-form-item label="科目" prop="subject">
        <el-select v-model="form.subject" placeholder="请选择科目" style="width: 100%">
          <el-option label="语文" value="语文" />
          <el-option label="数学" value="数学" />
          <el-option label="英语" value="英语" />
          <el-option label="物理" value="物理" />
          <el-option label="化学" value="化学" />
          <el-option label="其他" value="其他" />
        </el-select>
      </el-form-item>
      <el-form-item label="年级" prop="grade">
        <el-select v-model="form.grade" placeholder="请选择年级" style="width: 100%">
          <el-option v-for="g in grades" :key="g" :label="g" :value="g" />
        </el-select>
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-select v-model="form.type" placeholder="请选择类型" style="width: 100%">
          <el-option label="课件文档" value="课件文档" />
          <el-option label="教学PPT" value="教学PPT" />
          <el-option label="练习资料" value="练习资料" />
          <el-option label="其他" value="其他" />
        </el-select>
      </el-form-item>
      <el-form-item label="共享范围" prop="isPublic">
        <el-radio-group v-model="form.isPublic">
          <el-radio :value="true">公开</el-radio>
          <el-radio :value="false">私有</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="上传文件" prop="fileUrl">
        <el-upload
          ref="uploadRef"
          :auto-upload="false"
          :limit="1"
          :on-change="handleFileChange"
          :on-remove="handleFileRemove"
          accept=".pdf,.doc,.docx,.ppt,.pptx,.xls,.xlsx,.jpg,.png,.gif,.zip,.rar"
        >
          <el-button size="small" type="primary">选择文件</el-button>
          <template #tip>
            <div class="upload-tip">支持 PDF/Word/PPT/图片/压缩包，单文件 ≤ 50MB</div>
          </template>
        </el-upload>
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="$emit('update:modelValue', false)">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">上传</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { ElMessage } from 'element-plus';
import { addResource } from '@/api/course/resources.js';

const props = defineProps({
  modelValue: { type: Boolean, default: false },
});

const emit = defineEmits(['update:modelValue', 'saved']);

const grades = ['一年级', '二年级', '三年级', '四年级', '五年级', '六年级',
  '初一', '初二', '初三', '高一', '高二', '高三'];

const formRef = ref(null);
const uploadRef = ref(null);
const submitting = ref(false);
const selectedFile = ref(null);

const form = reactive({
  name: '',
  subject: '',
  grade: '',
  type: '',
  isPublic: true,
  fileUrl: '',
});

const rules = {
  name: [{ required: true, message: '请输入课件名称', trigger: 'blur' }],
  subject: [{ required: true, message: '请选择科目', trigger: 'change' }],
  grade: [{ required: true, message: '请选择年级', trigger: 'change' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  fileUrl: [{ required: true, message: '请选择文件', trigger: 'change' }],
};

function handleFileChange(file) {
  const maxSize = 50 * 1024 * 1024;
  if (file.size > maxSize) {
    ElMessage.warning('文件大小不能超过 50MB');
    uploadRef.value?.clearFiles();
    return;
  }
  selectedFile.value = file.raw;
  form.fileUrl = file.name;
}

function handleFileRemove() {
  selectedFile.value = null;
  form.fileUrl = '';
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false);
  if (!valid) return;

  submitting.value = true;
  try {
    const res = await addResource({
      name: form.name,
      subject: form.subject,
      grade: form.grade,
      type: form.type,
      isPublic: form.isPublic,
    });
    if (res.success) {
      ElMessage.success('上传成功');
      emit('update:modelValue', false);
      emit('saved');
    } else {
      ElMessage.error(res.msg || '上传失败');
    }
  } catch {
    ElMessage.error('上传失败，请检查网络');
  } finally {
    submitting.value = false;
  }
}
</script>
```

- [ ] **Step 2: Create index.vue** — resource list with card/table view toggle, left category tree, search filters

```vue
<!-- 教师端 — 课件共享 -->
<template>
  <div class="app-container">
    <el-row :gutter="16">
      <!-- 左侧分类 -->
      <el-col :span="4">
        <el-card shadow="never">
          <template #header><span class="panel-title">课件分类</span></template>
          <div v-loading="treeLoading">
            <el-empty v-if="!treeLoading && !treeData.length" description="暂无分类" :image-size="60" />
            <el-tree
              v-else
              :data="treeData"
              :props="{ children: 'children', label: 'name' }"
              node-key="id"
              highlight-current
              @node-click="handleTreeClick"
            />
          </div>
        </el-card>
      </el-col>

      <!-- 右侧内容 -->
      <el-col :span="20">
        <!-- 搜索栏 -->
        <el-card shadow="never" class="search-card">
          <el-form :model="query" inline>
            <el-form-item label="课件名称">
              <el-input v-model="query.keyword" placeholder="输入名称" clearable style="width: 200px" />
            </el-form-item>
            <el-form-item label="共享范围">
              <el-select v-model="query.scope" placeholder="全部" clearable style="width: 120px">
                <el-option label="公开" value="PUBLIC" />
                <el-option label="私有" value="PRIVATE" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleQuery">搜索</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 工具栏 -->
        <el-card shadow="never" class="toolbar-card">
          <div class="toolbar">
            <el-button type="primary" @click="uploadVisible = true">+ 上传课件</el-button>
            <el-radio-group v-model="viewMode" size="small">
              <el-radio-button value="card">📋 卡片</el-radio-button>
              <el-radio-button value="table">📄 表格</el-radio-button>
            </el-radio-group>
          </div>
        </el-card>

        <!-- 卡片视图 -->
        <div v-if="viewMode === 'card'" v-loading="loading" class="card-grid">
          <el-empty v-if="!loading && !list.length" description="暂无课件数据" :image-size="100" />
          <div v-for="item in list" :key="item.id" class="resource-card">
            <div class="card-type-icon">{{ fileTypeIcon(item.fileType) }}</div>
            <div class="card-name">{{ item.name }}</div>
            <div class="card-tags">
              <el-tag size="small">{{ item.subject }}</el-tag>
              <el-tag size="small" type="success">{{ item.grade }}</el-tag>
            </div>
            <div class="card-meta">
              <span>{{ item.uploaderName }}</span>
              <span>{{ item.createTime }}</span>
            </div>
            <div class="card-actions">
              <el-button size="small" text @click="handleDownload(item)">下载</el-button>
              <el-button v-if="item.canEdit" size="small" text type="danger" @click="handleDeleteRes(item)">删除</el-button>
            </div>
          </div>
        </div>

        <!-- 表格视图 -->
        <el-card v-else shadow="never">
          <el-table v-loading="loading" :data="list" stripe border>
            <el-table-column label="课件名称" prop="name" min-width="180" show-overflow-tooltip />
            <el-table-column label="文件类型" prop="fileType" width="90" />
            <el-table-column label="科目" prop="subject" width="80" />
            <el-table-column label="年级" prop="grade" width="80" />
            <el-table-column label="上传人" prop="uploaderName" width="100" />
            <el-table-column label="上传时间" prop="createTime" width="160" />
            <el-table-column label="共享范围" width="90" align="center">
              <template #default="{ row }">
                <el-tag :type="row.isPublic ? 'success' : 'warning'" size="small">
                  {{ row.isPublic ? '公开' : '私有' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="140" align="center">
              <template #default="{ row }">
                <el-button link type="primary" @click="handleDownload(row)">下载</el-button>
                <el-button v-if="row.canEdit" link type="danger" @click="handleDeleteRes(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <div class="pagination-wrap">
          <el-pagination
            v-model:current-page="query.page"
            v-model:page-size="query.pageSize"
            :total="total"
            :page-sizes="[12, 24, 48]"
            layout="total, sizes, prev, pager, next"
            @change="getList"
          />
        </div>
      </el-col>
    </el-row>

    <UploadDialog v-model="uploadVisible" @saved="getList" />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { listResource, delResource, recordDownload, getCategoryTree } from '@/api/course/resources.js';
import UploadDialog from './UploadDialog.vue';

const loading = ref(false);
const list = ref([]);
const total = ref(0);
const viewMode = ref('card');

const query = reactive({
  page: 1,
  pageSize: 12,
  keyword: '',
  scope: null,
  categoryId: null,
});

// 分类树
const treeData = ref([]);
const treeLoading = ref(false);

async function loadTree() {
  treeLoading.value = true;
  try {
    const res = await getCategoryTree();
    if (res.success) treeData.value = res.data || [];
  } catch { /* ignore */ }
  treeLoading.value = false;
}

function handleTreeClick(node) {
  query.categoryId = node.id;
  query.page = 1;
  getList();
}

let querySeq = 0;

function getList() {
  loading.value = true;
  querySeq++;
  const seq = querySeq;
  const params = { page: query.page, pageSize: query.pageSize };
  if (query.keyword?.trim()) params.keyword = query.keyword.trim();
  if (query.scope) params.scope = query.scope;
  if (query.categoryId) params.categoryId = query.categoryId;
  listResource(params).then(res => {
    if (seq !== querySeq) return;
    if (res.success) {
      list.value = (res.data?.records || []).map(r => ({
        ...r,
        canEdit: r.uploaderId === r.currentUserId, // 后端应返回此字段
      }));
      total.value = res.data?.total || 0;
    }
  }).finally(() => {
    if (seq === querySeq) loading.value = false;
  });
}

function handleQuery() {
  query.page = 1;
  getList();
}

function handleReset() {
  query.keyword = '';
  query.scope = null;
  query.categoryId = null;
  query.page = 1;
  getList();
}

function handleDownload(row) {
  recordDownload(row.id).then(() => {
    // 触发浏览器下载
    if (row.fileUrl) window.open(row.fileUrl, '_blank');
  }).catch(() => {
    ElMessage.error('下载失败');
  });
}

function handleDeleteRes(row) {
  ElMessageBox.confirm(`确认删除课件「${row.name}」？删除后将无法恢复。`, '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    const res = await delResource(row.id);
    if (res.success) {
      ElMessage.success('删除成功');
      getList();
    } else {
      ElMessage.error(res.msg || '删除失败');
    }
  }).catch(() => {});
}

function fileTypeIcon(type) {
  const map = { pdf: '📄', doc: '📝', ppt: '📊', xls: '📈', image: '🖼️' };
  return map[(type || '').toLowerCase()] || '📁';
}

const uploadVisible = ref(false);

onMounted(() => {
  loadTree();
  getList();
});
</script>

<style scoped lang="less">
.search-card, .toolbar-card { margin-bottom: 12px; }

.panel-title { font-size: 14px; font-weight: 600; }

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 12px;
  margin-bottom: 16px;
}

.resource-card {
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 16px;
  transition: all 0.2s;
  &:hover { box-shadow: 0 2px 8px rgba(0,0,0,0.08); }
}

.card-type-icon { font-size: 32px; margin-bottom: 8px; }

.card-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-tags {
  display: flex;
  gap: 4px;
  margin-bottom: 8px;
}

.card-meta {
  display: flex;
  justify-content: space-between;
  font-size: 11px;
  color: #909399;
  margin-bottom: 8px;
}

.card-actions {
  display: flex;
  justify-content: flex-end;
  gap: 4px;
  border-top: 1px solid #ebeef5;
  padding-top: 8px;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
```

- [ ] **Step 3: Verify** — card/table views, category tree, upload dialog, download with count, delete own only.

---

### Task 11: My Schedule Page (N09.4)

**Files:**
- Create: `education-store-web/src/views/teacher/my-schedule/index.vue`

- [ ] **Step 1: Create index.vue**

```vue
<!-- 教师端 — 我的课表（只读） -->
<template>
  <div class="app-container">
    <el-card shadow="never">
      <!-- 工具栏 -->
      <div class="schedule-toolbar">
        <div class="toolbar-left">
          <el-radio-group v-model="viewType" size="small" @change="loadData">
            <el-radio-button value="WEEK">周视图</el-radio-button>
            <el-radio-button value="MONTH">月视图</el-radio-button>
          </el-radio-group>
        </div>
        <div class="toolbar-center">
          <el-button size="small" text @click="goPrev">
            <el-icon><ArrowLeft /></el-icon>
          </el-button>
          <span class="period-label">{{ periodLabel }}</span>
          <el-button size="small" text @click="goNext">
            <el-icon><ArrowRight /></el-icon>
          </el-button>
          <el-button size="small" @click="goToday">今天</el-button>
        </div>
        <div class="toolbar-right">
          <el-select v-model="filterClassId" placeholder="班级" clearable size="small" style="width: 140px" @change="loadData">
            <el-option v-for="c in classOptions" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
          <el-button size="small" @click="resetFilter">重置筛选</el-button>
        </div>
      </div>

      <!-- 周视图 -->
      <div v-if="viewType === 'WEEK'" v-loading="loading" class="week-grid">
        <div v-for="day in weekData" :key="day.date" class="week-col" :class="{ 'is-today': day.isToday }">
          <div class="week-col-header">
            <span class="week-date">{{ formatDate(day.date) }}</span>
            <span class="week-day-label">{{ day.dayLabel }}</span>
          </div>
          <div class="week-col-body">
            <div
              v-for="s in day.schedules || []" :key="s.scheduleId"
              class="week-schedule-card"
              :style="{ borderTopColor: subjectColor(s.subject) }"
              @click="showDetail(s)"
            >
              <div class="ws-time">{{ s.startTime }} - {{ s.endTime }}</div>
              <div class="ws-course">{{ s.courseName }}</div>
              <div class="ws-class">{{ s.className }}</div>
              <div class="ws-room">{{ s.classroomName }}</div>
            </div>
            <el-empty v-if="!day.schedules?.length" description="--" :image-size="30" />
          </div>
        </div>
      </div>

      <!-- 月视图 -->
      <div v-else v-loading="loading" class="month-grid-wrap">
        <div class="weekday-row">
          <div v-for="d in weekdays" :key="d" class="weekday-cell">{{ d }}</div>
        </div>
        <div class="month-grid">
          <div
            v-for="(cell, i) in monthCells" :key="i"
            class="month-cell"
            :class="{ 'is-today': cell.isToday, 'is-other': !cell.isCurrentMonth, 'is-empty': !cell.date }"
            @click="cell.date && cell.schedules?.length && showDayDrawer(cell)"
          >
            <template v-if="cell.date">
              <div class="mc-date">{{ cell.dayNum }}</div>
              <div v-if="cell.schedules?.length" class="mc-count">{{ cell.schedules.length }} 节课</div>
            </template>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 排课详情弹窗 -->
    <el-dialog v-model="detailVisible" title="排课详情" width="480px" destroy-on-close>
      <div v-if="detailData">
        <el-descriptions :column="1" border size="small">
          <el-descriptions-item label="课程">{{ detailData.courseName }}</el-descriptions-item>
          <el-descriptions-item label="班级">{{ detailData.className }}</el-descriptions-item>
          <el-descriptions-item label="时间">{{ detailData.startTime }} - {{ detailData.endTime }}</el-descriptions-item>
          <el-descriptions-item label="教室">{{ detailData.classroomName }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>

    <!-- 月视图日抽屉 -->
    <el-drawer v-model="drawerVisible" title="当日排课" size="480px">
      <div v-for="s in drawerSchedules" :key="s.scheduleId" class="drawer-schedule" @click="showDetail(s)">
        <div class="ds-time">{{ s.startTime }} - {{ s.endTime }}</div>
        <div class="ds-course">{{ s.courseName }}</div>
        <div class="ds-meta">{{ s.className }} · {{ s.classroomName }}</div>
      </div>
      <el-empty v-if="!drawerSchedules.length" description="当日无排课" />
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted } from 'vue';
import { ArrowLeft, ArrowRight } from '@element-plus/icons-vue';
import { listSchedule } from '@/api/schedule/schedules.js';
import { listClass } from '@/api/class/classes.js';

const viewType = ref('WEEK');
const loading = ref(false);
const currentDate = ref(todayStr());
const weekData = ref([]);
const monthData = ref([]);
const filterClassId = ref(null);
const classOptions = ref([]);

const weekdays = ['一', '二', '三', '四', '五', '六', '日'];

function todayStr() {
  const d = new Date();
  return d.getFullYear() + '-' + String(d.getMonth() + 1).padStart(2, '0') + '-' + String(d.getDate()).padStart(2, '0');
}

const periodLabel = computed(() => {
  if (viewType.value === 'WEEK' && weekData.value.length) {
    return weekData.value[0].date + ' ~ ' + weekData.value[weekData.value.length - 1].date;
  }
  if (viewType.value === 'MONTH') {
    const d = parseDate(currentDate.value);
    if (d) return d.getFullYear() + '年' + (d.getMonth() + 1) + '月';
  }
  return '--';
});

function formatDate(dateStr) {
  const parts = (dateStr || '').split('-');
  return parts[2] ? parseInt(parts[2], 10) + '日' : '--';
}

function subjectColor(subject) {
  const map = { '数学': '#409EFF', '语文': '#67C23A', '英语': '#E6A23C', '物理': '#9B59B6' };
  return map[subject] || '#909399';
}

// 月视图网格
const monthCells = computed(() => {
  if (!monthData.value.length) return [];
  const firstDay = monthData.value[0];
  const offset = (firstDay.dayOfWeek || 1) - 1;
  const cells = [];
  for (let i = 0; i < offset; i++) {
    cells.push({ date: null, isToday: false, isCurrentMonth: false, schedules: [] });
  }
  for (const d of monthData.value) {
    const dayNum = parseInt((d.date || '').split('-')[2], 10) || 0;
    cells.push({ ...d, dayNum });
  }
  return cells;
});

let querySeq = 0;

async function loadData() {
  loading.value = true;
  querySeq++;
  const seq = querySeq;
  const params = {
    viewType: viewType.value,
    date: currentDate.value,
  };
  if (filterClassId.value) params.classId = filterClassId.value;

  try {
    const res = await listSchedule(params);
    if (seq !== querySeq) return;
    if (res.success) {
      const data = res.data || {};
      if (viewType.value === 'WEEK') {
        weekData.value = data.dates || [];
      } else {
        monthData.value = data.dates || [];
      }
    }
  } catch { /* ignore */ }
  finally {
    if (seq === querySeq) loading.value = false;
  }
}

function goPrev() {
  const d = parseDate(currentDate.value);
  if (!d) return;
  if (viewType.value === 'MONTH') d.setMonth(d.getMonth() - 1);
  else d.setDate(d.getDate() - 7);
  currentDate.value = formatDateStr(d);
  loadData();
}

function goNext() {
  const d = parseDate(currentDate.value);
  if (!d) return;
  if (viewType.value === 'MONTH') d.setMonth(d.getMonth() + 1);
  else d.setDate(d.getDate() + 7);
  currentDate.value = formatDateStr(d);
  loadData();
}

function goToday() {
  currentDate.value = todayStr();
  loadData();
}

function resetFilter() {
  filterClassId.value = null;
  loadData();
}

// 详情弹窗
const detailVisible = ref(false);
const detailData = ref(null);

function showDetail(schedule) {
  detailData.value = schedule;
  detailVisible.value = true;
}

// 月视图日抽屉
const drawerVisible = ref(false);
const drawerSchedules = ref([]);

function showDayDrawer(cell) {
  drawerSchedules.value = cell.schedules || [];
  drawerVisible.value = true;
}

function parseDate(str) {
  const parts = str.split('-');
  if (parts.length !== 3) return null;
  return new Date(parseInt(parts[0]), parseInt(parts[1]) - 1, parseInt(parts[2]));
}

function formatDateStr(d) {
  return d.getFullYear() + '-' + String(d.getMonth() + 1).padStart(2, '0') + '-' + String(d.getDate()).padStart(2, '0');
}

// 班级选项
async function loadClassOptions() {
  try {
    const res = await listClass({ page: 1, pageSize: 200 });
    if (res.success) classOptions.value = res.data?.records || [];
  } catch { /* ignore */ }
}

onMounted(() => {
  loadClassOptions();
  loadData();
});
</script>

<style scoped lang="less">
.schedule-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  flex-wrap: wrap;
  gap: 8px;
}

.period-label {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  min-width: 180px;
  text-align: center;
}

.toolbar-right {
  display: flex;
  gap: 8px;
}

// 周视图
.week-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 8px;
  min-height: 500px;
}

.week-col {
  border: 1px solid #ebeef5;
  border-radius: 6px;
  overflow: hidden;

  &.is-today {
    border-color: #409eff;
    box-shadow: 0 0 0 1px #409eff;
  }
}

.week-col-header {
  display: flex;
  justify-content: space-between;
  padding: 6px 8px;
  background: #f5f7fa;
  .is-today & { background: #ecf5ff; }
}

.week-date { font-size: 15px; font-weight: 600; color: #303133; }
.week-day-label { font-size: 11px; color: #909399; }

.week-col-body {
  padding: 6px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.week-schedule-card {
  padding: 6px 8px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 11px;
  border-top: 3px solid #409eff;
  background: #f5f7fa;
  &:hover { background: #ecf5ff; }
}

.ws-time { font-weight: 500; color: #303133; }
.ws-course { color: #409eff; }
.ws-class { color: #606266; }
.ws-room { color: #909399; }

// 月视图
.month-grid-wrap { min-height: 400px; }

.weekday-row {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  margin-bottom: 4px;
}

.weekday-cell {
  text-align: center;
  font-size: 12px;
  font-weight: 600;
  color: #909399;
  padding: 6px 0;
}

.month-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  border-left: 1px solid #ebeef5;
  border-top: 1px solid #ebeef5;
}

.month-cell {
  min-height: 80px;
  border-right: 1px solid #ebeef5;
  border-bottom: 1px solid #ebeef5;
  padding: 4px;
  cursor: pointer;
  transition: background 0.15s;

  &.is-empty { background: #fafafa; cursor: default; }
  &.is-other { background: #fafafa; }
  &.is-today { background: #ecf5ff; }
  &:hover:not(.is-empty) { background: #f0f2f5; }
}

.mc-date {
  font-size: 13px;
  font-weight: 500;
  color: #303133;
  .is-today & { color: #409eff; font-weight: 700; }
}

.mc-count {
  font-size: 11px;
  color: #409eff;
  margin-top: 4px;
}

// 抽屉
.drawer-schedule {
  padding: 12px;
  margin-bottom: 8px;
  background: #f5f7fa;
  border-radius: 6px;
  cursor: pointer;
  &:hover { background: #ecf5ff; }
}

.ds-time { font-weight: 600; color: #303133; margin-bottom: 4px; }
.ds-course { color: #409eff; margin-bottom: 2px; }
.ds-meta { font-size: 12px; color: #909399; }
</style>
```

- [ ] **Step 2: Verify** — week/month views, navigation, detail dialog, class filter, read-only (no cancel/reschedule buttons).

---

### Task 12: Final Verification

- [ ] **Step 1: Verify all files exist**

Run: verify 13 files created under `education-store-web/src/`

- [ ] **Step 2: Verify API pattern consistency**

API file uses `request` from `@/utils/request`, consistent with all other API modules.

- [ ] **Step 3: Verify component conventions**

All components use `<script setup>`, `<style scoped lang="less">`, Composition API, PascalCase filenames.

- [ ] **Step 4: Verify route compatibility**

File paths match `views/` glob pattern in router store: `teacher/dashboard/index`, `teacher/homework/index`, `teacher/resource/index`, `teacher/my-schedule/index`.

---

## Summary

| Task | Files | Description |
|:--:|------|------|
| 1 | 1 | API layer (`src/api/teacher/dashboard.js`) |
| 2-8 | 7 | N09.1 Dashboard (index + 6 sub-components) |
| 9 | 2 | N09.2 Homework (list + form dialog) |
| 10 | 2 | N09.3 Resource (list + upload dialog) |
| 11 | 1 | N09.4 My Schedule (week/month calendar) |
| 12 | — | Final verification |

**Total: 13 files created, 0 files modified.**
