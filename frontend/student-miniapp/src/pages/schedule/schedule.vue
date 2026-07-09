<template>
  <view class="container">
    <!-- 视图切换 -->
    <view class="view-switcher flex-row">
      <view :class="['switch-btn', { active: viewMode === 'week' }]" @click="viewMode = 'week'">周视图</view>
      <view :class="['switch-btn', { active: viewMode === 'month' }]" @click="viewMode = 'month'">月视图</view>
    </view>

    <!-- 周视图 -->
    <view v-if="viewMode === 'week'" class="week-view">
      <view class="week-header flex-between">
        <text class="arrow" @click="prevWeek">◀</text>
        <text class="week-title">{{ weekRange }}</text>
        <text class="arrow" @click="nextWeek">▶</text>
      </view>
      <scroll-view scroll-x class="week-scroll">
        <view class="week-grid">
          <view v-for="(day, idx) in weekDays" :key="idx" class="week-column"
                :class="{ today: day.isToday }" @click="selectDate(day.date)">
            <text class="day-name">{{ day.name }}</text>
            <text class="day-date">{{ day.dateStr }}</text>
            <view v-for="course in day.courses" :key="course.id" class="mini-course"
                  :style="{ background: getCourseColor(course) }"
                  @click.stop="goDetail(course.id)">
              <text class="mini-name">{{ course.courseName }}</text>
              <text class="mini-time">{{ course.startTime }}</text>
            </view>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 月视图 -->
    <view v-if="viewMode === 'month'" class="month-view">
      <view class="week-header flex-between">
        <text class="arrow" @click="prevMonth">◀</text>
        <text class="week-title">{{ monthTitle }}</text>
        <text class="arrow" @click="nextMonth">▶</text>
      </view>
      <view class="month-grid">
        <view v-for="d in monthDays" :key="d.key" class="month-cell"
              :class="{ today: d.isToday, 'other-month': !d.isCurrentMonth, 'has-course': d.courses.length }"
              @click="d.isCurrentMonth && selectDate(d.date)">
          <text class="cell-date">{{ d.day }}</text>
          <view v-if="d.courses.length" class="cell-dots">
            <view v-for="c in d.courses.slice(0,3)" :key="c.id" class="dot" :style="{ background: getCourseColor(c) }"></view>
          </view>
        </view>
      </view>
    </view>

    <!-- 当日课程列表 -->
    <view class="course-list">
      <text class="section-title">{{ selectedDate }} 课程</text>
      <view v-if="selectedCourses.length === 0" class="empty">当日无课程</view>
      <view v-for="course in selectedCourses" :key="course.id" class="course-card card" @click="goDetail(course.id)">
        <view class="course-header flex-between">
          <text class="course-title">{{ course.courseName }}</text>
          <text :class="['status-tag', 'status-' + course.status]">{{ statusMap[course.status] || '正常' }}</text>
        </view>
        <view class="course-body">
          <view class="info-row">🕐 {{ course.startTime }} - {{ course.endTime }}</view>
          <view class="info-row">👨‍🏫 {{ course.teacherName || '待定' }}</view>
          <view class="info-row">📍 {{ course.classroomName || '待定' }}</view>
        </view>
        <view class="course-footer">
          <button v-if="canCheckin(course)" class="btn-primary btn-xs" @click.stop="goCheckin(course.id)">签到</button>
          <button v-if="canLeave(course)" class="btn-outline btn-xs" @click.stop="goLeave(course.id)">请假</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { getSchedules } from '../../api/schedule'

