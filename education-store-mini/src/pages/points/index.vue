<template>
  <view class="sub-page">
    <child-switcher v-if="childrenStore.children.length > 1" :children="childrenStore.children" :current-id="currentChildId" @change="onChildChange" />
    <view v-if="balance" class="balance-card"><text class="balance-num">{{ balance.balance || 0 }}</text><text class="balance-label">当前积分</text><view class="balance-total"><text>累计获得：{{ balance.totalEarned || 0 }}</text></view></view>
    <view class="section-title">积分明细</view>
    <view v-if="loading"><loading-skeleton :rows="4" /></view>
    <view v-else-if="error"><empty-state icon="⚠️" text="加载失败" :show-retry="true" @retry="() => loadData()" /></view>
    <view v-else-if="!logList.length"><empty-state icon="📋" text="暂无积分记录" /></view>
    <view v-else class="record-list">
      <view v-for="item in logList" :key="item.id" class="log-card">
        <view class="log-left"><text class="log-type">{{ item.changeType || item.sourceType || '--' }}</text><text class="log-remark" v-if="item.remark">{{ item.remark }}</text><text class="log-time">{{ item.createTime || '' }}</text></view>
        <text class="log-amount" :class="item.amount > 0 ? 'positive' : 'negative'">{{ item.amount > 0 ? '+' : '' }}{{ item.amount }}</text>
      </view>
    </view>
  </view>
</template>
<script setup>
import { ref } from 'vue'; import { onLoad, onPullDownRefresh } from '@dcloudio/uni-app';
import { useChildrenStore } from '@/stores/children'; import { getPoints, getPointsLog } from '@/api/points';
import ChildSwitcher from '@/components/child-switcher.vue'; import EmptyState from '@/components/empty-state.vue'; import LoadingSkeleton from '@/components/loading-skeleton.vue';
const childrenStore = useChildrenStore(); const currentChildId = ref(null); const balance = ref(null); const logList = ref([]); const loading = ref(true); const error = ref(false);
onLoad((options) => { currentChildId.value = Number(options.studentId) || childrenStore.currentChildId; loadData(); });
async function loadData() { if (!currentChildId.value) return; loading.value = true; error.value = false; try { const [pointsRes, logRes] = await Promise.all([getPoints(currentChildId.value), getPointsLog(currentChildId.value)]); if (pointsRes.code === 200) balance.value = pointsRes.data; if (logRes.code === 200) logList.value = logRes.data || []; } catch { error.value = true; } finally { loading.value = false; } }
function onChildChange(id) { currentChildId.value = id; loadData(); }
onPullDownRefresh(async () => { await loadData(); uni.stopPullDownRefresh(); });
</script>
<style scoped lang="scss">
.sub-page { min-height: 100vh; padding-bottom: 24rpx; }
.balance-card { margin: 24rpx; padding: 40rpx; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border-radius: 16rpx; color: #fff; text-align: center; }
.balance-num { font-size: 72rpx; font-weight: 700; display: block; } .balance-label { font-size: 26rpx; opacity: 0.85; margin-top: 8rpx; display: block; }
.balance-total { font-size: 24rpx; opacity: 0.7; margin-top: 12rpx; }
.section-title { font-size: 30rpx; font-weight: 600; color: #303133; padding: 16rpx 24rpx; }
.record-list { padding: 0 24rpx; }
.log-card { display: flex; justify-content: space-between; align-items: center; background: #fff; border-radius: 12rpx; padding: 24rpx; margin-bottom: 12rpx; }
.log-left { flex: 1; min-width: 0; } .log-type { font-size: 28rpx; color: #303133; font-weight: 500; display: block; }
.log-remark { font-size: 24rpx; color: #909399; margin-top: 4rpx; display: block; } .log-time { font-size: 22rpx; color: #c0c4cc; margin-top: 4rpx; display: block; }
.log-amount { font-size: 32rpx; font-weight: 600; margin-left: 16rpx; } .positive { color: #67c23a; } .negative { color: #f56c6c; }
</style>
