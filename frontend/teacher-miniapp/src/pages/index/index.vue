<template>
  <view class="container">
    <!-- 教师信息 -->
    <view class="user-card card flex-row">
      <image class="avatar" :src="teacherInfo.avatar || '/static/default-avatar.png'" mode="aspectFill" />
      <view class="flex-1 ml-16">
        <text class="name">{{ teacherInfo.name || '教师' }}</text>
        <text class="text-muted">{{ teacherInfo.subjects || '' }}</text>
      </view>
    </view>

    <!-- 今日授课列表 -->
    <view class="card">
      <text class="card-title">📅 今日授课 ({{ todayClasses.length }}节)</text>
      <view v-if="todayClasses.length === 0" class="empty"><text>今日无课程安排</text></view>
      <view v-for="c in todayClasses" :key="c.id" class="class-item">
        <view class="flex-between">
          <view class="flex-1">
            <text class="class-name">{{ c.courseName }}</text>
            <text class="class-meta">{{ c.startTime }}-{{ c.endTime }} | {{ c.classroomName || '待定' }}</text>
            <text class="class-meta">班级: {{ c.className || '-' }} | 应到: {{ c.studentCount || 0 }}人</text>
          </view>
          <view class="class-actions">
            <button class="btn-primary btn-sm" @click="enterClass(c)">进入课堂</button>
          </view>
        </view>
      </view>
    </view>

    <!-- 待办事项 -->
    <view class="card">
      <text class="card-title">📋 待办事项</text>
      <view class="todo-grid">
        <view class="todo-item" @click="goPendingAttendance">
          <view class="todo-badge pending">{{ pendingAttendance }}</view>
          <text>待点名</text>
        </view>
        <view class="todo-item" @click="goPendingFeedback">
          <view class="todo-badge warning">{{ pendingFeedback }}</view>
          <text>待反馈</text>
        </view>
        <view class="todo-item" @click="goPendingLeave">
          <view class="todo-badge info">{{ pendingLeave }}</view>
          <text>待审批请假</text>
        </view>
      </view>
    </view>

    <!-- 快捷统计 -->
    <view class="card">
      <text class="card-title">📊 本周统计</text>
      <view class="stats-grid">
        <view class="stat-item"><text class="stat-val">{{ weekStats.totalClasses }}</text><text>授课节数</text></view>
        <view class="stat-item"><text class="stat-val">{{ weekStats.totalStudents }}</text><text>授课学员</text></view>
        <view class="stat-item"><text class="stat-val">¥{{ weekStats.estimatedSalary }}</text><text>预估课时费</text></view>
      </view>
    </view>
  </view>
</template>

<script>
import { get } from '../../utils/request'

export default {
  data() {
    return {
      teacherInfo: {},
      todayClasses: [],
      pendingAttendance: 0,
      pendingFeedback: 0,
      pendingLeave: 0,
      weekStats: { totalClasses: 0, totalStudents: 0, estimatedSalary: 0 }
    }
  },
  onShow() { this.loadData() },
  methods: {
    async loadData() {
      const today = new Date().toISOString().slice(0,10)
      try {
        const res = await get('/schedules', { date: today })
        if (res && res.schedules) this.todayClasses = res.schedules
      } catch(e) {}
      // TODO: 加载待办和统计
    },
    enterClass(c) { uni.navigateTo({ url: `/pages/classroom/roll-call?scheduleId=${c.id}&classId=${c.classId}` }) },
    goPendingAttendance() { uni.showToast({ title: '暂无待点名', icon: 'none' }) },
    goPendingFeedback() { uni.navigateTo({ url: '/pages/feedback/submit' }) },
    goPendingLeave() { uni.showToast({ title: '暂无待审批', icon: 'none' }) }
  }
}
</script>

<style scoped>
.avatar { width: 80rpx; height: 80rpx; border-radius: 50%; }
.name { font-size: 32rpx; font-weight: 600; display: block; }
.ml-16 { margin-left: 16rpx; }
.card-title { font-size: 28rpx; font-weight: 600; margin-bottom: 16rpx; display: block; }
.class-item { padding: 16rpx 0; border-bottom: 1px solid #f0f0f0; }
.class-item:last-child { border-bottom: none; }
.class-name { font-size: 28rpx; font-weight: 600; display: block; }
.class-meta { font-size: 24rpx; color: #666; display: block; margin-top: 4rpx; }
.class-actions { }
.btn-sm { padding: 12rpx 20rpx; font-size: 24rpx; }
.todo-grid { display: flex; justify-content: space-around; }
.todo-item { text-align: center; }
.todo-badge { width: 56rpx; height: 56rpx; border-radius: 50%; display: flex; align-items: center; justify-content: center; color: #fff; font-size: 26rpx; font-weight: 600; margin: 0 auto 8rpx; }
.todo-badge.pending { background: #ff4d4f; }
.todo-badge.warning { background: #faad14; }
.todo-badge.info { background: #1890ff; }
.stats-grid { display: flex; justify-content: space-around; }
.stat-item { text-align: center; }
.stat-val { font-size: 36rpx; font-weight: 700; color: #1890ff; display: block; }
.empty { text-align: center; padding: 30rpx; }
</style>
