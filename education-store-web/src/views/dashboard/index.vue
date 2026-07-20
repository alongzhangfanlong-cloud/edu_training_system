<!-- 工作台 Dashboard — 管理员首页 -->
<template>
  <div class="app-container dashboard">
    <!-- 1. 欢迎区 -->
    <div class="welcome-bar">
      <div class="welcome-info">
        <h2 class="welcome-greeting">你好，{{ userInfo.userName || '--' }}！</h2>
        <p class="welcome-date">{{ userInfo.serverDateFormatted || '--' }}</p>
        <p v-if="userInfo.lastLoginTime" class="welcome-login">上次登录：{{ userInfo.lastLoginTime }}</p>
        <p v-else-if="userInfo.isFirstLogin" class="welcome-login welcome-first">🎉 欢迎首次登录！</p>
      </div>
      <el-button @click="refreshAll" :loading="refreshing" :icon="Refresh" circle />
    </div>

    <!-- 2. 统计卡片 -->
    <el-row :gutter="16" class="stat-row">
      <el-col :xs="24" :sm="12" :md="6" v-for="card in statCards" :key="card.key">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-inner">
            <div class="stat-icon" :style="{ background: card.bg }">
              <el-icon :size="28"><component :is="card.icon" /></el-icon>
            </div>
            <div class="stat-body">
              <div class="stat-label">{{ card.label }}</div>
              <div class="stat-value">
                <span v-loading="statsLoading">{{ card.formatter(stats) }}</span>
              </div>
              <div v-if="card.key === 'revenue' && stats?.revenueTrend && stats.revenueTrend !== 'FLAT' && stats.revenueTrend !== 'NONE'" class="stat-trend" :class="stats.revenueTrend === 'UP' ? 'trend-up' : 'trend-down'">
                <el-icon><component :is="stats.revenueTrend === 'UP' ? 'Top' : 'Bottom'" /></el-icon>
                {{ stats.revenueTrendPercentage }}%
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 3. 排课日历 + 待办提醒 -->
    <el-row :gutter="16" class="main-row">
      <el-col :xs="24" :md="16">
        <CalendarPanel ref="calendarRef" />
      </el-col>
      <el-col :xs="24" :md="8">
        <TodoPanel :todos="todos" :loading="todosLoading" />
      </el-col>
    </el-row>

    <!-- 4. 本月报名统计 + 快捷入口 -->
    <el-row :gutter="16" class="bottom-row">
      <el-col :xs="24" :md="16">
        <el-card shadow="hover" class="enrollment-panel">
          <template #header>
            <div class="panel-header">
              <span class="panel-title">本月报名统计</span>
              <el-tag v-if="enrollmentStats?.monthLabel" size="small" type="info">{{ enrollmentStats.monthLabel }}</el-tag>
            </div>
          </template>
          <div v-loading="enrollmentLoading">
            <div v-if="enrollmentStats?.totalEnrollments != null" class="enrollment-total">
              本月共 <strong>{{ enrollmentStats.totalEnrollments }}</strong> 人次报名
            </div>
            <div v-if="enrollmentStats?.courses?.length" class="enrollment-list">
              <div v-for="c in enrollmentStats.courses" :key="c.courseId" class="enrollment-item">
                <div class="enrollment-name">{{ c.courseName }}</div>
                <div class="enrollment-bar-wrap">
                  <div class="enrollment-bar" :style="{ width: barWidth(c.enrollmentCount) }" />
                </div>
                <div class="enrollment-count">{{ c.enrollmentCount }}人</div>
              </div>
            </div>
            <el-empty v-else-if="!enrollmentLoading" description="暂无本月报名数据" :image-size="80" />
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="8">
        <el-card shadow="hover" class="quick-panel">
          <template #header>
            <span class="panel-title">快捷入口</span>
          </template>
          <div class="quick-actions">
            <div class="quick-item" v-permission="['enrollment:enrollment:add']" @click="$router.push('/finance/tuition')">
              <el-icon :size="22"><EditPen /></el-icon>
              <span>报名录入</span>
            </div>
            <div class="quick-item" v-permission="['schedule:plan:add']" @click="$router.push('/course/schedulePlan')">
              <el-icon :size="22"><Calendar /></el-icon>
              <span>排课操作</span>
            </div>
            <div class="quick-item" v-permission="['attendance:leave:teacher:approve', 'attendance:leave:student:approve']" @click="$router.push('/attendance/leave/teacher')">
              <el-icon :size="22"><DocumentChecked /></el-icon>
              <span>请假审批</span>
            </div>
            <div class="quick-item" v-permission="['tuition:fee:list']" @click="$router.push('/finance/tuition')">
              <el-icon :size="22"><Money /></el-icon>
              <span>收费管理</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import {
  User, Calendar, Money, Refresh,
  Top, Bottom, EditPen, DocumentChecked,
} from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import { getUserInfo, getStatistics, getTodos, getEnrollmentStats } from '@/api/dashboard/index.js';
import CalendarPanel from './CalendarPanel.vue';
import TodoPanel from './TodoPanel.vue';

const router = useRouter();
const calendarRef = ref(null);

