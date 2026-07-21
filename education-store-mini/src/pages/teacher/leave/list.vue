<template>
  <view class="leave-page">
    <view v-if="loading"><loading-skeleton :rows="4" /></view>
    <view v-else-if="error"><empty-state icon="⚠️" text="加载失败" :show-retry="true" @retry="loadData" /></view>
    <view v-else-if="!list.length"><empty-state icon="📋" text="暂无请假记录" /></view>

    <view v-else class="record-list">
      <view v-for="item in list" :key="item.id" class="leave-card">
        <view class="lc-header">
          <text class="lc-date">{{ item.leaveDate || item.startDate || '--' }}</text>
          <text class="lc-status" :style="{ color: statusColor(item.status) }">{{ statusText(item.status) }}</text>
        </view>
        <text class="lc-type">{{ typeText(item.leaveType) }}</text>
        <text v-if="item.reason" class="lc-reason">{{ item.reason }}</text>
        <text v-if="item.rejectReason" class="lc-reject">驳回原因：{{ item.rejectReason }}</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { getLeaveList } from '@/api/teacher-leave';
import EmptyState from '@/components/empty-state.vue';
import LoadingSkeleton from '@/components/loading-skeleton.vue';

const list = ref([]);
const loading = ref(true);
const error = ref(false);

const typeText = (t) => ({ 1: '事假', 2: '病假', 3: '调休', 4: '其他' }[t] || '--');
const statusText = (s) => ({ 0: '待审批', 1: '已通过', 2: '已拒绝' }[s] || '--');
const statusColor = (s) => ({ 0: '#e6a23c', 1: '#67c23a', 2: '#f56c6c' }[s] || '#909399');

async function loadData() {
  loading.value = true; error.value = false;
  try {
    const res = await getLeaveList({ page: 1, pageSize: 50 });
    if (res.code === 200) list.value = res.data?.records || res.data || [];
  } catch { error.value = true; }
  finally { loading.value = false; }
}

onMounted(() => loadData());
</script>

<style scoped lang="scss">
.leave-page { padding: 24rpx; }
.leave-card { background: #fff; border-radius: 12rpx; padding: 24rpx; margin-bottom: 12rpx; }
.lc-header { display: flex; justify-content: space-between; margin-bottom: 8rpx; }
.lc-date { font-size: 28rpx; font-weight: 600; color: #303133; }
.lc-status { font-size: 24rpx; font-weight: 500; }
.lc-type { font-size: 24rpx; color: #606266; display: block; }
.lc-reason { font-size: 24rpx; color: #909399; margin-top: 4rpx; display: block; }
.lc-reject { font-size: 24rpx; color: #f56c6c; margin-top: 4rpx; display: block; }
</style>
