<template>
  <view class="sub-page">
    <child-switcher v-if="childrenStore.children.length > 1" :children="childrenStore.children" :current-id="currentChildId" @change="onChildChange" />
    <view v-if="loading"><loading-skeleton :rows="4" /></view>
    <view v-else-if="error"><empty-state icon="⚠️" text="加载失败" :show-retry="true" @retry="() => loadData()" /></view>
    <view v-else-if="!list.length"><empty-state icon="📋" text="暂无报名记录" /></view>
    <view v-else class="record-list">
      <view v-for="item in list" :key="item.id" class="enroll-card">
        <view class="enroll-header"><text class="enroll-course">{{ item.courseName || '--' }}</text><text class="enroll-status" :style="{ color: payColor(item.paymentStatus) }">{{ payText(item.paymentStatus) }}</text></view>
        <view class="enroll-meta"><text v-if="item.enrollmentDate">报名日期：{{ item.enrollmentDate }}</text><text v-if="item.amount != null" class="enroll-amount">¥{{ item.amount }}</text></view>
      </view>
    </view>
  </view>
</template>
<script setup>
import { ref } from 'vue'; import { onLoad, onPullDownRefresh } from '@dcloudio/uni-app';
import { useChildrenStore } from '@/stores/children'; import { getEnrollments } from '@/api/enrollment';
import ChildSwitcher from '@/components/child-switcher.vue'; import EmptyState from '@/components/empty-state.vue'; import LoadingSkeleton from '@/components/loading-skeleton.vue';
const childrenStore = useChildrenStore(); const currentChildId = ref(null); const list = ref([]); const loading = ref(true); const error = ref(false);
const payText = (s) => ({ PAID: '已缴费', UNPAID: '未缴费', REFUNDED: '已退费' }[s] || s || '--');
const payColor = (s) => ({ PAID: '#67c23a', UNPAID: '#e6a23c', REFUNDED: '#f56c6c' }[s] || '#909399');
onLoad((options) => { currentChildId.value = Number(options.studentId) || childrenStore.currentChildId; loadData(); });
async function loadData() { if (!currentChildId.value) return; loading.value = true; error.value = false; try { const res = await getEnrollments(currentChildId.value); if (res.code === 200) list.value = res.data || []; } catch { error.value = true; } finally { loading.value = false; } }
function onChildChange(id) { currentChildId.value = id; loadData(); }
onPullDownRefresh(async () => { await loadData(); uni.stopPullDownRefresh(); });
</script>
<style scoped lang="scss">
.sub-page { min-height: 100vh; padding-bottom: 24rpx; } .record-list { padding: 0 24rpx; }
.enroll-card { background: #fff; border-radius: 12rpx; padding: 24rpx; margin-bottom: 12rpx; }
.enroll-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8rpx; }
.enroll-course { font-size: 30rpx; font-weight: 600; color: #303133; } .enroll-status { font-size: 26rpx; font-weight: 500; }
.enroll-meta { display: flex; justify-content: space-between; font-size: 24rpx; color: #909399; margin-top: 8rpx; } .enroll-amount { font-size: 32rpx; font-weight: 600; color: #f56c6c; }
</style>
