<!-- 排课日历面板 — 月/周/日三视图 -->
<template>
  <el-card shadow="hover" class="calendar-panel">
    <!-- 头部：视图切换 + 导航 -->
    <div class="calendar-header">
      <el-radio-group v-model="viewType" size="small" @change="onViewChange">
        <el-radio-button value="MONTH">月</el-radio-button>
        <el-radio-button value="WEEK">周</el-radio-button>
        <el-radio-button value="DAY">日</el-radio-button>
      </el-radio-group>
      <div class="calendar-nav">
        <el-button size="small" text :disabled="data ? !data.hasPrev : true" @click="goPrev">
          <el-icon><ArrowLeft /></el-icon>
        </el-button>
        <span class="calendar-label">{{ data?.viewLabel || '--' }}</span>
        <el-button size="small" text :disabled="data ? !data.hasNext : true" @click="goNext">
          <el-icon><ArrowRight /></el-icon>
        </el-button>
        <el-button v-if="!data?.isCurrentPeriod" size="small" @click="goToday">今天</el-button>
      </div>
    </div>

    <!-- 加载遮罩 -->
    <div v-loading="loading" class="calendar-body">

      <!-- ==================== 月视图 ==================== -->
      <div v-if="viewType === 'MONTH'" class="month-view">
        <div class="weekday-header">
          <div v-for="d in weekdays" :key="d" class="weekday-cell">{{ d }}</div>
        </div>
        <div class="month-grid">
          <div
            v-for="(cell, i) in monthCells" :key="i"
            class="month-cell"
            :class="{
              'is-today': cell.isToday,
              'is-other-month': !cell.isCurrentMonth && cell.date,
              'is-empty': !cell.date,
            }"
          >
            <template v-if="cell.date">
              <div class="cell-date">
                <span class="cell-day">{{ cell.dayNum }}</span>
                <span class="cell-weekday">{{ cell.dayLabel }}</span>
              </div>
              <div v-if="cell.schedules?.length" class="cell-schedules">
                <div
                  v-for="s in cell.schedules.slice(0, cell.displayCount || 3)" :key="s.scheduleId"
                  class="cell-schedule"
                  :class="'schedule-' + (s.status || 'NOT_STARTED').toLowerCase()"
                  @click.stop="showStudents(s)"
                >
                  <span class="schedule-time">{{ s.startTime }}</span>
                  <span class="schedule-name">{{ s.courseName }}</span>
                </div>
                <div
                  v-if="cell.totalCount > (cell.displayCount || 3)"
                  class="cell-more"
                  @click.stop="switchToDayView(cell.date)"
                >
                  +{{ cell.totalCount - (cell.displayCount || 3) }} 更多
                </div>
              </div>
            </template>
          </div>
        </div>
      </div>

      <!-- ==================== 周视图 ==================== -->
      <div v-else-if="viewType === 'WEEK'" class="week-view">
        <div
          v-for="d in data?.dates || []" :key="d.date"
          class="week-day"
          :class="{ 'is-today': d.isToday }"
        >
          <div class="week-day-header">
            <span class="week-date">{{ formatDayHeader(d) }}</span>
            <span class="week-label">{{ d.dayLabel }}</span>
          </div>
          <div class="week-schedules">
            <div
              v-for="s in d.schedules || []" :key="s.scheduleId"
              class="week-schedule" :class="'schedule-' + (s.status || 'NOT_STARTED').toLowerCase()"
              @click="showStudents(s)"
            >
              <div class="week-schedule-time">{{ s.startTime }} - {{ s.endTime }}</div>
              <div class="week-schedule-name">{{ s.courseName }}</div>
              <div class="week-schedule-info">{{ s.teacherName }} · {{ s.classroomName }}</div>
              <div class="week-schedule-att">
                签到 {{ s.attendedCount || 0 }}/{{ s.expectedCount || 0 }}
              </div>
            </div>
            <el-empty v-if="!d.schedules?.length" description="无排课" :image-size="40" />
          </div>
        </div>
      </div>

      <!-- ==================== 日视图 ==================== -->
      <div v-else class="day-view">
        <div
          v-for="d in data?.dates || []" :key="d.date"
          class="day-schedules"
        >
          <div
            v-for="s in d.schedules || []" :key="s.scheduleId"
            class="day-schedule"
            :class="'schedule-' + (s.status || 'NOT_STARTED').toLowerCase()"
            @click="showStudents(s)"
          >
            <div class="day-schedule-left">
              <div class="day-schedule-time">{{ s.startTime }} - {{ s.endTime }}</div>
              <div class="day-schedule-status">
                <el-tag
                  size="small"
                  :type="statusTagType(s.status)"
                >{{ statusLabel(s.status) }}</el-tag>
              </div>
            </div>
            <div class="day-schedule-body">
              <div class="day-schedule-title">{{ s.courseName }}</div>
              <div class="day-schedule-meta">
                <span>班级：{{ s.className }}</span>
                <span>教师：{{ s.teacherName }}</span>
                <span>教室：{{ s.classroomName }}</span>
              </div>
            </div>
            <div class="day-schedule-right">
              <div class="day-schedule-att">
                <span class="att-count">{{ s.attendedCount || 0 }}</span>
                <span class="att-sep">/</span>
                <span class="att-total">{{ s.expectedCount || 0 }}</span>
                <span class="att-label">签到</span>
              </div>
            </div>
          </div>
          <el-empty v-if="!d.schedules?.length" description="当日无排课" :image-size="100" />
        </div>
      </div>

    </div>

    <!-- 学生签到详情弹窗 -->
    <el-dialog v-model="studentVisible" :title="studentDialogTitle" width="520px" destroy-on-close>
      <div v-loading="studentLoading">
        <div class="student-summary">
          <span>应到 <strong>{{ studentData?.totalCount || 0 }}</strong> 人</span>
          <el-divider direction="vertical" />
          <span>已签到 <strong>{{ studentData?.attendedCount || 0 }}</strong> 人</span>
        </div>
        <el-table :data="studentData?.students || []" size="small" max-height="320" stripe>
          <el-table-column label="学生姓名" prop="studentName" />
          <el-table-column label="签到状态" align="center" width="100">
            <template #default="{ row }">
              <el-tag
                v-if="row.attendanceStatus === 'PRESENT'"
                type="success" size="small"
              >已签到</el-tag>
              <el-tag
                v-else-if="row.attendanceStatus === 'LATE'"
                type="warning" size="small"
              >迟到</el-tag>
              <el-tag
                v-else-if="row.attendanceStatus === 'ABSENT'"
                type="danger" size="small"
              >缺勤</el-tag>
              <span v-else class="text-muted">未签到</span>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ArrowLeft, ArrowRight } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import { getCalendar, getScheduleStudents } from '@/api/dashboard/index.js';

