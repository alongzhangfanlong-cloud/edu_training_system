<template>
  <view class="child-detail-page">
    <view v-if="loading"><loading-skeleton :rows="3" /></view>
    <view v-else-if="error"><empty-state icon="⚠️" text="加载失败" :show-retry="true" @retry="loadDetail" /></view>
    <view v-else-if="!detail"><empty-state icon="👶" text="孩子信息不存在" /></view>

    <template v-else>
      <view class="section">
        <view class="info-header">
          <image class="info-avatar" :src="detail.avatar || '/static/logo.png'" mode="aspectFill" />
          <view class="info-main">
            <text class="info-name">{{ detail.name }}</text>
            <text class="info-grade">{{ detail.grade || '--' }}</text>
          </view>
        </view>
        <view class="info-grid">
          <view class="info-item">
            <text class="info-label">性别</text>
            <text class="info-value">{{ detail.gender === 1 ? '男' : detail.gender === 2 ? '女' : '--' }}</text>
          </view>
          <view class="info-item">
            <text class="info-label">学校</text>
            <text class="info-value">{{ detail.school || '--' }}</text>
          </view>
          <view class="info-item">
            <text class="info-label">总课时</text>
            <text class="info-value">{{ detail.totalHours || 0 }}</text>
          </view>
          <view class="info-item">
            <text class="info-label">剩余课时</text>
            <text class="info-value primary">{{ detail.remainingHours || 0 }}</text>
          </view>
        </view>
      </view>

      <view v-if="detail.classes && detail.classes.length" class="section">
        <view class="section-title">在读班级</view>
        <view v-for="cls in detail.classes" :key="cls.classId || cls.id" class="class-item">
          <text class="class-name">{{ cls.className || cls.name || '--' }}</text>
          <text class="class-course">{{ cls.courseName || '--' }}</text>
        </view>
      </view>
    </template>
  </view>
</template>

<script setup>
import { ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { getChildDetail } from '@/api/parent';
import EmptyState from '@/components/empty-state.vue';
import LoadingSkeleton from '@/components/loading-skeleton.vue';

const loading = ref(true);
const error = ref(false);
const detail = ref(null);
let studentId = null;

onLoad((options) => {
  studentId = options.studentId;
  loadDetail();
});

async function loadDetail() {
  if (!studentId) return;
  loading.value = true; error.value = false;
  try {
    const res = await getChildDetail(studentId);
    if (res.code === 200) detail.value = res.data;
  } catch { error.value = true; }
  finally { loading.value = false; }
}
</script>

<style scoped lang="scss">
.child-detail-page { padding: 24rpx; min-height: 100vh; }
.section { background: #fff; border-radius: 12rpx; margin-bottom: 24rpx; overflow: hidden; }
.section-title { font-size: 30rpx; font-weight: 600; color: #303133; padding: 24rpx 24rpx 16rpx; }
.info-header { display: flex; align-items: center; padding: 32rpx 24rpx 24rpx; }
.info-avatar { width: 120rpx; height: 120rpx; border-radius: 50%; background: #f0f2f5; }
.info-main { margin-left: 24rpx; }
.info-name { font-size: 36rpx; font-weight: 700; color: #303133; display: block; }
.info-grade { font-size: 26rpx; color: #909399; margin-top: 8rpx; display: block; }
.info-grid { display: grid; grid-template-columns: 1fr 1fr; padding: 0 24rpx 24rpx; gap: 24rpx; }
.info-item { display: flex; flex-direction: column; }
.info-label { font-size: 24rpx; color: #909399; margin-bottom: 8rpx; }
.info-value { font-size: 30rpx; color: #303133; font-weight: 500; }
.info-value.primary { color: #409eff; }
.class-item { display: flex; justify-content: space-between; align-items: center; padding: 20rpx 24rpx; border-top: 1rpx solid #f0f2f5; }
.class-name { font-size: 28rpx; color: #303133; font-weight: 500; }
.class-course { font-size: 24rpx; color: #909399; }
</style>
