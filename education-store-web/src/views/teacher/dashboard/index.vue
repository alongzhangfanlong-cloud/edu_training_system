<!-- 教师工作台 — 首页 -->
<template>
  <div class="app-container teacher-dashboard">
    <!-- 1. 欢迎区 -->
    <WelcomeBar
      :user-name="userInfo.userName"
      :server-date-formatted="userInfo.serverDateFormatted"
      :last-login-time="userInfo.lastLoginTime"
      :is-first-login="userInfo.isFirstLogin"
      :refreshing="refreshing"
      @refresh="refreshAll"
    />

    <!-- 2. 统计卡片 -->
    <StatCards :stats="stats" :loading="statsLoading" />

    <!-- 3. 今日课表 + 待办区 -->
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

    <!-- 4. 快捷入口 -->
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
  // 必须在 Promise.allSettled 之前设置 loading，否则 UI 不会渲染加载态
  statsLoading.value = true;
  scheduleLoading.value = true;
  todosLoading.value = true;

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
  if (r1.status === 'fulfilled' && r1.value?.success) {
    stats.value = r1.value.data;
  }
  statsLoading.value = false;

  // schedule
  scheduleError.value = false;
  if (r2.status === 'fulfilled' && r2.value?.success) {
    schedules.value = r2.value.data || [];
  } else if (r2.status === 'rejected' || !r2.value?.success) {
    scheduleError.value = true;
  }
  scheduleLoading.value = false;

  // todos
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

function handleStartSignin() {
  ElMessage.info('签到功能请在考勤管理页面操作');
  router.push('/attendance/signin');
}

function handleManageSignin() {
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