// ==================== 状态管理 ====================
const viewType = ref('MONTH');
const currentDate = ref(todayStr());
const data = ref(null);
const loading = ref(false);

// 学生签到弹窗
const studentVisible = ref(false);
const studentLoading = ref(false);
const studentData = ref(null);
const selectedSchedule = ref(null);

const studentDialogTitle = computed(() => {
  const s = selectedSchedule.value;
  if (!s) return '学生签到详情';
  return `${s.courseName || ''} — ${s.startTime || ''}-${s.endTime || ''}`;
});

// 星期表头
const weekdays = ['一', '二', '三', '四', '五', '六', '日'];

// ==================== 工具函数 ====================
function todayStr() {
  const d = new Date();
  return d.getFullYear() + '-' + String(d.getMonth() + 1).padStart(2, '0') + '-' + String(d.getDate()).padStart(2, '0');
}

function formatDayHeader(d) {
  const parts = (d.date || '').split('-');
  return parts[2] ? parseInt(parts[2], 10) + '日' : '--';
}

function statusTagType(status) {
  if (status === 'IN_PROGRESS') return '';
  if (status === 'FINISHED') return 'info';
  return 'primary';
}

function statusLabel(status) {
  if (status === 'IN_PROGRESS') return '进行中';
  if (status === 'FINISHED') return '已结束';
  return '未开始';
}

