<template>
  <view class="sub-page">
    <child-switcher v-if="childrenStore.children.length > 1" :children="childrenStore.children" :current-id="currentChildId" @change="onChildChange" />
    <view v-if="loading"><loading-skeleton :rows="4" /></view>
    <view v-else-if="error"><empty-state icon="⚠️" text="加载失败" :show-retry="true" @retry="() => loadData(true)" /></view>
    <view v-else-if="!list.length"><empty-state icon="📝" text="暂无课后作业" /></view>
    <view v-else class="record-list">
      <view v-for="item in list" :key="item.id" class="hw-card">
        <view class="hw-header"><text class="hw-title">{{ item.title || '无标题' }}</text><text class="hw-time">{{ item.createTime || '' }}</text></view>
        <text class="hw-content">{{ item.content || '' }}</text>
        <view v-if="item.teacherName" class="hw-teacher"><text>布置老师：{{ item.teacherName }}</text></view>
      </view>
    </view>
  </view>
</template>
<script setup>
import { ref } from 'vue'; import { onLoad, onReachBottom, onPullDownRefresh } from '@dcloudio/uni-app';
import { useChildrenStore } from '@/stores/children'; import { getHomework } from '@/api/homework';
import ChildSwitcher from '@/components/child-switcher.vue'; import EmptyState from '@/components/empty-state.vue'; import LoadingSkeleton from '@/components/loading-skeleton.vue';
const childrenStore = useChildrenStore(); const currentChildId = ref(null); const list = ref([]); const loading = ref(true); const error = ref(false); const page = ref(1); const hasMore = ref(true);
onLoad((options) => { currentChildId.value = Number(options.studentId) || childrenStore.currentChildId; loadData(true); });
async function loadData(isRefresh) {
  if (isRefresh) { page.value = 1; hasMore.value = true; error.value = false; } if (!currentChildId.value) return; loading.value = true;
  try { const res = await getHomework(currentChildId.value, { page: page.value, pageSize: 10 });
    if (res.code === 200) { const data = res.data?.records || res.data || []; if (isRefresh) list.value = []; list.value.push(...data); hasMore.value = data.length >= 10; page.value++; }
  } catch { if (isRefresh) error.value = true; } finally { loading.value = false; }
}
function onChildChange(id) { currentChildId.value = id; loadData(true); }
onPullDownRefresh(async () => { await loadData(true); uni.stopPullDownRefresh(); });
onReachBottom(() => { if (!loading.value && hasMore.value) loadData(); });
</script>
<style scoped lang="scss">
.sub-page { min-height: 100vh; padding-bottom: 24rpx; } .record-list { padding: 0 24rpx; }
.hw-card { background: #fff; border-radius: 12rpx; padding: 24rpx; margin-bottom: 12rpx; }
.hw-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12rpx; }
.hw-title { font-size: 30rpx; font-weight: 600; color: #303133; flex: 1; } .hw-time { font-size: 22rpx; color: #c0c4cc; }
.hw-content { font-size: 28rpx; color: #606266; line-height: 1.6; } .hw-teacher { margin-top: 12rpx; font-size: 24rpx; color: #909399; }
</style>
