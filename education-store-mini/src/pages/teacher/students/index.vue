<template>
  <view class="students-page">
    <view v-if="loading && list.length === 0" class="loading-wrap">
      <view class="loading-spinner" />
      <text class="loading-text">加载中...</text>
    </view>

    <view v-else-if="error" class="error-wrap" @click="loadData(true)">
      <text class="error-icon">⚠️</text>
      <text class="loading-text">{{ error }}</text>
      <view class="retry-btn">
        <text class="retry-btn-text">点击重试</text>
      </view>
    </view>

    <template v-else>
      <!-- 统计 -->
      <view class="stat-bar">
        <text class="stat-text">共 <text class="stat-num">{{ list.length }}</text> 名学生</text>
      </view>

      <!-- 学生列表 -->
      <view v-if="list.length > 0" class="student-list">
        <view v-for="item in list" :key="item.studentId" class="student-card">
          <view class="sc-avatar" :class="item.gender === '女' ? 'female' : ''">
            <text class="avatar-text">{{ item.studentName?.charAt(0) || '学' }}</text>
          </view>
          <view class="sc-info">
            <view class="sc-name-row">
              <text class="sc-name">{{ item.studentName }}</text>
              <text class="sc-gender" :class="item.gender === '女' ? 'female' : ''">{{ item.gender || '--' }}</text>
              <text v-if="item.grade" class="sc-grade">{{ item.grade }}</text>
            </view>
            <view class="sc-meta">
              <text class="sc-class">{{ item.className }} · {{ item.courseName }}</text>
            </view>
            <view v-if="item.mobile" class="sc-mobile">📱 {{ item.mobile }}</view>
          </view>
        </view>
      </view>

      <view v-else class="empty-wrap">
        <text class="empty-icon">📚</text>
        <text class="empty-text">暂无学生数据</text>
        <text class="empty-sub">您还没有被分配班级</text>
      </view>
    </template>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { getMyStudents } from '@/api/teacher';
import { useAuthStore } from '@/stores/auth';

const authStore = useAuthStore();
const loading = ref(false);
const list = ref([]);
const error = ref('');

async function loadData(isRefresh) {
  if (loading.value) return;
  loading.value = true;
  error.value = '';
  try {
    const res = await getMyStudents();
    if (res.code === 200) {
      list.value = res.data || [];
    } else {
      error.value = res.msg || '加载失败';
    }
    if (isRefresh) uni.stopPullDownRefresh();
  } catch (e) {
    console.error('加载学生列表失败:', e);
    error.value = '网络异常，请稍后重试';
    if (isRefresh) uni.stopPullDownRefresh();
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  if (authStore.isTeacher) {
    loadData();
  }
});

defineExpose({ loadData });
</script>

<script>
export default {
  onPullDownRefresh() {
    this.$.exposed?.loadData?.(true);
  }
};
</script>

<style scoped lang="scss">
.students-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 20rpx 24rpx 40rpx;
}

.loading-wrap,
.error-wrap,
.empty-wrap {
  padding: 160rpx 0;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.loading-spinner {
  width: 36px; height: 36px;
  border: 3px solid #e4e7ed;
  border-top-color: #409eff;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }
.loading-text { font-size: 28rpx; color: #909399; margin-top: 16rpx; }
.error-icon { font-size: 72rpx; }
.retry-btn {
  margin-top: 20rpx;
  padding: 12rpx 40rpx;
  background: #409eff;
  border-radius: 8rpx;
}
.retry-btn-text { color: #fff; font-size: 28rpx; }
.empty-icon { font-size: 72rpx; margin-bottom: 12rpx; }
.empty-text { font-size: 30rpx; color: #606266; margin-bottom: 8rpx; }
.empty-sub { font-size: 26rpx; color: #909399; }

.stat-bar {
  padding: 20rpx 8rpx;
}
.stat-text { font-size: 26rpx; color: #909399; }
.stat-num { color: #409eff; font-weight: 600; font-size: 30rpx; }

.student-list { display: flex; flex-direction: column; gap: 16rpx; }
.student-card {
  display: flex;
  align-items: center;
  gap: 24rpx;
  background: #fff;
  border-radius: 16rpx;
  padding: 28rpx 24rpx;
  box-shadow: 0 2rpx 8rpx rgba(0,0,0,.04);
}

.sc-avatar {
  width: 88rpx; height: 88rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, #409eff, #667eea);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.sc-avatar.female {
  background: linear-gradient(135deg, #f56c6c, #ff9a9e);
}
.avatar-text { color: #fff; font-size: 36rpx; font-weight: 600; }

.sc-info { flex: 1; min-width: 0; }
.sc-name-row { display: flex; align-items: center; gap: 12rpx; margin-bottom: 10rpx; }
.sc-name { font-size: 32rpx; font-weight: 600; color: #303133; }
.sc-gender {
  font-size: 22rpx;
  padding: 2rpx 12rpx;
  border-radius: 20rpx;
  background: #ecf5ff;
  color: #409eff;
}
.sc-gender.female { background: #fef0f0; color: #f56c6c; }
.sc-grade {
  font-size: 22rpx;
  padding: 2rpx 12rpx;
  border-radius: 20rpx;
  background: #f0f9eb;
  color: #67c23a;
}
.sc-meta { margin-bottom: 6rpx; }
.sc-class { font-size: 26rpx; color: #606266; }
.sc-mobile { font-size: 24rpx; color: #909399; }
</style>
