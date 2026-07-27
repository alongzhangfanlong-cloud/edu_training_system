<template>
  <view class="admin-students-page">
    <view class="search-bar">
      <input v-model="keyword" class="search-input" placeholder="搜索学生姓名/手机号" confirm-type="search" @confirm="loadData(true)" />
    </view>

    <view v-if="loading"><loading-skeleton :rows="4" /></view>
    <view v-else-if="error"><empty-state icon="⚠️" text="加载失败" :show-retry="true" @retry="() => loadData(true)" /></view>
    <view v-else-if="!list.length"><empty-state icon="👥" text="暂无学生数据" /></view>

    <view v-else class="student-list">
      <view v-for="item in list" :key="item.id" class="student-card">
        <view class="sc-avatar">👤</view>
        <view class="sc-info">
          <text class="sc-name">{{ item.name }}</text>
          <text class="sc-meta">{{ item.grade || '--' }} · {{ item.mobile || '--' }}</text>
          <text v-if="item.remainHours !== undefined" class="sc-hours">剩余课时：{{ item.remainHours ?? item.remainingHours ?? 0 }}</text>
        </view>
        <text class="sc-status" :class="statusClass(item.status)">{{ statusLabel(item.status) }}</text>
      </view>
    </view>

    <view v-if="hasMore && !loading" class="load-more" @click="loadData()">加载更多</view>
  </view>
</template>

<script setup>
import { ref } from 'vue';
import { onShow, onPullDownRefresh } from '@dcloudio/uni-app';
import { getStudentList } from '@/api/admin';
import EmptyState from '@/components/empty-state.vue';
import LoadingSkeleton from '@/components/loading-skeleton.vue';

const keyword = ref('');
const list = ref([]);
const loading = ref(true);
const error = ref(false);
const page = ref(1);
const hasMore = ref(true);

async function loadData(isRefresh) {
  if (isRefresh) { page.value = 1; hasMore.value = true; error.value = false; }
  if (!hasMore.value && !isRefresh) return;
  loading.value = true;
  try {
    const res = await getStudentList({ page: page.value, pageSize: 10, keyword: keyword.value || undefined });
    if (res.code === 200) {
      const data = res.data?.records || res.data || [];
      if (isRefresh || page.value === 1) list.value = [];
      list.value.push(...data);
      hasMore.value = data.length >= 10;
      page.value++;
    }
  } catch { if (page.value === 1 || isRefresh) error.value = true; }
  finally {
    loading.value = false;
    uni.stopPullDownRefresh();
  }
}

function statusLabel(s) {
  const map = { STUDYING: '在读', GRADUATED: '已结业', SUSPENDED: '休学', LOST: '流失' };
  return map[s] || s || '在读';
}
function statusClass(s) {
  if (s === 'STUDYING' || !s) return 'status-studying';
  if (s === 'GRADUATED') return 'status-graduated';
  if (s === 'SUSPENDED') return 'status-suspended';
  return 'status-lost';
}

onShow(() => { loadData(true); });
onPullDownRefresh(() => { loadData(true); });
</script>

<style scoped lang="scss">
.admin-students-page { min-height: 100vh; padding-bottom: 120rpx; }
.search-bar { padding: 16rpx 24rpx; background: #fff; }
.search-input { width: 100%; height: 72rpx; padding: 0 24rpx; background: #f5f7fa; border-radius: 36rpx; font-size: 28rpx; box-sizing: border-box; }
.student-list { padding: 16rpx 24rpx; }
.student-card { display: flex; align-items: center; background: #fff; border-radius: 12rpx; padding: 24rpx; margin-bottom: 12rpx; }
.sc-avatar { width: 80rpx; height: 80rpx; border-radius: 50%; background: #ecf5ff; display: flex; align-items: center; justify-content: center; font-size: 36rpx; margin-right: 20rpx; }
.sc-info { flex: 1; }
.sc-name { font-size: 30rpx; font-weight: 600; color: #303133; display: block; }
.sc-meta { font-size: 24rpx; color: #909399; display: block; margin-top: 4rpx; }
.sc-hours { font-size: 24rpx; color: #606266; display: block; margin-top: 4rpx; }
.sc-status { font-size: 22rpx; padding: 6rpx 16rpx; border-radius: 16rpx; }
.status-studying { background: #f0f9eb; color: #67c23a; }
.status-graduated { background: #f4f4f5; color: #909399; }
.status-suspended { background: #fdf6ec; color: #e6a23c; }
.status-lost { background: #fef0f0; color: #f56c6c; }
.load-more { text-align: center; padding: 24rpx; color: #409eff; font-size: 26rpx; }
</style>
