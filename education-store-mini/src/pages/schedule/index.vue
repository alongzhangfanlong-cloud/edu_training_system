<template>
  <view class="sub-page">
    <child-switcher v-if="childrenStore.children.length > 1" :children="childrenStore.children" :current-id="currentChildId" @change="onChildChange" />

    <view class="date-range-bar">
      <picker mode="date" :value="startDate" @change="(e) => { startDate = e.detail.value; loadData(); }">
        <view class="dr-item"><text class="dr-label">开始</text><text class="dr-value">{{ startDate || '选择' }}</text></view>
      </picker>
      <text class="dr-sep">至</text>
      <picker mode="date" :value="endDate" @change="(e) => { endDate = e.detail.value; loadData(); }">
        <view class="dr-item"><text class="dr-label">结束</text><text class="dr-value">{{ endDate || '选择' }}</text></view>
      </picker>
    </view>

    <view v-if="loading"><loading-skeleton :rows="4" /></view>
    <view v-else-if="error"><empty-state icon="⚠️" text="加载失败" :show-retry="true" @retry="() => loadData()" /></view>
    <view v-else-if="!list.length"><empty-state icon="📅" text="当前日期范围暂无课程" /></view>
    <view v-else class="record-list">
      <view v-for="item in list" :key="item.id" class="sched-card">
        <view class="sched-top"><text class="sched-date">{{ item.scheduleDate || '--' }}</text><text class="sched-time">{{ item.startTime || '' }} - {{ item.endTime || '' }}</text></view>
        <text class="sched-course">{{ item.courseName || '--' }}</text>
        <view class="sched-meta"><text v-if="item.roomName">📍 {{ item.roomName }}</text></view>
      </view>
    </view>
  </view>
</template>
<script setup>
import { ref } from 'vue'; import { onLoad, onPullDownRefresh } from '@dcloudio/uni-app';
import { useChildrenStore } from '@/stores/children'; import { getSchedule } from '@/api/schedule';
import ChildSwitcher from '@/components/child-switcher.vue'; import EmptyState from '@/components/empty-state.vue'; import LoadingSkeleton from '@/components/loading-skeleton.vue';
const childrenStore = useChildrenStore(); const currentChildId = ref(null); const list = ref([]); const loading = ref(true); const error = ref(false);
const now = new Date(); const dayOfWeek = now.getDay() || 7;
const monday = new Date(now); monday.setDate(now.getDate() - dayOfWeek + 1);
const sunday = new Date(now); sunday.setDate(now.getDate() + (7 - dayOfWeek));
const fmt = d => d.getFullYear() + '-' + String(d.getMonth() + 1).padStart(2, '0') + '-' + String(d.getDate()).padStart(2, '0');
const startDate = ref(fmt(monday)); const endDate = ref(fmt(sunday));
onLoad((options) => { currentChildId.value = Number(options.studentId) || childrenStore.currentChildId; loadData(); });
async function loadData() { if (!currentChildId.value) return; loading.value = true; error.value = false; try { const res = await getSchedule(currentChildId.value, { startDate: startDate.value, endDate: endDate.value }); if (res.code === 200) list.value = res.data || []; } catch { error.value = true; } finally { loading.value = false; } }
function onChildChange(id) { currentChildId.value = id; loadData(); }
onPullDownRefresh(async () => { await loadData(); uni.stopPullDownRefresh(); });
</script>
<style scoped lang="scss">
.sub-page { min-height: 100vh; padding-bottom: 24rpx; }
.date-range-bar { display: flex; align-items: center; gap: 12rpx; padding: 24rpx; background: #fff; margin-bottom: 16rpx; }
.dr-item { flex: 1; } .dr-label { font-size: 22rpx; color: #909399; display: block; }
.dr-value { font-size: 28rpx; color: #409eff; font-weight: 500; } .dr-sep { color: #909399; font-size: 24rpx; }
.record-list { padding: 0 24rpx; }
.sched-card { background: #fff; border-radius: 12rpx; padding: 24rpx; margin-bottom: 12rpx; }
.sched-top { display: flex; justify-content: space-between; margin-bottom: 8rpx; }
.sched-date { font-size: 28rpx; color: #303133; font-weight: 500; } .sched-time { font-size: 26rpx; color: #409eff; font-weight: 500; }
.sched-course { font-size: 30rpx; font-weight: 600; color: #303133; display: block; margin-bottom: 8rpx; }
.sched-meta { font-size: 24rpx; color: #909399; }
</style>