// ==================== 月视图网格计算 ====================
const monthCells = computed(() => {
  if (!data.value?.dates?.length || viewType.value !== 'MONTH') return [];
  const dates = data.value.dates;
  const firstDay = dates[0];
  const offset = (firstDay.dayOfWeek || 1) - 1; // Monday=0 offset
  const cells = [];
  for (let i = 0; i < offset; i++) {
    cells.push({ date: null, dayNum: null, isToday: false, isCurrentMonth: true, schedules: [] });
  }
  for (const d of dates) {
    const dayNum = parseInt((d.date || '').split('-')[2], 10) || 0;
    cells.push({ ...d, dayNum });
  }
  return cells;
});

// ==================== 数据加载 ====================
let calendarReqId = 0;

async function loadCalendar() {
  const reqId = ++calendarReqId;
  loading.value = true;
  try {
    const res = await getCalendar({ viewType: viewType.value, date: currentDate.value });
    if (reqId !== calendarReqId) return; // 丢弃过期响应
    if (res.success) {
      data.value = res.data;
    } else {
      ElMessage.error(res.msg || '加载日历失败');
    }
  } catch {
    if (reqId !== calendarReqId) return;
    ElMessage.error('加载日历失败，请检查网络');
  } finally {
    if (reqId === calendarReqId) loading.value = false;
  }
}

function onViewChange() {
  // 切换视图时重置日期为今天
  currentDate.value = todayStr();
  loadCalendar();
}

function goPrev() {
  const d = parseDate(currentDate.value);
  if (!d) return;
  if (viewType.value === 'MONTH') {
    d.setMonth(d.getMonth() - 1);
  } else if (viewType.value === 'WEEK') {
    d.setDate(d.getDate() - 7);
  } else {
    d.setDate(d.getDate() - 1);
  }
  currentDate.value = formatDate(d);
  loadCalendar();
}

function goNext() {
  const d = parseDate(currentDate.value);
  if (!d) return;
  if (viewType.value === 'MONTH') {
    d.setMonth(d.getMonth() + 1);
  } else if (viewType.value === 'WEEK') {
    d.setDate(d.getDate() + 7);
  } else {
    d.setDate(d.getDate() + 1);
  }
  currentDate.value = formatDate(d);
  loadCalendar();
}

function goToday() {
  currentDate.value = todayStr();
  loadCalendar();
}

function parseDate(str) {
  const parts = str.split('-');
  if (parts.length !== 3) return null;
  return new Date(parseInt(parts[0]), parseInt(parts[1]) - 1, parseInt(parts[2]));
}

function formatDate(d) {
  return d.getFullYear() + '-' + String(d.getMonth() + 1).padStart(2, '0') + '-' + String(d.getDate()).padStart(2, '0');
}

// ==================== 学生签到详情 ====================
let studentReqId = 0;

async function showStudents(schedule) {
  const reqId = ++studentReqId;
  selectedSchedule.value = schedule;
  studentVisible.value = true;
  studentLoading.value = true;
  studentData.value = null;
  try {
    const res = await getScheduleStudents(schedule.scheduleId);
    if (reqId !== studentReqId) return;
    if (res.success) {
      studentData.value = res.data;
    } else {
      ElMessage.error(res.msg || '加载签到详情失败');
    }
  } catch {
    if (reqId !== studentReqId) return;
    ElMessage.error('加载签到详情失败');
  } finally {
    if (reqId === studentReqId) studentLoading.value = false;
  }
}

function switchToDayView(dateStr) {
  currentDate.value = dateStr;
  viewType.value = 'DAY';
  loadCalendar();
}

// ==================== 暴露刷新方法 ====================
function refresh() {
  currentDate.value = todayStr();
  loadCalendar();
}

defineExpose({ refresh });

// ==================== 生命周期 ====================
onMounted(() => {
  loadCalendar();
});
</script>

<style scoped lang="less">
.calendar-panel {
  height: 100%;
}

// 头部
.calendar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  flex-wrap: wrap;
  gap: 8px;
}

.calendar-nav {
  display: flex;
  align-items: center;
  gap: 4px;
}

.calendar-label {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  min-width: 120px;
  text-align: center;
}

.calendar-body {
  min-height: 400px;
}

