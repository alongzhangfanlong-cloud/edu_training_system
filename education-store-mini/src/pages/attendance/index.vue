<template>
  <view class="sub-page">
    <child-switcher v-if="childrenStore.children.length > 1" :children="childrenStore.children" :current-id="currentChildId" @change="onChildChange" />

    <!-- Stats cards -->
    <view v-if="stats" class="stats-row">
      <view class="stat-card stat-present"><text class="stat-num">{{ stats.present }}</text><text class="stat-label">出勤</text></view>
      <view class="stat-card stat-late"><text class="stat-num">{{ stats.late }}</text><text class="stat-label">迟到</text></view>
      <view class="stat-card stat-absent"><text class="stat-num">{{ stats.absent }}</text><text class="stat-label">缺勤</text></view>
      <view class="stat-card stat-leave"><text class="stat-num">{{ stats.leave }}</text><text class="stat-label">请假</text></view>
    </view>

    <!-- Month picker -->
    <view class="month-bar">
      <text class="month-arrow" @click="prevMonth">‹</text>
      <text class="month-label">{{ yearMonth }}</text>
      <text class="month-arrow" @click="nextMonth">›</text>
    </view>

    <view v-if="loading"><loading-skeleton :rows="4" /></view>
    <view v-else-if="error"><empty-state icon="⚠️" text="加载失败" :show-retry="true" @retry="() => loadData(true)" /></view>
    <view v-else-if="!list.length"><empty-state icon="📋" text="本月暂无考勤记录" /></view>

    <view v-else class="record-list">
      <view v-for="item in list" :key="item.id" class="record-card">
        <view class="rc-left">
          <text class="rc-date">{{ item.attendanceDate || item.date || '--' }}</text>
          <text class="rc-time" v-if="item.signinTime">{{ item.signinTime }}</text>
        </view>
        <text class="rc-status" :style="{ color: statusColor(item.statusText || item.status) }">{{ item.statusText || statusMap[item.status] || item.status || '--' }}</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue';
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

function statusColor(s) { const m = { 出勤: '#67c23a', 迟到: '#e6a23c', 缺勤: '#f56c6c', 请假: '#409eff' }; return m[s] || '#909399'; }

onLoad((options) => { currentChildId.value = Number(options.studentId) || childrenStore.currentChildId; loadData(true); });

async function loadData(isRefresh) {
  if (isRefresh) { page.value = 1; hasMore.value = true; error.value = false; }
  if (!currentChildId.value) return;
  loading.value = true;
  try {
    const [attRes, statsRes] = await Promise.all([
      getAttendance(currentChildId.value, { page: page.value, pageSize: 20, yearMonth: yearMonth.value }),
      page.value === 1 ? getAttendanceStats(currentChildId.value) : Promise.resolve(null),
    ]);
    if (attRes.code === 200) { const data = attRes.data?.records || attRes.data || []; if (isRefresh) list.value = []; list.value.push(...data); hasMore.value = data.length >= 20; page.value++; }
    if (statsRes && statsRes.code === 200) stats.value = statsRes.data;
  } catch { if (isRefresh) error.value = true; }
  finally { loading.value = false; }
}

function prevMonth() { const [y, m] = yearMonth.value.split('-').map(Number); const d = new Date(y, m - 2, 1); yearMonth.value = d.getFullYear() + '-' + String(d.getMonth() + 1).padStart(2, '0'); loadData(true); }
function nextMonth() { const [y, m] = yearMonth.value.split('-').map(Number); const d = new Date(y, m, 1); yearMonth.value = d.getFullYear() + '-' + String(d.getMonth() + 1).padStart(2, '0'); loadData(true); }
function onChildChange(id) { currentChildId.value = id; loadData(true); }

onPullDownRefresh(async () => { await loadData(true); uni.stopPullDownRefresh(); });
onReachBottom(() => { if (!loading.value && hasMore.value) loadData(); });
</script>

<style scoped lang="scss">
.sub-page { min-height: 100vh; padding-bottom: 24rpx; }
.stats-row { display: grid; grid-template-columns: repeat(4, 1fr); gap: 12rpx; padding: 24rpx; }
.stat-card { display: flex; flex-direction: column; align-items: center; padding: 24rpx 8rpx; border-radius: 12rpx; background: #fff; }
.stat-num { font-size: 40rpx; font-weight: 700; }
.stat-label { font-size: 22rpx; color: #909399; margin-top: 4rpx; }
.stat-present .stat-num { color: #67c23a; } .stat-late .stat-num { color: #e6a23c; } .stat-absent .stat-num { color: #f56c6c; } .stat-leave .stat-num { color: #409eff; }
.month-bar { display: flex; justify-content: center; align-items: center; padding: 16rpx 0; background: #fff; margin-bottom: 16rpx; }
.month-label { font-size: 32rpx; font-weight: 600; color: #303133; margin: 0 32rpx; }
.month-arrow { font-size: 48rpx; color: #409eff; padding: 8rpx 24rpx; }
.record-list { padding: 0 24rpx; }
.record-card { display: flex; justify-content: space-between; align-items: center; padding: 24rpx; background: #fff; border-radius: 12rpx; margin-bottom: 12rpx; }
.rc-date { font-size: 28rpx; color: #303133; display: block; }
.rc-time { font-size: 24rpx; color: #909399; margin-top: 4rpx; display: block; }
.rc-status { font-size: 28rpx; font-weight: 500; }
</style>
