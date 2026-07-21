<template>
  <view class="sched-card" :class="{ 'is-finished': isFinished, 'is-active': isActive }">
    <view class="sc-time-bar">
      <text class="sc-time">{{ startTime }} - {{ endTime }}</text>
      <text v-if="isActive" class="sc-badge">进行中</text>
    </view>
    <view class="sc-body">
      <text class="sc-course">{{ courseName }}</text>
      <view class="sc-meta">
        <text class="sc-class">{{ className }}</text>
        <text v-if="classroomName" class="sc-room">📍 {{ classroomName }}</text>
      </view>
      <view class="sc-attendance">
        <text>签到：</text>
        <text :style="{ color: checkinColor }" class="sc-count">{{ checkedCount }}/{{ expectedCount }}</text>
        <text class="sc-status-text"> {{ attendanceLabel }}</text>
      </view>
    </view>
    <view v-if="showAction" class="sc-action" @click.stop="$emit('action')">
      <text>{{ actionText }}</text>
    </view>
  </view>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  startTime: { type: String, default: '' },
  endTime: { type: String, default: '' },
  courseName: { type: String, default: '' },
  className: { type: String, default: '' },
  classroomName: { type: String, default: '' },
  expectedCount: { type: Number, default: 0 },
  checkedCount: { type: Number, default: 0 },
  courseStatus: { type: String, default: '' },
  attendanceStatus: { type: String, default: '' },
  showAction: { type: Boolean, default: false },
  actionText: { type: String, default: '签到' },
});

defineEmits(['action']);

const isActive = computed(() => props.courseStatus === 'IN_PROGRESS');
const isFinished = computed(() => props.courseStatus === 'FINISHED');

const attendanceLabel = computed(() => {
  const map = {
    COMPLETED: '✓已签到',
    PARTIAL: '签到中',
    UNCHECKED: '待签到',
    NOT_STARTED: '未开始',
  };
  return map[props.attendanceStatus] || '';
});

const checkinColor = computed(() => {
  const map = {
    COMPLETED: '#67c23a',
    PARTIAL: '#e6a23c',
    UNCHECKED: '#f56c6c',
    NOT_STARTED: '#909399',
  };
  return map[props.attendanceStatus] || '#909399';
});
</script>

<style scoped lang="scss">
.sched-card {
  background: #fff;
  border-radius: 12rpx;
  padding: 20rpx 24rpx;
  margin-bottom: 12rpx;
  border-left: 4rpx solid transparent;
  &.is-active { border-left-color: #67c23a; }
  &.is-finished { opacity: 0.6; }
}
.sc-time-bar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8rpx; }
.sc-time { font-size: 26rpx; color: #409eff; font-weight: 500; }
.sc-badge { font-size: 20rpx; background: #67c23a; color: #fff; padding: 2rpx 12rpx; border-radius: 20rpx; }
.sc-body { margin-bottom: 8rpx; }
.sc-course { font-size: 30rpx; font-weight: 600; color: #303133; display: block; margin-bottom: 4rpx; }
.sc-meta { display: flex; gap: 16rpx; margin-bottom: 4rpx; }
.sc-class { font-size: 24rpx; color: #606266; }
.sc-room { font-size: 24rpx; color: #909399; }
.sc-attendance { font-size: 24rpx; color: #606266; }
.sc-count { font-weight: 600; }
.sc-status-text { color: #909399; }
.sc-action {
  margin-top: 8rpx;
  text-align: center;
  padding: 12rpx;
  background: #409eff;
  border-radius: 8rpx;
  color: #fff;
  font-size: 26rpx;
}
</style>
