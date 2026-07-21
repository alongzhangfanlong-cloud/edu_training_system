<template>
  <view class="eval-list-page">
    <view class="sub-tabs">
      <text :class="{ active: statusFilter === '' }" @click="switchFilter('')">待评价</text>
      <text :class="{ active: statusFilter === '1' }" @click="switchFilter('1')">已评价</text>
    </view>

    <view v-if="loading"><loading-skeleton :rows="4" /></view>
    <view v-else-if="error"><empty-state icon="⚠️" text="加载失败" :show-retry="true" @retry="() => loadData(true)" /></view>
    <view v-else-if="!list.length"><empty-state icon="⭐" :text="statusFilter === '1' ? '暂无已评价记录' : '暂无待评价课程'" /></view>

    <view v-else class="eval-list">
      <view v-for="item in list" :key="item.id" class="eval-card" @click="goForm(item)">
        <view class="ec-header">
          <text class="ec-date">{{ item.evaluationDate || item.createTime || '' }}</text>
          <text class="ec-status" :style="{ color: item.status === 1 ? '#67c23a' : '#e6a23c' }">
            {{ item.status === 1 ? '已评' : item.status === 0 ? '草稿' : '待评' }}
          </text>
        </view>
        <text class="ec-course">{{ item.courseName || '--' }}</text>
        <text class="ec-class">{{ item.className || '--' }} · {{ item.studentCount || 0 }}人</text>
      </view>
    </view>

    <view v-if="hasMore && !loading" class="load-more" @click="loadData()">加载更多</view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { getEvaluationList } from '@/api/teacher-evaluation';
import EmptyState from '@/components/empty-state.vue';
import LoadingSkeleton from '@/components/loading-skeleton.vue';

const statusFilter = ref('');
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
    const res = await getEvaluationList({ page: page.value, pageSize: 10, status: statusFilter.value });
    if (res.code === 200) {
      const data = res.data?.records || res.data || [];
      if (isRefresh || page.value === 1) list.value = [];
      list.value.push(...data);
      hasMore.value = data.length >= 10;
      page.value++;
    }
  } catch { if (page.value === 1 || isRefresh) error.value = true; }
  finally { loading.value = false; }
}

function switchFilter(s) { statusFilter.value = s; loadData(true); }

function goForm(item) { uni.navigateTo({ url: `/pages/teacher/teaching/eval-form?id=${item.id}` }); }

onMounted(() => loadData(true));
</script>

<style scoped lang="scss">
.eval-list-page { padding-bottom: 120rpx; }
.sub-tabs { display: flex; padding: 16rpx 24rpx; background: #fff; gap: 32rpx; }
.sub-tabs text { font-size: 28rpx; color: #909399; padding: 8rpx 16rpx; }
.sub-tabs text.active { color: #409eff; font-weight: 600; background: #ecf5ff; border-radius: 8rpx; }

.eval-list { padding: 16rpx 24rpx; }
.eval-card { background: #fff; border-radius: 12rpx; padding: 24rpx; margin-bottom: 12rpx; }
.ec-header { display: flex; justify-content: space-between; margin-bottom: 8rpx; }
.ec-date { font-size: 24rpx; color: #909399; }
.ec-status { font-size: 24rpx; font-weight: 500; }
.ec-course { font-size: 30rpx; font-weight: 600; color: #303133; display: block; }
.ec-class { font-size: 24rpx; color: #909399; margin-top: 4rpx; display: block; }

.load-more { text-align: center; padding: 24rpx; color: #409eff; font-size: 26rpx; }
</style>