export default {
  data() {
    return {
      viewMode: 'week',
      currentWeekStart: new Date(),
      currentMonth: new Date(),
      schedules: [],
      selectedDate: '',
      statusMap: { 0:'已取消', 1:'正常', 2:'已调课', 3:'补课', 4:'停课' }
    }
  },
  computed: {
    weekRange() {
      const end = new Date(this.currentWeekStart)
      end.setDate(end.getDate() + 6)
      return `${this.fmt(this.currentWeekStart)} - ${this.fmt(end)}`
    },
    weekDays() {
      const days = []
      const today = this.fmt(new Date())
      for (let i = 0; i < 7; i++) {
        const d = new Date(this.currentWeekStart)
        d.setDate(d.getDate() + i)
        const ds = this.fmt(d)
        days.push({
          name: ['日','一','二','三','四','五','六'][d.getDay()],
          dateStr: `${d.getMonth()+1}/${d.getDate()}`,
          date: ds,
          isToday: ds === today,
          courses: this.schedules.filter(s => s.date === ds)
        })
      }
      return days
    },
    monthTitle() {
      return `${this.currentMonth.getFullYear()}年${this.currentMonth.getMonth()+1}月`
    },
    monthDays() {
      const year = this.currentMonth.getFullYear()
      const month = this.currentMonth.getMonth()
      const firstDay = new Date(year, month, 1)
      const startDow = firstDay.getDay()
      const daysInMonth = new Date(year, month+1, 0).getDate()
      const today = this.fmt(new Date())
      const days = []
      for (let i = 0; i < startDow; i++) {
        const d = new Date(year, month, -startDow + i + 1)
        days.push({ key: `prev-${i}`, day: d.getDate(), date: this.fmt(d), isCurrentMonth: false, isToday: false, courses: [] })
      }
      for (let i = 1; i <= daysInMonth; i++) {
        const d = new Date(year, month, i)
        const ds = this.fmt(d)
        days.push({ key: `cur-${i}`, day: i, date: ds, isCurrentMonth: true, isToday: ds === today, courses: this.schedules.filter(s => s.date === ds) })
      }
      return days
    },
    selectedCourses() {
      return this.schedules.filter(s => s.date === this.selectedDate)
    }
  },
  onLoad() {
    this.selectedDate = this.fmt(new Date())
    this.loadSchedules()
  },
  methods: {
    fmt(d) { return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}` },
    async loadSchedules() {
      try {
        const start = this.fmt(new Date(this.currentWeekStart.getTime() - 14*86400000))
        const end = this.fmt(new Date(this.currentWeekStart.getTime() + 35*86400000))
        const res = await getSchedules({ startDate: start, endDate: end })
        if (res && res.schedules) this.schedules = res.schedules
      } catch (e) { console.error(e) }
    },
    selectDate(date) { this.selectedDate = date },
    prevWeek() { this.currentWeekStart = new Date(this.currentWeekStart.getTime() - 7*86400000); this.loadSchedules() },
    nextWeek() { this.currentWeekStart = new Date(this.currentWeekStart.getTime() + 7*86400000); this.loadSchedules() },
    prevMonth() { this.currentMonth = new Date(this.currentMonth.getFullYear(), this.currentMonth.getMonth()-1, 1) },
    nextMonth() { this.currentMonth = new Date(this.currentMonth.getFullYear(), this.currentMonth.getMonth()+1, 1) },
    canCheckin(c) { return (c.status === 1 || c.status === 3) && c.date === this.fmt(new Date()) },
    canLeave(c) { return c.status === 1 && new Date(`${c.date}T${c.startTime}`) > new Date() },
    getCourseColor(c) {
      const colors = ['#1890ff','#52c41a','#faad14','#722ed1','#eb2f96','#13c2c2']
      return colors[Math.abs(c.courseName || '').split('').reduce((a,b)=>a+b.charCodeAt(0),0) % colors.length]
    },
    goDetail(id) { uni.navigateTo({ url: `/pages/schedule/detail?id=${id}` }) },
    goCheckin(sid) { uni.navigateTo({ url: `/pages/checkin/checkin?scheduleId=${sid}` }) },
    goLeave(sid) { uni.navigateTo({ url: `/pages/leave/apply?scheduleId=${sid}` }) }
  }
}
</script>

<style scoped>
.view-switcher { background: #fff; border-radius: 8rpx; overflow: hidden; margin-bottom: 16rpx; }
.switch-btn { flex: 1; text-align: center; padding: 16rpx; font-size: 26rpx; }
.switch-btn.active { background: #1890ff; color: #fff; }
.week-header { padding: 16rpx 0; }
.week-title { font-size: 28rpx; font-weight: 600; }
.arrow { font-size: 32rpx; padding: 8rpx; }
.week-scroll { white-space: nowrap; }
.week-grid { display: inline-flex; min-width: 100%; }
.week-column { width: 120rpx; text-align: center; padding: 8rpx 4rpx; border-right: 1px solid #f0f0f0; }
.week-column.today { background: #e6f7ff; }
.day-name { font-size: 22rpx; color: #999; display: block; }
.day-date { font-size: 28rpx; font-weight: 600; display: block; margin: 4rpx 0; }
.mini-course { padding: 4rpx 2rpx; margin: 2rpx 0; border-radius: 4rpx; }
.mini-name { font-size: 18rpx; color: #fff; display: block; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.mini-time { font-size: 16rpx; color: rgba(255,255,255,0.8); }
.month-grid { display: flex; flex-wrap: wrap; }
.month-cell { width: 14.28%; text-align: center; padding: 8rpx 0; }
.month-cell.today { background: #e6f7ff; border-radius: 8rpx; }
.month-cell.other-month .cell-date { color: #ddd; }
.cell-date { font-size: 24rpx; }
.cell-dots { display: flex; justify-content: center; gap: 4rpx; margin-top: 4rpx; }
.dot { width: 10rpx; height: 10rpx; border-radius: 50%; }
.section-title { font-size: 28rpx; font-weight: 600; padding: 20rpx 0 12rpx; }
.course-card { }
.course-title { font-size: 30rpx; font-weight: 600; }
.status-tag { font-size: 22rpx; padding: 2rpx 12rpx; border-radius: 4rpx; }
.status-1 { background: #f6ffed; color: #52c41a; }
.status-0 { background: #fff2f0; color: #ff4d4f; }
.status-2 { background: #fff7e6; color: #faad14; }
.course-body { margin: 12rpx 0; }
.info-row { font-size: 24rpx; color: #666; padding: 4rpx 0; }
.course-footer { display: flex; gap: 12rpx; }
.btn-xs { padding: 8rpx 20rpx; font-size: 22rpx; }
.btn-outline { background: #fff; color: #1890ff; border: 1px solid #1890ff; border-radius: 8rpx; }
.empty { text-align: center; color: #999; padding: 40rpx; }
</style>
