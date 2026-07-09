<template>
  <view class="container">
    <!-- 用户头部 -->
    <view class="header-card card">
      <view class="flex-row">
        <image class="avatar" :src="userInfo.avatar || '/static/images/default-avatar.png'" mode="aspectFill" />
        <view class="flex-1 ml-16">
          <text class="name">{{ userInfo.name || '学员' }}</text>
          <text class="grade text-muted" v-if="userInfo.grade">{{ userInfo.grade }}</text>
        </view>
        <view class="role-tag">{{ roleName }}</view>
      </view>
    </view>

    <!-- 家长端：孩子切换器 -->
    <view class="child-switcher card" v-if="isParent && children.length > 1">
      <scroll-view scroll-x class="child-scroll">
        <view
          v-for="child in children" :key="child.id"
          class="child-item"
          :class="{ active: currentChild && currentChild.id === child.id }"
          @click="switchChild(child)"
        >
          <image class="child-avatar" :src="child.avatar || '/static/images/default-avatar.png'" mode="aspectFill" />
          <text class="child-name">{{ child.name }}</text>
        </view>
      </scroll-view>
    </view>

    <!-- 今日课程卡片 -->
    <view class="card today-course" v-if="todayCourse">
      <view class="card-header flex-between">
        <text class="card-title">今日课程</text>
        <text class="text-primary" @click="goSchedule">查看课表</text>
      </view>
      <view class="course-info">
        <text class="course-name">{{ todayCourse.courseName }}</text>
        <view class="course-meta text-muted">
          <text>🕐 {{ todayCourse.startTime }}-{{ todayCourse.endTime }}</text>
          <text>📍 {{ todayCourse.classroomName || '待定' }}</text>
          <text>👨‍🏫 {{ todayCourse.teacherName }}</text>
        </view>
        <view class="course-actions">
          <button class="btn-primary btn-sm" @click="goCheckin">去签到</button>
          <button class="btn-outline btn-sm" @click="goCourseDetail(todayCourse.id)">查看详情</button>
        </view>
      </view>
    </view>
    <view class="card empty-card" v-else>
      <text class="text-muted">今日暂无课程安排</text>
    </view>

    <!-- 本周课表预览 -->
    <view class="card">
      <view class="card-header flex-between">
        <text class="card-title">本周课表</text>
        <text class="text-primary" @click="goSchedule">查看完整课表</text>
      </view>
      <view class="week-preview">
        <view v-for="(day, idx) in weekSchedules" :key="idx" class="week-day">
          <text class="day-label">{{ day.label }}</text>
          <view v-if="day.courses.length" class="day-courses">
            <view v-for="c in day.courses" :key="c.id" class="day-course-item" @click="goCourseDetail(c.id)">
              <text class="course-time">{{ c.startTime }}</text>
              <text class="course-title-text">{{ c.courseName }}</text>
            </view>
          </view>
          <text v-else class="no-course">-</text>
        </view>
      </view>
    </view>

    <!-- 待办提醒 -->
    <view class="card">
      <view class="card-header">
        <text class="card-title">待办提醒</text>
      </view>
      <view class="todo-grid">
        <view class="todo-item" @click="goCheckin">
          <view class="todo-badge pending">{{ pendingCheckins }}</view>
          <text>待签到</text>
        </view>
        <view class="todo-item" @click="goHomework">
          <view class="todo-badge warning">{{ pendingHomework }}</view>
          <text>待交作业</text>
        </view>
        <view class="todo-item" @click="goSchedule">
          <view class="todo-badge info">{{ pendingMakeup }}</view>
          <text>待补课</text>
        </view>
      </view>
    </view>

    <!-- 快捷入口 -->
    <view class="quick-actions card">
      <view class="action-grid">
        <view class="action-item" @click="goCheckin">
          <text class="action-icon">📱</text>
          <text class="action-text">签到</text>
        </view>
        <view class="action-item" @click="goLeave">
          <text class="action-icon">📝</text>
          <text class="action-text">请假</text>
        </view>
        <view class="action-item" @click="goHours">
          <text class="action-icon">⏱️</text>
          <text class="action-text">查课时</text>
        </view>
        <view class="action-item">
          <text class="action-icon">💬</text>
          <text class="action-text">联系老师</text>
        </view>
        <view class="action-item" @click="goPoints">
          <text class="action-icon">🎁</text>
          <text class="action-text">积分商城</text>
        </view>
      </view>
    </view>

    <!-- 公告通知 -->
    <view class="card" v-if="notices.length">
      <view class="card-header flex-between">
        <text class="card-title">公告通知</text>
        <text class="text-primary">查看全部</text>
      </view>
      <view v-for="notice in notices" :key="notice.id" class="notice-item">
        <text class="notice-title">{{ notice.title }}</text>
        <text class="notice-date text-muted">{{ notice.date }}</text>
      </view>
    </view>
  </view>
</template>

<script>
import { getUserStore } from '../../store/user'
import { getSchedules } from '../../api/schedule'

