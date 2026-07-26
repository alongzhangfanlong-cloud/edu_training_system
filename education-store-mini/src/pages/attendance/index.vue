<template>
  <view class="sub-page">
    <child-switcher v-if="childrenStore.children.length > 1" :children="childrenStore.children" :current-id="currentChildId" @change="onChildChange" />

    <view v-if="stats" class="stats-row">
      <view class="stat-card stat-present"><text class="stat-num">{{ stats.present ?? stats.PRESENT ?? 0 }}</text><text class="stat-label">出勤</text></view>
      <view class="stat-card stat-late"><text class="stat-num">{{ stats.late ?? stats.LATE ?? 0 }}</text><text class="stat-label">迟到</text></view>
      <view class="stat-card stat-absent"><text class="stat-num">{{ stats.absent ?? stats.ABSENT ?? 0 }}</text><text class="stat-label">缺勤</text></view>
      <view class="stat-card stat-leave"><text class="stat-num">{{ stats.leave ?? stats.LEAVE ?? 0 }}</text><text class="stat-label">请假</text></view>
    </view>

    <view class="month-bar">
      <text class="month-arrow" @click="prevMonth">‹</text>
      <text class="month-label">{{ yearMonth }}</text>
      <text class="month-arrow" @click="nextMonth">›</text>
    </view>

    <view v-if="loading && list.length === 0"><loading-skeleton :rows="4" /></view>
    <view v-else-if="error && list.length === 0"><empty-state icon="⚠️" text="加载失败" :show-retry="true" @retry="() => loadData(true)" /></view>
    <view v-else-if="!list.length"><empty-state icon="📋" text="本月暂无考勤记录" /></view>

    <view v-else class="record-list">
      <view v-for="item in list" :key="item.id || item.attendanceId" class="record-card">
        <view class="rc-left">
          <text class="rc-date">{{ item.attendanceDate || item.date || '--' }}</text>
          <text class="rc-time" v-if="item.signinTime || item.checkinTime">{{ item.signinTime || item.checkinTime }}</text>
        </view>
        <text class="rc-status" :style="{ color: statusColor(item.statusText || item.status) }">{{ item.statusText || statusMap[item.status] || item.status || '--' }}</text>
      </view>
      <view v-if="loading" class="loading-more">加载中...</view>
      <view v-else-if="!hasMore && list.length > 0" class="no-more">没有更多了</view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue';
import { onLoad, onShow, onReachBottom, onPullDownRefresh } from '@dcloudio/uni-app';
import { useChildrenStore } from '@/stores/children';
import { getAttendance, getAttendanceStats } from '@/api/attendance';
import { STATUS_MAP, normalizePaginationParams, parsePaginationResponse } from '@/utils/index';
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
const pageSize = ref(20);
const hasMore = ref(true);
const now = new Date();
const yearMonth = ref(now.getFullYear() + '-' + String(now.getMonth() + 1).padStart(2, '0'));

function statusColor(s) { const m = { 出勤: '#67c23a', 迟到: '#e6a23c', 缺勤: '#f56c6c', 请假: '#409eff', PRESENT: '#67c23a', LATE: '#e6a23c', ABSENT: '#f56c6c', LEAVE: '#409eff' }; return m[s] || '#909399'; }

onLoad((options) => {
  if (options?.studentId) {
    currentChildId.value = Number(options.studentId);
  }
});

onShow(() => {
  if (!currentChildId.value) {
    currentChildId.value = childrenStore.currentChildId;
  }
  if (childrenStore.children.length === 0) {
    childrenStore.fetchChildren().then(() => {
      if (!currentChildId.value) {
        currentChildId.value = childrenStore.currentChildId;
      }
      loadData(true);
    });
  } else {
    loadData(true);
  }
});

async function loadData(isRefresh) {
  if (isRefresh) {
    page.value = 1;
    hasMore.value = true;
    error.value = false;
  }
  if (!currentChildId.value || !hasMore.value) return;
  loading.value = true;
  try {
    const params = normalizePaginationParams({ page: page.value, pageSize: pageSize.value, yearMonth: yearMonth.value });
    const [attRes, statsRes] = await Promise.all([
      getAttendance(currentChildId.value, params),
      page.value === 1 ? getAttendanceStats(currentChildId.value) : Promise.resolve(null),
    ]);
    if (attRes.code === 200) {
      const { list: newList } = parsePaginationResponse(attRes);
      if (isRefresh) list.value = [];
      list.value.push(...newList);
      hasMore.value = newList.length >= pageSize.value;
      if (hasMore.value) page.value++;
    }
    if (statsRes && statsRes.code === 200) stats.value = statsRes.data;
  } catch {
    if (isRefresh) error.value = true;
  } finally {
    loading.value = false;
  }
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
.loading-more, .no-more { text-align: center; padding: 24rpx; font-size: 24rpx; color: #909399; }
</style>
