<template>
  <view class="sub-page">
    <child-switcher v-if="childrenStore.children.length > 1" :children="childrenStore.children" :current-id="currentChildId" @change="onChildChange" />
    <view v-if="loading"><loading-skeleton :rows="4" /></view>
    <view v-else-if="error"><empty-state icon="⚠️" text="加载失败" :show-retry="true" @retry="() => loadData()" /></view>
    <view v-else-if="!list.length"><empty-state icon="📊" text="暂无考试成绩" /></view>
    <view v-else class="record-list">
      <view v-for="item in list" :key="item.id || item.examId" class="exam-card">
        <view class="exam-header"><text class="exam-name">{{ item.examName || item.name || '--' }}</text><text class="exam-score">{{ item.totalScore != null ? item.totalScore + '分' : (item.score != null ? item.score + '分' : '--') }}</text></view>
        <view class="exam-meta"><text>{{ item.examType || item.type || '--' }}</text><text>{{ item.examDate || item.date || '' }}</text></view>
      </view>
    </view>
  </view>
</template>
<script setup>
import { ref } from 'vue';
import { onLoad, onShow, onPullDownRefresh } from '@dcloudio/uni-app';
import { useChildrenStore } from '@/stores/children';
import { getExamScores } from '@/api/exam';
import ChildSwitcher from '@/components/child-switcher.vue';
import EmptyState from '@/components/empty-state.vue';
import LoadingSkeleton from '@/components/loading-skeleton.vue';

const childrenStore = useChildrenStore();
const currentChildId = ref(null);
const list = ref([]);
const loading = ref(true);
const error = ref(false);

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
      loadData();
    });
  } else {
    loadData();
  }
});

async function loadData() {
  if (!currentChildId.value) return;
  loading.value = true;
  error.value = false;
  try {
    const res = await getExamScores(currentChildId.value);
    if (res.code === 200) {
      const data = res.data;
      list.value = Array.isArray(data) ? data : (data.records || data.list || data || []);
    }
  } catch {
    error.value = true;
  } finally {
    loading.value = false;
  }
}

function onChildChange(id) {
  currentChildId.value = id;
  loadData();
}

onPullDownRefresh(async () => {
  await loadData();
  uni.stopPullDownRefresh();
});
</script>
<style scoped lang="scss">
.sub-page { min-height: 100vh; padding-bottom: 24rpx; } .record-list { padding: 0 24rpx; }
.exam-card { background: #fff; border-radius: 12rpx; padding: 24rpx; margin-bottom: 12rpx; }
.exam-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8rpx; }
.exam-name { font-size: 30rpx; font-weight: 600; color: #303133; } .exam-score { font-size: 36rpx; font-weight: 700; color: #409eff; }
.exam-meta { display: flex; justify-content: space-between; font-size: 24rpx; color: #909399; }
</style>
