<template>
  <view class="sub-page">
    <child-switcher v-if="childrenStore.children.length > 1" :children="childrenStore.children" :current-id="currentChildId" @change="onChildChange" />
    <view v-if="loading"><loading-skeleton :rows="3" /></view>
    <view v-else-if="error"><empty-state icon="⚠️" text="加载失败" :show-retry="true" @retry="() => loadData()" /></view>
    <view v-else-if="!list.length"><empty-state icon="📋" text="暂无请假记录" /></view>
    <view v-else class="record-list">
      <view v-for="item in list" :key="item.id" class="leave-card">
        <view class="leave-header"><text class="leave-date">{{ item.leaveDate || '--' }}</text><text class="leave-status" :style="{ color: leaveStatusColor(item.status) }">{{ leaveStatusText(item.status) }}</text></view>
        <text class="leave-type">{{ leaveTypeText(item.leaveType) }}</text>
        <text v-if="item.reason" class="leave-reason">{{ item.reason }}</text>
        <text v-if="item.rejectReason" class="leave-reject">驳回原因：{{ item.rejectReason }}</text>
      </view>
    </view>
    <view class="fab-wrap"><button class="fab-btn" @click="goSubmit">+ 发起请假</button></view>
  </view>
</template>
<script setup>
import { ref } from 'vue'; import { onShow, onPullDownRefresh } from '@dcloudio/uni-app';
import { useChildrenStore } from '@/stores/children'; import { getLeaves } from '@/api/leave';
import ChildSwitcher from '@/components/child-switcher.vue'; import EmptyState from '@/components/empty-state.vue'; import LoadingSkeleton from '@/components/loading-skeleton.vue';
const childrenStore = useChildrenStore(); const currentChildId = ref(null); const list = ref([]); const loading = ref(true); const error = ref(false);
const leaveStatusText = (s) => ({ 0: '待审批', 1: '已通过', 2: '已拒绝' }[s] || '--');
const leaveStatusColor = (s) => ({ 0: '#e6a23c', 1: '#67c23a', 2: '#f56c6c' }[s] || '#909399');
const leaveTypeText = (t) => ({ 1: '事假', 2: '病假', 3: '其他' }[t] || '--');
onShow(() => { currentChildId.value = childrenStore.currentChildId; loadData(); });
async function loadData() { if (!currentChildId.value) return; loading.value = true; error.value = false; try { const res = await getLeaves(currentChildId.value); if (res.code === 200) list.value = res.data || []; } catch { error.value = true; } finally { loading.value = false; } }
function onChildChange(id) { currentChildId.value = id; loadData(); }
function goSubmit() { uni.navigateTo({ url: `/pages/leave/submit?studentId=${currentChildId.value}` }); }
onPullDownRefresh(async () => { await loadData(); uni.stopPullDownRefresh(); });
</script>
<style scoped lang="scss">
.sub-page { min-height: 100vh; padding-bottom: 120rpx; } .record-list { padding: 0 24rpx; }
.leave-card { background: #fff; border-radius: 12rpx; padding: 24rpx; margin-bottom: 12rpx; }
.leave-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8rpx; }
.leave-date { font-size: 30rpx; font-weight: 600; color: #303133; } .leave-status { font-size: 26rpx; font-weight: 500; }
.leave-type { font-size: 26rpx; color: #606266; display: block; }
.leave-reason { font-size: 26rpx; color: #606266; margin-top: 8rpx; display: block; }
.leave-reject { font-size: 24rpx; color: #f56c6c; margin-top: 8rpx; display: block; }
.fab-wrap { position: fixed; bottom: 48rpx; left: 0; right: 0; display: flex; justify-content: center; }
.fab-btn { background: #409eff; color: #fff; border-radius: 44rpx; padding: 20rpx 48rpx; font-size: 30rpx; border: none; box-shadow: 0 8rpx 24rpx rgba(64,158,255,0.3); } .fab-btn::after { border: none; }
</style>
