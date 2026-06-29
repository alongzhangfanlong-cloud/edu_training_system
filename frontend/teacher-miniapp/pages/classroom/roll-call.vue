<template>
  <view class="container">
    <!-- 课程信息 -->
    <view class="card">
      <text class="title">{{ courseName || '课堂点名' }}</text>
      <text class="text-muted">{{ scheduleDate }} {{ scheduleTime }}</text>
    </view>

    <!-- 签到二维码 (教师端展示) -->
    <view class="card">
      <view class="flex-between">
        <text class="section-title">签到二维码</text>
        <button class="btn-outline btn-xs" @click="refreshQR">刷新</button>
      </view>
      <view class="qr-area" @click="enlargeQR">
        <text class="qr-placeholder">📱 二维码区域</text>
        <text class="text-muted qr-tip">点击放大供学员扫码</text>
      </view>
    </view>

    <!-- 统计 -->
    <view class="stats-bar card flex-between">
      <text>应到 <text class="text-primary">{{ students.length }}</text> 人</text>
      <text>实到 <text class="text-success">{{ presentCount }}</text> 人</text>
      <text>请假 <text class="text-warning">{{ leaveCount }}</text> 人</text>
      <text>旷课 <text class="text-danger">{{ absentCount }}</text> 人</text>
    </view>

    <!-- 学员列表 -->
    <view class="card">
      <view class="flex-between" style="margin-bottom:16rpx">
        <text class="section-title">学员列表</text>
        <button class="btn-outline btn-xs" @click="batchAllPresent">全部出勤</button>
      </view>
      <view v-for="student in students" :key="student.id" class="student-row">
        <view class="flex-row flex-1">
          <image class="s-avatar" :src="student.avatar || '/static/default-avatar.png'" mode="aspectFill" />
          <text class="s-name">{{ student.name }}</text>
        </view>
        <view class="status-btns">
          <text :class="['status-btn', student._status === 1 ? 'active present' : '']" @click="setStatus(student, 1)">出勤</text>
          <text :class="['status-btn', student._status === 2 ? 'active leave' : '']" @click="setStatus(student, 2)">请假</text>
          <text :class="['status-btn', student._status === 3 ? 'active absent' : '']" @click="setStatus(student, 3)">旷课</text>
        </view>
      </view>
    </view>

    <!-- 提交按钮 -->
    <button class="btn-primary submit-btn" :disabled="submitting" @click="submitRollCall">
      {{ submitting ? '提交中...' : '确认提交点名' }}
    </button>
  </view>
</template>

<script>
import { post } from '../../utils/request'

export default {
  data() {
    return {
      scheduleId: null, classId: null,
      courseName: '', scheduleDate: '', scheduleTime: '',
      students: [],
      submitting: false
    }
  },
  computed: {
    presentCount() { return this.students.filter(s => s._status === 1).length },
    leaveCount() { return this.students.filter(s => s._status === 2).length },
    absentCount() { return this.students.filter(s => s._status === 3).length }
  },
  onLoad(options) {
    this.scheduleId = options.scheduleId
    this.classId = options.classId
    this.loadStudents()
  },
  methods: {
    async loadStudents() {
      // Phase 1: 从班级加载学员列表
      try {
        if (this.classId) {
          const cls = await (await uni.request({ url: `http://localhost:8080/api/v1/classes/${this.classId}` }))
          // 简化：模拟学员列表
        }
      } catch(e) {}
      // 模拟数据
      this.students = [
        { id: 1, name: '张三', avatar: '', _status: 0 },
        { id: 2, name: '李四', avatar: '', _status: 0 },
        { id: 3, name: '王五', avatar: '', _status: 0 }
      ]
    },
    setStatus(student, status) { student._status = status },
    batchAllPresent() { this.students.forEach(s => s._status = 1) },
    refreshQR() { uni.showToast({ title: '二维码已刷新', icon: 'success' }) },
    enlargeQR() { uni.showToast({ title: '二维码放大', icon: 'none' }) },
    async submitRollCall() {
      const unchecked = this.students.filter(s => !s._status)
      if (unchecked.length) {
        return uni.showToast({ title: `还有${unchecked.length}名学员未标记`, icon: 'none' })
      }
      this.submitting = true
      try {
        await post('/attendances/batch-check', {
          scheduleId: this.scheduleId,
          attendances: this.students.map(s => ({ studentId: s.id, status: s._status }))
        })
        uni.showToast({ title: '点名提交成功', icon: 'success' })
        setTimeout(() => uni.navigateBack(), 1500)
      } catch(e) { uni.showToast({ title: '提交失败', icon: 'none' }) }
      finally { this.submitting = false }
    }
  }
}
</script>

<style scoped>
.title { font-size: 32rpx; font-weight: 600; display: block; }
.section-title { font-size: 28rpx; font-weight: 600; }
.btn-xs { padding: 8rpx 16rpx; font-size: 22rpx; border-radius: 6rpx; }
.qr-area { text-align: center; padding: 40rpx; background: #f9f9f9; border-radius: 12rpx; }
.qr-placeholder { font-size: 80rpx; display: block; }
.qr-tip { font-size: 24rpx; margin-top: 12rpx; display: block; }
.stats-bar { text-align: center; font-size: 26rpx; }
.student-row { display: flex; align-items: center; padding: 16rpx 0; border-bottom: 1px solid #f0f0f0; }
.s-avatar { width: 60rpx; height: 60rpx; border-radius: 50%; margin-right: 16rpx; }
.s-name { font-size: 28rpx; }
.status-btns { display: flex; gap: 8rpx; }
.status-btn { padding: 6rpx 16rpx; border: 1px solid #d9d9d9; border-radius: 6rpx; font-size: 22rpx; }
.status-btn.active.present { background: #f6ffed; border-color: #52c41a; color: #52c41a; }
.status-btn.active.leave { background: #fff7e6; border-color: #faad14; color: #faad14; }
.status-btn.active.absent { background: #fff2f0; border-color: #ff4d4f; color: #ff4d4f; }
.submit-btn { margin-top: 32rpx; }
</style>
