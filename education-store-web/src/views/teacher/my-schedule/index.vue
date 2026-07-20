<!-- 教师端 — 我的课表（只读周/月视图） -->
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
          <el-select v-model="filterClassId" placeholder="班级筛选" clearable size="small" style="width: 140px" @change="loadData">
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
              :style="{ borderTopColor: subjectColor(s.subject || s.courseName) }"
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
            :class="{
              'is-today': cell.isToday,
              'is-other': !cell.isCurrentMonth,
              'is-empty': !cell.date,
            }"
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

    <!-- 排课详情弹窗（只读） -->
    <el-dialog v-model="detailVisible" title="排课详情" width="480px" destroy-on-close>
      <div v-if="detailData">
        <el-descriptions :column="1" border size="small">
          <el-descriptions-item label="课程">{{ detailData.courseName }}</el-descriptions-item>
          <el-descriptions-item label="班级">{{ detailData.className }}</el-descriptions-item>
          <el-descriptions-item label="时间">{{ detailData.startTime }} - {{ detailData.endTime }}</el-descriptions-item>
          <el-descriptions-item label="教室">{{ detailData.classroomName }}</el-descriptions-item>
          <el-descriptions-item v-if="detailData.teacherName" label="教师">{{ detailData.teacherName }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag
              v-if="detailData.status === 'CANCELLED'"
              type="danger" size="small"
            >已取消</el-tag>
            <el-tag
              v-else-if="detailData.status === 'RESCHEDULED'"
              type="warning" size="small"
            >已调课</el-tag>
            <el-tag
              v-else
              type="success" size="small"
            >正常</el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>

    <!-- 月视图日抽屉 -->
    <el-drawer v-model="drawerVisible" title="当日排课" size="480px">
      <div v-for="s in drawerSchedules" :key="s.scheduleId" class="drawer-schedule" @click="showDetail(s); drawerVisible = false">
        <div class="ds-time">{{ s.startTime }} - {{ s.endTime }}</div>
        <div class="ds-course">{{ s.courseName }}</div>
        <div class="ds-meta">{{ s.className }} · {{ s.classroomName }}</div>
      </div>
      <el-empty v-if="!drawerSchedules.length" description="当日无排课" />
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ArrowLeft, ArrowRight } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
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

function subjectColor(name) {
  const map = { '数学': '#409EFF', '语文': '#67C23A', '英语': '#E6A23C', '物理': '#9B59B6' };
  return map[name] || '#909399';
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
  } catch {
    ElMessage.error('课表数据加载失败，请稍后重试');
  } finally {
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
  } catch { /* 班级列表加载失败不影响课表主功能 */ }
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

.toolbar-center {
  display: flex;
  align-items: center;
  gap: 4px;
}

.period-label {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  min-width: 200px;
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

.ws-time { font-weight: 500; color: #303133; margin-bottom: 1px; }
.ws-course { color: #409eff; margin-bottom: 1px; }
.ws-class { color: #606266; }
.ws-room { color: #909399; font-size: 10px; }

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
