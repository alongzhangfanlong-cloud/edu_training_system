<template>
  <view class="container">
    <view class="card">
      <text class="title">提交课堂反馈</text>

      <view class="form-item">
        <text class="form-label">选择课程</text>
        <picker mode="selector" :range="scheduleNames" @change="onScheduleChange">
          <view class="picker-val">{{ selectedScheduleName || '请选择' }}</view>
        </picker>
      </view>

      <view class="form-item">
        <text class="form-label">教学内容</text>
        <textarea v-model="content" class="textarea" placeholder="今日教学内容..." />
      </view>

      <view class="form-item">
        <text class="form-label">作业布置</text>
        <textarea v-model="homework" class="textarea" placeholder="作业内容..." />
      </view>

      <view class="form-item">
        <text class="form-label">课堂照片</text>
        <view class="photo-upload">
          <view v-for="(p, i) in photos" :key="i" class="photo-item">
            <image :src="p" mode="aspectFill" class="photo" />
            <text class="photo-del" @click="removePhoto(i)">✕</text>
          </view>
          <view v-if="photos.length < 9" class="add-photo" @click="addPhoto">+</view>
        </view>
      </view>

      <view class="form-item">
        <text class="form-label">整体评语</text>
        <textarea v-model="overallComment" class="textarea" placeholder="班级整体表现..." />
      </view>

      <view class="form-item">
        <text class="form-label">下节课预告</text>
        <input v-model="nextPreview" class="input" placeholder="下次课程内容预告" />
      </view>

      <!-- 学员评价 -->
      <view class="form-item" v-if="selectedSchedule">
        <text class="form-label">学员个人评价</text>
        <view v-for="student in students" :key="student.id" class="student-eval">
          <view class="flex-between">
            <text class="eval-name">{{ student.name }}</text>
            <view class="stars">
              <text v-for="i in 5" :key="i"
                    :class="student._score >= i ? 'star active' : 'star'"
                    @click="student._score = i">★</text>
            </view>
          </view>
          <input v-model="student._comment" class="eval-input" placeholder="评语（选填）" />
          <view class="homework-status">
            <text :class="student._hw === 1 ? 'hw-btn active' : 'hw-btn'" @click="student._hw = 1">已完成</text>
            <text :class="student._hw === 2 ? 'hw-btn active' : 'hw-btn'" @click="student._hw = 2">部分完成</text>
            <text :class="student._hw === 3 ? 'hw-btn active' : 'hw-btn'" @click="student._hw = 3">未完成</text>
          </view>
        </view>
      </view>

      <button class="btn-primary" :disabled="submitting" @click="submitFeedback">
        {{ submitting ? '提交中...' : '提交反馈' }}
      </button>
    </view>
  </view>
</template>

<script>
import { post, get } from '../../utils/request'

export default {
  data() {
    return {
      schedules: [], selectedSchedule: null,
      content: '', homework: '', photos: [], overallComment: '', nextPreview: '',
      students: [], submitting: false
    }
  },
  computed: {
    scheduleNames() { return this.schedules.map(s => `${s.courseName} (${s.date})`) },
    selectedScheduleName() {
      if (this.selectedSchedule === null) return ''
      const s = this.schedules[this.selectedSchedule]
      return s ? `${s.courseName} (${s.date})` : ''
    }
  },
  onLoad() { this.loadSchedules() },
  methods: {
    async loadSchedules() {
      try {
        const res = await get('/schedules', { startDate: new Date().toISOString().slice(0,10) })
        if (res && res.schedules) this.schedules = res.schedules
      } catch(e) {}
    },
    onScheduleChange(e) {
      this.selectedSchedule = e.detail.value
      // 模拟加载学员列表
      this.students = [
        { id: 1, name: '学员A', _score: 0, _comment: '', _hw: 0 },
        { id: 2, name: '学员B', _score: 0, _comment: '', _hw: 0 }
      ]
    },
    addPhoto() { uni.showToast({ title: '上传功能Phase 2实现', icon: 'none' }) },
    removePhoto(i) { this.photos.splice(i, 1) },
    async submitFeedback() {
      if (this.selectedSchedule === null) {
        return uni.showToast({ title: '请选择课程', icon: 'none' })
      }
      this.submitting = true
      try {
        const schedule = this.schedules[this.selectedSchedule]
        await post('/feedbacks', {
          scheduleId: schedule.id,
          content: this.content,
          homework: this.homework,
          photos: this.photos,
          overallComment: this.overallComment,
          nextPreview: this.nextPreview,
          studentFeedbacks: this.students.map(s => ({
            studentId: s.id, performanceScore: s._score,
            performanceComment: s._comment, homeworkStatus: s._hw
          }))
        })
        uni.showToast({ title: '反馈提交成功', icon: 'success' })
        setTimeout(() => uni.navigateBack(), 1500)
      } catch(e) { uni.showToast({ title: '提交失败', icon: 'none' }) }
      finally { this.submitting = false }
    }
  }
}
</script>

<style scoped>
.title { font-size: 32rpx; font-weight: 600; display: block; margin-bottom: 24rpx; }
.form-item { margin-bottom: 20rpx; }
.form-label { font-size: 26rpx; display: block; margin-bottom: 8rpx; color: #333; }
.picker-val { padding: 16rpx; background: #f5f5f5; border-radius: 8rpx; }
.textarea { width: 100%; height: 140rpx; background: #f5f5f5; border-radius: 8rpx; padding: 16rpx; font-size: 26rpx; box-sizing: border-box; }
.input { background: #f5f5f5; border-radius: 8rpx; padding: 16rpx; font-size: 26rpx; }
.photo-upload { display: flex; flex-wrap: wrap; gap: 12rpx; }
.photo-item { position: relative; }
.photo { width: 140rpx; height: 140rpx; border-radius: 8rpx; }
.photo-del { position: absolute; top: -8rpx; right: -8rpx; background: #ff4d4f; color: #fff; width: 36rpx; height: 36rpx; border-radius: 50%; text-align: center; line-height: 36rpx; font-size: 22rpx; }
.add-photo { width: 140rpx; height: 140rpx; border: 1px dashed #d9d9d9; border-radius: 8rpx; display: flex; align-items: center; justify-content: center; font-size: 48rpx; color: #d9d9d9; }
.student-eval { padding: 16rpx 0; border-bottom: 1px solid #f0f0f0; }
.eval-name { font-size: 28rpx; }
.stars { display: flex; }
.star { font-size: 36rpx; color: #ddd; }
.star.active { color: #faad14; }
.eval-input { background: #f5f5f5; border-radius: 6rpx; padding: 8rpx 12rpx; font-size: 24rpx; margin-top: 8rpx; }
.homework-status { display: flex; gap: 12rpx; margin-top: 8rpx; }
.hw-btn { padding: 4rpx 16rpx; border: 1px solid #d9d9d9; border-radius: 4rpx; font-size: 22rpx; }
.hw-btn.active { border-color: #1890ff; color: #1890ff; background: #e6f7ff; }
</style>