export default {
  data() {
    return {
      userInfo: {},
      isParent: false,
      children: [],
      currentChild: null,
      todayCourse: null,
      weekSchedules: [],
      pendingCheckins: 0,
      pendingHomework: 0,
      pendingMakeup: 0,
      notices: []
    }
  },
  computed: {
    roleName() {
      const map = { student: '学员', parent: '家长', teacher: '教师', admin: '管理' }
      return map[this.userInfo.role] || '学员'
    }
  },
  onLoad() {
    this.loadData()
  },
  onShow() {
    this.loadData()
  },
  methods: {
    async loadData() {
      const store = getUserStore()
      this.userInfo = store.state.userInfo || {}
      this.isParent = store.state.role === 'parent'
      this.children = store.state.children
      this.currentChild = store.state.currentChild

      try {
        // 加载今日和本周课表
        const today = this.formatDate(new Date())
        const weekEnd = this.formatDate(new Date(Date.now() + 7 * 86400000))
        const res = await getSchedules({ startDate: today, endDate: weekEnd })
        if (res && res.schedules) {
          this.processSchedules(res.schedules)
        }

        // TODO: 加载待办数量
        // TODO: 加载公告
      } catch (e) {
        console.error('加载首页数据失败', e)
      }
    },
    processSchedules(schedules) {
      const today = this.formatDate(new Date())
      this.todayCourse = schedules.find(s => s.date === today) || null
      this.weekSchedules = this.buildWeekView(schedules)
    },
    buildWeekView(schedules) {
      const days = []
      const weekLabels = ['日', '一', '二', '三', '四', '五', '六']
      for (let i = 0; i < 7; i++) {
        const d = new Date(Date.now() + i * 86400000)
        const dateStr = this.formatDate(d)
        days.push({
          label: `${dateStr.slice(5)} 周${weekLabels[d.getDay()]}`,
          date: dateStr,
          courses: schedules.filter(s => s.date === dateStr)
        })
      }
      return days
    },
    formatDate(d) {
      return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}`
    },
    switchChild(child) {
      getUserStore().switchChild(child)
      this.currentChild = child
      this.loadData()
    },
    goSchedule() { uni.switchTab({ url: '/pages/schedule/schedule' }) },
    goCourseDetail(id) { uni.navigateTo({ url: `/pages/schedule/detail?id=${id}` }) },
    goCheckin() { uni.navigateTo({ url: '/pages/checkin/checkin' }) },
    goLeave() { uni.navigateTo({ url: '/pages/leave/apply' }) },
    goHours() { uni.navigateTo({ url: '/pages/hours/detail' }) },
    goHomework() { uni.showToast({ title: '功能开发中', icon: 'none' }) },
    goPoints() { uni.navigateTo({ url: '/pages/points/mall' }) }
  }
}
</script>

<style scoped>
.header-card { padding: 24rpx; }
.avatar { width: 80rpx; height: 80rpx; border-radius: 50%; }
.name { font-size: 32rpx; font-weight: 600; display: block; }
.grade { font-size: 24rpx; }
.role-tag { background: #e6f7ff; color: #1890ff; padding: 4rpx 16rpx; border-radius: 8rpx; font-size: 22rpx; }
.child-scroll { white-space: nowrap; padding: 10rpx 0; }
.child-item { display: inline-flex; flex-direction: column; align-items: center; margin-right: 24rpx; padding: 8rpx; }
.child-item.active { background: #e6f7ff; border-radius: 12rpx; }
.child-avatar { width: 60rpx; height: 60rpx; border-radius: 50%; }
.child-name { font-size: 22rpx; margin-top: 4rpx; }
.card-title { font-size: 30rpx; font-weight: 600; }
.card-header { margin-bottom: 16rpx; }
.course-name { font-size: 32rpx; font-weight: 600; display: block; margin-bottom: 12rpx; }
.course-meta text { display: block; margin-bottom: 6rpx; }
.course-actions { margin-top: 16rpx; display: flex; gap: 16rpx; }
.btn-sm { padding: 12rpx 24rpx; font-size: 24rpx; }
.btn-outline { background: #fff; color: #1890ff; border: 1px solid #1890ff; border-radius: 8rpx; }
.todo-grid { display: flex; justify-content: space-around; }
.todo-item { text-align: center; font-size: 24rpx; }
.todo-badge { width: 48rpx; height: 48rpx; border-radius: 50%; display: flex; align-items: center; justify-content: center; color: #fff; font-size: 24rpx; font-weight: 600; margin: 0 auto 8rpx; }
.todo-badge.pending { background: #ff4d4f; }
.todo-badge.warning { background: #faad14; }
.todo-badge.info { background: #1890ff; }
.action-grid { display: flex; flex-wrap: wrap; }
.action-item { width: 20%; text-align: center; padding: 16rpx 0; }
.action-icon { font-size: 40rpx; display: block; }
.action-text { font-size: 22rpx; color: #666; margin-top: 4rpx; }
.week-preview { }
.week-day { display: flex; align-items: center; padding: 10rpx 0; border-bottom: 1px solid #f0f0f0; }
.day-label { width: 140rpx; font-size: 24rpx; color: #666; }
.day-courses { flex: 1; }
.day-course-item { display: flex; gap: 12rpx; padding: 4rpx 0; }
.course-time { color: #1890ff; font-size: 22rpx; white-space: nowrap; }
.course-title-text { font-size: 24rpx; }
.no-course { color: #ccc; }
.ml-16 { margin-left: 16rpx; }
.notice-item { padding: 12rpx 0; border-bottom: 1px solid #f0f0f0; }
.notice-title { font-size: 26rpx; display: block; }
.notice-date { font-size: 22rpx; }
.empty-card { text-align: center; padding: 40rpx; }
</style>
