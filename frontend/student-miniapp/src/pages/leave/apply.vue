<template>
  <view class="container">
    <view class="card">
      <text class="title">请假申请</text>

      <!-- 选择课程 -->
      <view class="form-item">
        <text class="form-label">请假课程 <text class="required">*</text></text>
        <picker mode="selector" :range="courseNames" @change="onCourseChange">
          <view class="picker-value">{{ selectedCourseName || '请选择课程' }}</view>
        </picker>
      </view>

      <!-- 请假类型 -->
      <view class="form-item">
        <text class="form-label">请假类型 <text class="required">*</text></text>
        <view class="radio-group">
          <view v-for="item in leaveTypes" :key="item.value"
                :class="['radio-item', { active: leaveType === item.value }]"
                @click="leaveType = item.value">{{ item.label }}</view>
        </view>
      </view>

      <!-- 请假原因 -->
      <view class="form-item">
        <text class="form-label">请假原因</text>
        <textarea v-model="reason" class="textarea" placeholder="请输入请假原因..." maxlength="200" />
      </view>

      <!-- 消课提示 -->
      <view class="consume-tip" v-if="consumeInfo">
        <text class="tip-title">💡 消课提示</text>
        <text class="tip-text">{{ consumeInfo.rule }}</text>
        <text class="tip-hours">预计消课: {{ consumeInfo.consumeHours }} 课时</text>
      </view>

      <!-- 提交 -->
      <button class="btn-primary" :disabled="submitting" @click="submitLeave">
        {{ submitting ? '提交中...' : '提交申请' }}
      </button>
    </view>
  </view>
</template>

<script>
import { getSchedules } from '../../api/schedule'
import { applyLeave } from '../../api/attendance'

export default {
  data() {
    return {
      scheduleId: null,
      courseList: [],
      selectedCourse: null,
      leaveType: 1,
      leaveTypes: [{ label: '病假', value: 1 }, { label: '事假', value: 2 }, { label: '其他', value: 3 }],
      reason: '',
      consumeInfo: null,
      submitting: false
    }
  },
  computed: {
    courseNames() { return this.courseList.map(c => `${c.courseName} (${c.date} ${c.startTime})`) },
    selectedCourseName() {
      if (!this.selectedCourse) return ''
      const c = this.courseList[this.selectedCourse]
      return c ? `${c.courseName} (${c.date} ${c.startTime})` : ''
    }
  },
  onLoad(options) {
    this.scheduleId = options.scheduleId
    this.loadCourses()
  },
  methods: {
    async loadCourses() {
      try {
        const res = await getSchedules({ startDate: new Date().toISOString().slice(0,10) })
        if (res && res.schedules) {
          this.courseList = res.schedules.filter(s => s.status === 1)
          if (this.scheduleId) {
            const idx = this.courseList.findIndex(s => s.id == this.scheduleId)
            if (idx >= 0) this.selectedCourse = idx
          }
        }
      } catch(e) { console.error(e) }
    },
    onCourseChange(e) {
      this.selectedCourse = e.detail.value
      this.calcConsume()
    },
    async calcConsume() {
      if (!this.selectedCourse && this.selectedCourse !== 0) return
      // Phase 1简化: 根据请假提前时间计算
      const course = this.courseList[this.selectedCourse]
      const hoursBefore = (new Date(`${course.date}T${course.startTime}`) - new Date()) / 3600000
      if (hoursBefore >= 24) {
        this.consumeInfo = { rule: '提前24小时以上请假，不消课，可补课', consumeHours: 0 }
      } else if (hoursBefore >= 2) {
        this.consumeInfo = { rule: '提前2小时以上请假，消课50%，可补课', consumeHours: 0.5 }
      } else {
        this.consumeInfo = { rule: '临时请假，正常消课，不可补课', consumeHours: 1 }
      }
    },
    async submitLeave() {
      if (this.selectedCourse === null) {
        uni.showToast({ title: '请选择课程', icon: 'none' })
        return
      }
      this.submitting = true
      try {
        const course = this.courseList[this.selectedCourse]
        await applyLeave({
          scheduleId: course.id,
          leaveType: this.leaveType,
          reason: this.reason
        })
        uni.showToast({ title: '申请已提交', icon: 'success' })
        setTimeout(() => uni.navigateBack(), 1500)
      } catch(e) {
        uni.showToast({ title: e.message || '提交失败', icon: 'none' })
      } finally {
        this.submitting = false
      }
    }
  }
}
</script>

<style scoped>
.title { font-size: 34rpx; font-weight: 700; display: block; margin-bottom: 32rpx; }
.form-item { margin-bottom: 24rpx; }
.form-label { font-size: 28rpx; display: block; margin-bottom: 8rpx; }
.required { color: #ff4d4f; }
.picker-value { padding: 16rpx; background: #f5f5f5; border-radius: 8rpx; font-size: 26rpx; color: #333; }
.radio-group { display: flex; gap: 24rpx; }
.radio-item { padding: 12rpx 32rpx; border: 1px solid #d9d9d9; border-radius: 8rpx; font-size: 26rpx; }
.radio-item.active { border-color: #1890ff; color: #1890ff; background: #e6f7ff; }
.textarea { width: 100%; height: 160rpx; background: #f5f5f5; border-radius: 8rpx; padding: 16rpx; font-size: 26rpx; box-sizing: border-box; }
.consume-tip { background: #fff7e6; border-radius: 8rpx; padding: 20rpx; margin: 16rpx 0; }
.tip-title { font-size: 26rpx; font-weight: 600; display: block; }
.tip-text { font-size: 24rpx; color: #666; display: block; margin: 8rpx 0; }
.tip-hours { font-size: 24rpx; color: #faad14; font-weight: 600; }
.btn-primary { margin-top: 32rpx; }
</style>