// ============ 月视图 ============
.weekday-header {
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
  min-height: 90px;
  border-right: 1px solid #ebeef5;
  border-bottom: 1px solid #ebeef5;
  padding: 4px;
  background: #fff;
  transition: background 0.15s;

  &.is-empty {
    background: #fafafa;
    cursor: default;
  }

  &.is-other-month {
    background: #fafafa;

    .cell-day { color: #c0c4cc; }
  }

  &.is-today {
    background: #ecf5ff;

    .cell-day {
      background: #409eff;
      color: #fff;
      border-radius: 50%;
      width: 22px;
      height: 22px;
      display: inline-flex;
      align-items: center;
      justify-content: center;
    }
  }
}

.cell-date {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2px;
  padding: 0 2px;
}

.cell-day {
  font-size: 13px;
  font-weight: 500;
  color: #303133;
}

.cell-weekday {
  font-size: 10px;
  color: #c0c4cc;
}

.cell-schedules {
  display: flex;
  flex-direction: column;
  gap: 1px;
}

.cell-schedule {
  font-size: 11px;
  padding: 1px 4px;
  border-radius: 3px;
  cursor: pointer;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  border-left: 3px solid transparent;

  &.schedule-not_started {
    background: #ecf5ff;
    border-left-color: #409eff;
    color: #409eff;
  }

  &.schedule-in_progress {
    background: #fdf6ec;
    border-left-color: #e6a23c;
    color: #e6a23c;
  }

  &.schedule-finished {
    background: #f0f9eb;
    border-left-color: #67c23a;
    color: #67c23a;
  }
}

.schedule-time {
  margin-right: 4px;
  font-weight: 500;
}

.cell-more {
  font-size: 11px;
  color: #909399;
  cursor: pointer;
  padding: 1px 4px;
  text-align: center;

  &:hover { color: #409eff; }
}

// ============ 周视图 ============
.week-view {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 8px;
}

.week-day {
  border: 1px solid #ebeef5;
  border-radius: 6px;
  overflow: hidden;

  &.is-today {
    border-color: #409eff;
    box-shadow: 0 0 0 1px #409eff;
  }
}

.week-day-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 8px;
  background: #f5f7fa;

  .is-today & { background: #ecf5ff; }
}

.week-date {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.week-label {
  font-size: 11px;
  color: #909399;
}

.week-schedules {
  padding: 6px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.week-schedule {
  padding: 6px 8px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 11px;
  border-left: 3px solid transparent;

  &.schedule-not_started {
    background: #ecf5ff;
    border-left-color: #409eff;
  }

  &.schedule-in_progress {
    background: #fdf6ec;
    border-left-color: #e6a23c;
  }

  &.schedule-finished {
    background: #f0f9eb;
    border-left-color: #67c23a;
  }
}

.week-schedule-time {
  font-weight: 500;
  color: #303133;
  margin-bottom: 1px;
}

.week-schedule-name {
  color: #606266;
  margin-bottom: 1px;
}

.week-schedule-info {
  color: #909399;
  font-size: 10px;
  margin-bottom: 2px;
}

.week-schedule-att {
  color: #409eff;
  font-size: 10px;
}

// ============ 日视图 ============
.day-schedules {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.day-schedule {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.15s;
  border: 1px solid #ebeef5;

  &.schedule-not_started {
    border-left: 4px solid #409eff;
    background: #fafafa;
  }

  &.schedule-in_progress {
    border-left: 4px solid #e6a23c;
    background: #fef9f0;
  }

  &.schedule-finished {
    border-left: 4px solid #67c23a;
    background: #f5faf5;
    opacity: 0.75;
  }

  &:hover { background: #f5f7fa; }
}

.day-schedule-left {
  width: 120px;
  flex-shrink: 0;
  text-align: center;
}

.day-schedule-time {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 6px;
}

.day-schedule-body {
  flex: 1;
  min-width: 0;
}

.day-schedule-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.day-schedule-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #909399;
}

.day-schedule-right {
  flex-shrink: 0;
  text-align: center;
}

.att-count {
  font-size: 22px;
  font-weight: 700;
  color: #409eff;
}

.att-sep {
  font-size: 14px;
  color: #c0c4cc;
  margin: 0 2px;
}

.att-total {
  font-size: 14px;
  color: #909399;
}

.att-label {
  display: block;
  font-size: 11px;
  color: #c0c4cc;
  margin-top: 2px;
}

// ============ 学生签到弹窗 ============
.student-summary {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
  font-size: 14px;
  color: #606266;

  strong { color: #409eff; font-size: 16px; }
}

.text-muted { color: #c0c4cc; }
</style>
