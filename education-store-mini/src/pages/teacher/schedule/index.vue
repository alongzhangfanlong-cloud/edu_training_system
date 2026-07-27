<template>
  <view class="schedule-page">
    <!-- 视图切换 -->
    <view class="view-bar">
      <view class="view-tabs">
        <text :class="{ active: viewMode === 'week' }" @click="switchView('week')">周视图</text>
        <text :class="{ active: viewMode === 'month' }" @click="switchView('month')">月视图</text>
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
      <view v-else-if="error"><empty-state icon="⚠️" text="加载失败" :show-retry="true" @retry="loadData" /></view>
      <view v-else-if="!list.length"><empty-state icon="📅" text="暂无排课" /></view>
      <view v-else class="week-body">
        <schedule-card
          v-for="item in list"
          :key="item.scheduleId || item.id"
          :start-time="item.startTime"
          :end-time="item.endTime"
          :course-name="item.courseName"
          :class-name="item.className"
          :classroom-name="item.classroomName"
          :expected-count="item.expectedCount || 0"
          :checked-count="item.checkedCount || 0"
          :course-status="item.courseStatus"
          :attendance-status="item.attendanceStatus"
        />
      </view>
    </view>

    <!-- 月视图 -->
    <view v-else class="month-view">
      <view class="month-grid">
        <view v-for="d in monthDays" :key="d.dateStr || d.day" class="month-cell" :class="{ today: d.isToday, 'has-course': d.hasCourse }" @click="selectMonthDay(d)">
          <text class="mc-date">{{ d.day }}</text>
          <view v-if="d.hasCourse" class="mc-dot" />
        </view>
      </view>
      <view v-if="selectedDay" class="day-detail">
        <text class="dd-title">{{ selectedDay.dateStr }} 课程</text>
        <schedule-card
          v-for="item in selectedDayCourses"
          :key="item.scheduleId || item.id"
          :start-time="item.startTime"
          :end-time="item.endTime"
          :course-name="item.courseName"
          :class-name="item.className"
          :classroom-name="item.classroomName"
          :expected-count="item.expectedCount || 0"
          :checked-count="item.checkedCount || 0"
          :course-status="item.courseStatus"
          :attendance-status="item.attendanceStatus"
        />
        <view v-if="!selectedDayCourses.length"><empty-state icon="📅" text="当天无课程" /></view>
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
  return currentMonthFirst.value.getFullYear() + '年' + (currentMonthFirst.value.getMonth() + 1) + '月';
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
  const firstDayOfWeek = new Date(y, m, 1).getDay() || 7;
  const daysInMonth = new Date(y, m + 1, 0).getDate();
  const courseDates = new Set(list.value.map(i => i.scheduleDate || i.date));
  const cells = [];
  for (let i = 1; i < firstDayOfWeek; i++) cells.push({ day: '', dateStr: '', isToday: false, hasCourse: false });
  for (let d = 1; d <= daysInMonth; d++) {
    const ds = y + '-' + String(m + 1).padStart(2, '0') + '-' + String(d).padStart(2, '0');
    cells.push({ day: d, dateStr: ds, isToday: fmt(now) === ds, hasCourse: courseDates.has(ds) });
  }
  return cells;
});

const selectedDay = ref(null);
const selectedDayCourses = computed(() => {
  if (!selectedDay.value) return [];
  return list.value.filter(i => (i.scheduleDate || i.date) === selectedDay.value.dateStr);
});

function selectMonthDay(d) { if (d.hasCourse) selectedDay.value = d; }

async function loadData() {
  loading.value = true; error.value = false;
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
    if (res.code === 200) list.value = res.data || [];
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
  selectedDay.value = null;
  loadData();
}
function switchView(mode) { viewMode.value = mode; selectedDay.value = null; loadData(); }

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
.week-header { display: flex; margin-bottom: 12rpx; }
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