// 数据状态
const userInfo = ref({});
const stats = ref(null);
const todos = ref(null);
const enrollmentStats = ref(null);

const statsLoading = ref(false);
const todosLoading = ref(false);
const enrollmentLoading = ref(false);
const refreshing = ref(false);

// 统计卡片配置
const statCards = [
  {
    key: 'studentCount',
    label: '在学学员',
    icon: 'User',
    bg: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    formatter: (s) => s?.studentCount ?? '--',
  },
  {
    key: 'scheduleCount',
    label: '当月排课',
    icon: 'Calendar',
    bg: 'linear-gradient(135deg, #11998e 0%, #38ef7d 100%)',
    formatter: (s) => s?.scheduleCount ?? '--',
  },
  {
    key: 'teacherCount',
    label: '在职教师',
    icon: 'User',
    bg: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    formatter: (s) => s?.teacherCount ?? '--',
  },
  {
    key: 'revenue',
    label: '本月营收',
    icon: 'Money',
    bg: 'linear-gradient(135deg, #fa8231 0%, #f7b731 100%)',
    formatter: (s) => s?.monthlyRevenue != null ? `¥${formatMoney(s.monthlyRevenue)}` : '--',
  },
];

// 格式化金额
function formatMoney(val) {
  if (val == null) return '--';
  const num = Number(val);
  if (Number.isNaN(num)) return '--';
  if (num >= 10000) return (num / 10000).toFixed(1) + '万';
  return num.toLocaleString('zh-CN', { minimumFractionDigits: 0, maximumFractionDigits: 0 });
}

// 进度条宽度
const maxEnrollment = computed(() => {
  if (!enrollmentStats.value?.courses?.length) return 1;
  return Math.max(...enrollmentStats.value.courses.map(c => c.enrollmentCount || 0), 1);
});

function barWidth(count) {
  return ((count || 0) / maxEnrollment.value * 100).toFixed(0) + '%';
}

// 加载全部数据
async function loadAll() {
  const results = await Promise.allSettled([
    getUserInfo(),
    getStatistics(),
    getTodos(),
    getEnrollmentStats(),
  ]);

  // userInfo
  const [r0, r1, r2, r3] = results;
  if (r0.status === 'fulfilled' && r0.value?.success) {
    userInfo.value = r0.value.data || {};
  }
  // statistics
  statsLoading.value = true;
  if (r1.status === 'fulfilled' && r1.value?.success) {
    stats.value = r1.value.data;
  }
  statsLoading.value = false;
  // todos
  todosLoading.value = true;
  if (r2.status === 'fulfilled' && r2.value?.success) {
    todos.value = r2.value.data;
  }
  todosLoading.value = false;
  // enrollmentStats
  enrollmentLoading.value = true;
  if (r3.status === 'fulfilled' && r3.value?.success) {
    enrollmentStats.value = r3.value.data;
  }
  enrollmentLoading.value = false;
}

async function refreshAll() {
  refreshing.value = true;
  await loadAll();
  calendarRef.value?.refresh();
  refreshing.value = false;
  ElMessage.success('已刷新');
}

// 定时刷新（5分钟）
let refreshTimer = null;

onMounted(() => {
  loadAll();
  // 日历组件自己加载数据
  refreshTimer = setInterval(() => {
    loadAll();
    calendarRef.value?.refresh();
  }, 5 * 60 * 1000);
});

onUnmounted(() => {
  if (refreshTimer) clearInterval(refreshTimer);
});
</script>

<style scoped lang="less">
.dashboard {
  padding: 16px;
  min-height: calc(100vh - 100px);
}

// 欢迎区
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

// 统计卡片行
.stat-row { margin-bottom: 16px; }

.stat-card {
  :deep(.el-card__body) { padding: 16px 20px; }
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

.stat-body {
  flex: 1;
  min-width: 0;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #303133;
  line-height: 1.2;
}

.stat-trend {
  font-size: 12px;
  margin-top: 2px;
  display: flex;
  align-items: center;
  gap: 2px;

  &.trend-up { color: #67c23a; }
  &.trend-down { color: #f56c6c; }
}

// 主内容行
.main-row { margin-bottom: 16px; }

// 面板头部
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

// 底部行
.bottom-row { margin-bottom: 16px; }

// 报名统计
.enrollment-total {
  font-size: 14px;
  color: #606266;
  margin-bottom: 16px;

  strong { color: #409eff; font-size: 18px; }
}

.enrollment-list { display: flex; flex-direction: column; gap: 12px; }

.enrollment-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.enrollment-name {
  width: 120px;
  font-size: 13px;
  color: #606266;
  text-align: right;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex-shrink: 0;
}

.enrollment-bar-wrap {
  flex: 1;
  height: 20px;
  background: #f0f2f5;
  border-radius: 10px;
  overflow: hidden;
}

.enrollment-bar {
  height: 100%;
  background: linear-gradient(90deg, #409eff 0%, #66b1ff 100%);
  border-radius: 10px;
  transition: width 0.6s ease;
  min-width: 2px;
}

.enrollment-count {
  width: 40px;
  font-size: 13px;
  color: #303133;
  font-weight: 500;
  flex-shrink: 0;
}

// 快捷入口
.quick-actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.quick-item {
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
