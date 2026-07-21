<template>
  <view class="checkin-page">
    <!-- 统计卡片 -->
    <view class="stats-row">
      <view class="stat-card">
        <text class="stat-num">{{ teacherStore.todayCourseCount }}</text>
        <text class="stat-label">今日课程</text>
      </view>
      <view class="stat-card">
        <text class="stat-num" style="color:#e6a23c">{{ teacherStore.uncheckedCount }}</text>
        <text class="stat-label">待签到</text>
      </view>
      <view class="stat-card">
        <text class="stat-num" style="color:#67c23a">{{ checkedCount }}</text>
        <text class="stat-label">已签到</text>
      </view>
    </view>

    <view v-if="teacherStore.loading"><loading-skeleton :rows="3" /></view>
    <view v-else-if="!teacherStore.todaySchedule.length"><empty-state icon="📅" text="今日无课程安排 ☕" /></view>

    <view v-else class="schedule-list">
      <schedule-card
        v-for="item in teacherStore.todaySchedule"
        :key="item.scheduleId"
        :start-time="item.startTime"
        :end-time="item.endTime"
        :course-name="item.courseName"
        :class-name="item.className"
        :classroom-name="item.classroomName"
        :expected-count="item.expectedCount"
        :checked-count="item.checkedCount"
        :course-status="item.courseStatus"
        :attendance-status="item.attendanceStatus"
        :show-action="item.attendanceStatus !== 'COMPLETED' && item.courseStatus !== 'NOT_STARTED'"
        :action-text="item.attendanceStatus === 'PARTIAL' ? '签到管理' : '去签到'"
        @action="goBatch(item.scheduleId)"
      />
    </view>
  </view>
</template>

<script setup>
import { computed } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import { useTeacherStore } from '@/stores/teacher';
import ScheduleCard from '@/components/schedule-card.vue';
import EmptyState from '@/components/empty-state.vue';
import LoadingSkeleton from '@/components/loading-skeleton.vue';

const teacherStore = useTeacherStore();

const checkedCount = computed(() =>
  teacherStore.todaySchedule.filter(s => s.attendanceStatus === 'COMPLETED').length
);

function goBatch(scheduleId) {
  uni.navigateTo({ url: `/pages/teacher/checkin/batch?scheduleId=${scheduleId}` });
}

onShow(() => { teacherStore.fetchDashboard(); });
</script>

<style scoped lang="scss">
.checkin-page { min-height: 100vh; padding-bottom: 120rpx; }
.stats-row { display: grid; grid-template-columns: repeat(3, 1fr); gap: 12rpx; padding: 24rpx; }
.stat-card { background: #fff; border-radius: 12rpx; padding: 24rpx 8rpx; text-align: center; }
.stat-num { font-size: 40rpx; font-weight: 700; color: #409eff; display: block; }
.stat-label { font-size: 22rpx; color: #909399; margin-top: 4rpx; display: block; }
.schedule-list { padding: 0 24rpx; }
</style>
