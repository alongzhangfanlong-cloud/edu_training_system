<template>
  <view class="sub-page">
    <child-switcher v-if="childrenStore.children.length > 1" :children="childrenStore.children" :current-id="currentChildId" @change="onChildChange" />
    <view v-if="loading"><loading-skeleton :rows="4" /></view>
    <view v-else-if="error"><empty-state icon="⚠️" text="加载失败" :show-retry="true" @retry="() => loadData()" /></view>
    <view v-else-if="!list.length"><empty-state icon="⭐" text="暂无课堂评价" /></view>
    <view v-else class="record-list">
      <view v-for="item in list" :key="item.evaluationId || item.id" class="eval-card">
        <view class="eval-stars"><text v-for="i in 5" :key="i">{{ i <= (item.starRating || 0) ? '⭐' : '☆' }}</text><text class="eval-time">{{ item.createTime || '' }}</text></view>
        <text v-if="item.summary" class="eval-summary">{{ item.summary }}</text>
        <text v-if="item.comment" class="eval-comment">{{ item.comment }}</text>
      </view>
    </view>
  </view>
</template>
<script setup>
import { ref } from 'vue'; import { onLoad, onPullDownRefresh } from '@dcloudio/uni-app';
import { useChildrenStore } from '@/stores/children'; import { getEvaluations } from '@/api/evaluation';
import ChildSwitcher from '@/components/child-switcher.vue'; import EmptyState from '@/components/empty-state.vue'; import LoadingSkeleton from '@/components/loading-skeleton.vue';
const childrenStore = useChildrenStore(); const currentChildId = ref(null); const list = ref([]); const loading = ref(true); const error = ref(false);
onLoad((options) => { currentChildId.value = Number(options.studentId) || childrenStore.currentChildId; loadData(); });
async function loadData() { if (!currentChildId.value) return; loading.value = true; error.value = false; try { const res = await getEvaluations(currentChildId.value); if (res.code === 200) list.value = res.data || []; } catch { error.value = true; } finally { loading.value = false; } }
function onChildChange(id) { currentChildId.value = id; loadData(); }
onPullDownRefresh(async () => { await loadData(); uni.stopPullDownRefresh(); });
</script>
<style scoped lang="scss">
.sub-page { min-height: 100vh; padding-bottom: 24rpx; } .record-list { padding: 0 24rpx; }
.eval-card { background: #fff; border-radius: 12rpx; padding: 24rpx; margin-bottom: 12rpx; }
.eval-stars { display: flex; align-items: center; gap: 4rpx; margin-bottom: 8rpx; font-size: 28rpx; } .eval-time { font-size: 22rpx; color: #c0c4cc; margin-left: auto; }
.eval-summary { font-size: 28rpx; color: #303133; font-weight: 500; display: block; margin-bottom: 8rpx; }
.eval-comment { font-size: 26rpx; color: #606266; line-height: 1.5; display: block; }
</style>
