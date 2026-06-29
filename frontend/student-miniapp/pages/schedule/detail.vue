<template>
  <view class="container">
    <!-- 课程信息 -->
    <view class="card">
      <text class="course-name">{{ course.courseName }}</text>
      <view class="info-grid">
        <view class="info-item"><text class="label">日期</text><text>{{ course.date }}</text></view>
        <view class="info-item"><text class="label">时间</text><text>{{ course.startTime }} - {{ course.endTime }}</text></view>
        <view class="info-item"><text class="label">教师</text><text>{{ course.teacherName || '待定' }}</text></view>
        <view class="info-item"><text class="label">教室</text><text>{{ course.classroomName || '待定' }}</text></view>
        <view class="info-item"><text class="label">班级</text><text>{{ course.className || '-' }}</text></view>
        <view class="info-item"><text class="label">状态</text>
          <text :class="['text-' + statusColor]">{{ statusMap[course.status] || '正常' }}</text>
        </view>
      </view>
    </view>

    <!-- 签到状态 -->
    <view class="card">
      <text class="section-title">签到状态</text>
      <view v-if="attendance" class="attendance-status">
        <view :class="['status-badge', 'att-' + attendance.status]">
          {{ attendanceStatusMap[attendance.status] || '未知' }}
        </view>
        <text v-if="attendance.checkInTime" class="text-muted">签到时间: {{ attendance.checkInTime }}</text>
      </view>
      <view v-else class="attendance-actions">
        <text class="text-warning">尚未签到</text>
        <button class="btn-primary" @click="goCheckin">扫码签到</button>
        <button class="btn-outline" @click="goLeave">申请请假</button>
      </view>
    </view>

    <!-- 课堂反馈（课后显示） -->
    <view class="card" v-if="feedback">
      <text class="section-title">课堂反馈</text>
      <view class="feedback-content">
        <text class="label">教学内容</text>
        <text class="content-text">{{ feedback.content || '暂无' }}</text>
        <text class="label" style="margin-top:16rpx">作业布置</text>
        <text class="content-text">{{ feedback.homework || '暂无' }}</text>
        <view v-if="feedback.photos && feedback.photos.length" class="photos">
          <image v-for="(p, i) in feedback.photos" :key="i" :src="p" mode="aspectFill" class="photo" />
        </view>
        <text class="label" style="margin-top:16rpx">教师评语</text>
        <text class="content-text">{{ feedback.overallComment || '暂无' }}</text>
      </view>
    </view>

    <!-- 个人评价 -->
    <view class="card" v-if="studentFeedback.performanceScore">
      <text class="section-title">课堂表现</text>
      <view class="score-row">
        <text>评分: </text>
        <view class="stars">
          <text v-for="i in 5" :key="i" :class="i <= studentFeedback.performanceScore ? 'star active' : 'star'">★</text>
        </view>
      </view>
      <text class="content-text">{{ studentFeedback.performanceComment }}</text>
    </view>
  </view>
</template>

<script>
import { getSchedules } from '../../api/schedule'
import { getAttendanceBySchedule } from '../../api/attendance'
import { getStudentFeedback } from '../../api/feedback'
import { getUserStore } from '../../store/user'

export default {
  data() {
    return {
      id: null,
      course: {},
      attendance: null,
      feedback: null,
      studentFeedback: {},
      statusMap: { 0:'已取消', 1:'正常', 2:'已调课', 3:'补课', 4:'停课' },
      attendanceStatusMap: { 1:'已出勤', 2:'已请假', 3:'旷课', 4:'补课' }
    }
  },
  computed: {
    statusColor() {
      const map = { 0:'danger', 1:'success', 2:'warning', 3:'info', 4:'danger' }
      return map[this.course.status] || 'muted'
    }
  },
  onLoad(options) {
    this.id = options.id
    this.loadDetail()
  },
  methods: {
    async loadDetail() {
      try {
        const res = await getSchedules({})
        if (res && res.schedules) {
          this.course = res.schedules.find(s => s.id == this.id) || {}
        }
        if (this.course.id) {
          try { this.attendance = (await getAttendanceBySchedule(this.course.id))[0] } catch(e) {}
          try { this.studentFeedback = await getStudentFeedback(this.course.id, getUserStore().state.userInfo?.id) || {} } catch(e) {}
        }
      } catch(e) { console.error(e) }
    },
    goCheckin() { uni.navigateTo({ url: `/pages/checkin/checkin?scheduleId=${this.id}` }) },
    goLeave() { uni.navigateTo({ url: `/pages/leave/apply?scheduleId=${this.id}` }) }
  }
}
</script>

<style scoped>
.course-name { font-size: 34rpx; font-weight: 700; display: block; margin-bottom: 20rpx; }
.info-grid { display: flex; flex-wrap: wrap; }
.info-item { width: 50%; padding: 8rpx 0; font-size: 26rpx; }
.info-item .label { color: #999; margin-right: 8rpx; }
.section-title { font-size: 28rpx; font-weight: 600; margin-bottom: 16rpx; display: block; }
.attendance-status { text-align: center; }
.status-badge { display: inline-block; padding: 12rpx 40rpx; border-radius: 12rpx; font-size: 30rpx; font-weight: 600; }
.att-1 { background: #f6ffed; color: #52c41a; }
.att-2 { background: #fff7e6; color: #faad14; }
.att-3 { background: #fff2f0; color: #ff4d4f; }
.attendance-actions { text-align: center; }
.attendance-actions button { margin: 16rpx auto; width: 60%; }
.btn-outline { background: #fff; color: #1890ff; border: 1px solid #1890ff; border-radius: 12rpx; padding: 16rpx 32rpx; }
.feedback-content { }
.content-text { font-size: 26rpx; color: #333; line-height: 1.6; display: block; margin: 8rpx 0; }
.photos { display: flex; gap: 12rpx; margin: 12rpx 0; overflow-x: auto; }
.photo { width: 160rpx; height: 160rpx; border-radius: 8rpx; }
.score-row { display: flex; align-items: center; margin: 12rpx 0; }
.stars { display: flex; }
.star { font-size: 36rpx; color: #ddd; margin-right: 4rpx; }
.star.active { color: #faad14; }
</style>
