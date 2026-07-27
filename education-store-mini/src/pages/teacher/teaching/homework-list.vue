<template>
  <view class="hw-list-page">
    <view v-if="loading"><loading-skeleton :rows="4" /></view>
    <view v-else-if="error"><empty-state icon="⚠️" text="加载失败" :show-retry="true" @retry="() => loadData(true)" /></view>
    <view v-else-if="!list.length"><empty-state icon="📝" text="暂无课后作业" /></view>

    <view v-else class="hw-list">
      <view v-for="item in list" :key="item.id" class="hw-card">
        <view class="hwc-header">
          <text class="hwc-title">{{ item.title || (item.content || '').substring(0, 30) || '无标题' }}</text>
          <text class="hwc-time">{{ item.createTime || '' }}</text>
        </view>
        <text class="hwc-content">{{ (item.content || '').substring(0, 80) }}</text>
        <view class="hwc-meta">
          <text>{{ item.className || '' }}</text>
          <text class="hwc-delete" @click.stop="handleDelete(item.id)">删除</text>
        </view>
      </view>
    </view>

    <view v-if="hasMore && !loading" class="load-more" @click="loadData()">加载更多</view>

    <view class="fab-wrap">
      <button class="fab-btn" @click="goCreate">+ 布置作业</button>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import { getHomeworkList, deleteHomework } from '@/api/teacher-evaluation';
import { useAuthStore } from '@/stores/auth';
import EmptyState from '@/components/empty-state.vue';
import LoadingSkeleton from '@/components/loading-skeleton.vue';

const authStore = useAuthStore();
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
    const res = await getHomeworkList({ page: page.value, pageSize: 10, teacherId: authStore.userInfo?.userId });
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

function goCreate() { uni.navigateTo({ url: '/pages/teacher/teaching/homework-form' }); }

async function handleDelete(id) {
  const confirmed = await new Promise(r => uni.showModal({ title: '删除确认', content: '确定删除该作业？', success: res => r(res.confirm) }));
  if (!confirmed) return;
  try {
    const res = await deleteHomework(id, authStore.userInfo?.userId);
    if (res.code === 200) { uni.showToast({ title: '已删除', icon: 'success' }); loadData(true); }
    else { uni.showToast({ title: res.msg || '删除失败', icon: 'none' }); }
  } catch { uni.showToast({ title: '删除失败', icon: 'none' }); }
}

onShow(() => { loadData(true); });
</script>

<style scoped lang="scss">
.hw-list-page { min-height: 100vh; padding-bottom: 140rpx; }
.hw-list { padding: 16rpx 24rpx; }
.hw-card { background: #fff; border-radius: 12rpx; padding: 24rpx; margin-bottom: 12rpx; }
.hwc-header { display: flex; justify-content: space-between; margin-bottom: 8rpx; }
.hwc-title { font-size: 30rpx; font-weight: 600; color: #303133; flex: 1; }
.hwc-time { font-size: 22rpx; color: #c0c4cc; }
.hwc-content { font-size: 26rpx; color: #606266; line-height: 1.5; display: block; margin-bottom: 8rpx; }
.hwc-meta { display: flex; justify-content: space-between; font-size: 24rpx; color: #909399; }
.hwc-delete { color: #f56c6c; padding: 4rpx 8rpx; }
.load-more { text-align: center; padding: 24rpx; color: #409eff; font-size: 26rpx; }

.fab-wrap { position: fixed; bottom: 120rpx; left: 0; right: 0; display: flex; justify-content: center; }
.fab-btn { background: #409eff; color: #fff; border-radius: 44rpx; padding: 20rpx 48rpx; font-size: 30rpx; border: none; box-shadow: 0 8rpx 24rpx rgba(64,158,255,.3); }
.fab-btn::after { border: none; }
</style>
