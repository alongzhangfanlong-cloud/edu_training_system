<template>
  <view class="container">
    <!-- 未登录 -->
    <view v-if="!isLoggedIn" class="login-card card">
      <image class="login-avatar" src="/static/images/default-avatar.png" mode="aspectFill" />
      <text class="login-title">欢迎使用教育培训</text>
      <text class="login-sub">登录后查看课程与课时</text>
      <button class="btn-primary" @click="showLogin = true">手机号登录</button>

      <!-- 登录弹窗 -->
      <view v-if="showLogin" class="login-modal">
        <view class="modal-mask" @click="showLogin = false"></view>
        <view class="modal-content">
          <text class="modal-title">手机号登录</text>
          <input v-model="phone" class="input" placeholder="请输入手机号" type="number" maxlength="11" />
          <view class="sms-row">
            <input v-model="code" class="input flex-1" placeholder="验证码" type="number" maxlength="6" />
            <button class="sms-btn" :disabled="smsCountdown > 0" @click="sendSmsCode">
              {{ smsCountdown > 0 ? smsCountdown + 's' : '获取验证码' }}
            </button>
          </view>
          <view class="role-select">
            <text class="label">选择角色:</text>
            <view class="role-options">
              <view v-for="r in roles" :key="r.value"
                    :class="['role-opt', { active: role === r.value }]"
                    @click="role = r.value">{{ r.label }}</view>
            </view>
          </view>
          <button class="btn-primary" :disabled="loggingIn" @click="doLogin">
            {{ loggingIn ? '登录中...' : '登录' }}
          </button>
          <text class="dev-tip text-muted">开发环境验证码: 123456</text>
        </view>
      </view>
    </view>

    <!-- 已登录 -->
    <template v-else>
      <!-- 用户信息卡片 -->
      <view class="user-card card">
        <view class="flex-row">
          <image class="avatar" :src="userInfo.avatar || '/static/images/default-avatar.png'" mode="aspectFill" />
          <view class="flex-1 ml-16">
            <text class="user-name">{{ userInfo.name }}</text>
            <text class="user-role">{{ roleLabel }}</text>
          </view>
          <text class="text-muted" @click="goSettings">⚙️</text>
        </view>
      </view>

      <!-- 课时余额卡片 -->
      <view class="card hour-card">
        <text class="card-title">课时余额</text>
        <view v-if="hourList.length" class="hour-list">
          <view v-for="h in hourList" :key="h.classId" class="hour-item">
            <view class="flex-between">
              <text class="hour-course">{{ h.courseName }}</text>
              <text :class="['hour-remain', h.remainHours <= 5 ? 'text-danger' : 'text-primary']">
                {{ h.remainHours }} / {{ h.totalHours }} 课时
              </text>
            </view>
            <view class="progress-bar">
              <view class="progress-fill" :style="{ width: progressWidth(h) }"></view>
            </view>
          </view>
        </view>
        <view v-else class="empty-hours">
          <text class="text-muted">暂无在读课程</text>
        </view>
        <text class="view-detail text-primary" @click="goHours">查看明细 →</text>
      </view>

      <!-- 功能列表 -->
      <view class="card menu-list">
        <view class="menu-item" @click="goOrderList">
          <text class="menu-icon">📋</text>
          <text class="menu-text">我的订单</text>
          <text class="menu-arrow">›</text>
        </view>
        <view class="menu-item" @click="goHours">
          <text class="menu-icon">📊</text>
          <text class="menu-text">课时明细</text>
          <text class="menu-arrow">›</text>
        </view>
        <view class="menu-item" @click="goReport">
          <text class="menu-icon">📝</text>
          <text class="menu-text">成长报告</text>
          <text class="menu-arrow">›</text>
        </view>
        <view class="menu-item" @click="goPoints">
          <text class="menu-icon">🏆</text>
          <text class="menu-text">积分商城</text>
          <text class="menu-arrow">›</text>
        </view>
        <view class="menu-item" @click="goSettings">
          <text class="menu-icon">⚙️</text>
          <text class="menu-text">设置</text>
          <text class="menu-arrow">›</text>
        </view>
      </view>

      <!-- 家长端特有：孩子管理 -->
      <view class="card menu-list" v-if="isParent">
        <view class="menu-item" @click="manageChildren">
          <text class="menu-icon">👨‍👩‍👧</text>
          <text class="menu-text">孩子管理</text>
          <text class="menu-arrow">›</text>
        </view>
        <view class="menu-item" @click="goExpense">
          <text class="menu-icon">💰</text>
          <text class="menu-text">消费记录</text>
          <text class="menu-arrow">›</text>
        </view>
      </view>

      <!-- 退出登录 -->
      <view class="logout-btn" @click="doLogout">退出登录</view>
    </template>
  </view>
</template>

<script>
import { getUserStore } from '../../store/user'
import { sendSms, login, logout } from '../../api/auth'
import { getStudentDetail } from '../../api/student'

export default {
  data() {
    return {
      isLoggedIn: false,
      showLogin: false,
      phone: '',
      code: '',
      role: 'student',
      smsCountdown: 0,
      loggingIn: false,
      userInfo: {},
      hourList: [],
      roles: [
        { label: '学员', value: 'student' },
        { label: '家长', value: 'parent' },
        { label: '教师', value: 'teacher' },
        { label: '管理', value: 'admin' }
      ]
    }
  },
  computed: {
    isParent() { return getUserStore().state.role === 'parent' },
    roleLabel() {
      const map = { student: '学员', parent: '家长', teacher: '教师', admin: '管理员' }
      return map[this.userInfo.role] || '学员'
    }
  },
  onShow() {
    const store = getUserStore()
    this.isLoggedIn = store.state.isLoggedIn
    if (this.isLoggedIn) {
      this.userInfo = store.state.userInfo || {}
      this.loadHours()
    }
  },
  methods: {
    async sendSmsCode() {
      if (!this.phone || this.phone.length !== 11) {
        return uni.showToast({ title: '请输入正确手机号', icon: 'none' })
      }
      try {
        await sendSms(this.phone)
        uni.showToast({ title: '验证码已发送', icon: 'success' })
        this.smsCountdown = 60
        const timer = setInterval(() => {
          this.smsCountdown--
          if (this.smsCountdown <= 0) clearInterval(timer)
        }, 1000)
      } catch(e) { uni.showToast({ title: '发送失败', icon: 'none' }) }
    },
    async doLogin() {
      if (!this.phone || !this.code) {
        return uni.showToast({ title: '请输入手机号和验证码', icon: 'none' })
      }
      this.loggingIn = true
      try {
        const data = await login(this.phone, this.code, this.role)
        const store = getUserStore()
        store.setUser(data.user)
        this.isLoggedIn = true
        this.userInfo = data.user
        this.showLogin = false
        uni.showToast({ title: '登录成功', icon: 'success' })
        this.loadHours()
      } catch(e) {
        uni.showToast({ title: e.message || '登录失败', icon: 'none' })
      } finally { this.loggingIn = false }
    },
    async loadHours() {
      try {
        const detail = await getStudentDetail(this.userInfo.id)
        if (detail && detail.courses) this.hourList = detail.courses
      } catch(e) { console.error(e) }
    },
    progressWidth(h) {
      if (!h.totalHours || h.totalHours == 0) return '0%'
      return Math.max(0, Math.min(100, ((h.totalHours - h.remainHours) / h.totalHours * 100))) + '%'
    },
    async doLogout() {
      try { await logout() } catch(e) {}
      getUserStore().logout()
      this.isLoggedIn = false
      this.userInfo = {}
      this.hourList = []
      uni.showToast({ title: '已退出', icon: 'none' })
    },
    goHours() { uni.navigateTo({ url: '/pages/hours/detail' }) },
    goOrderList() { uni.navigateTo({ url: '/pages/order/list' }) },
    goReport() { uni.navigateTo({ url: '/pages/report/growth' }) },
    goPoints() { uni.navigateTo({ url: '/pages/points/mall' }) },
    goSettings() { uni.navigateTo({ url: '/pages/settings/index' }) },
    goExpense() { uni.showToast({ title: '功能开发中', icon: 'none' }) },
    manageChildren() { uni.showToast({ title: '功能开发中', icon: 'none' }) }
  }
}
</script>

<style scoped>
.login-card { text-align: center; padding: 60rpx 40rpx; }
.login-avatar { width: 120rpx; height: 120rpx; border-radius: 50%; margin-bottom: 20rpx; }
.login-title { font-size: 34rpx; font-weight: 600; display: block; }
.login-sub { font-size: 26rpx; color: #999; display: block; margin: 12rpx 0 32rpx; }
.login-modal { position: fixed; top: 0; left: 0; right: 0; bottom: 0; z-index: 1000; display: flex; align-items: center; justify-content: center; }
.modal-mask { position: absolute; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); }
.modal-content { position: relative; width: 600rpx; background: #fff; border-radius: 16rpx; padding: 40rpx; }
.modal-title { font-size: 32rpx; font-weight: 600; text-align: center; display: block; margin-bottom: 32rpx; }
.input { background: #f5f5f5; border-radius: 8rpx; padding: 20rpx; font-size: 28rpx; margin-bottom: 16rpx; }
.sms-row { display: flex; gap: 16rpx; }
.sms-btn { background: #1890ff; color: #fff; border-radius: 8rpx; padding: 16rpx 20rpx; font-size: 24rpx; white-space: nowrap; border: none; }
.role-select { margin: 16rpx 0; }
.role-select .label { font-size: 26rpx; margin-right: 12rpx; }
.role-options { display: flex; gap: 12rpx; flex-wrap: wrap; margin-top: 8rpx; }
.role-opt { padding: 8rpx 20rpx; border: 1px solid #d9d9d9; border-radius: 8rpx; font-size: 24rpx; }
.role-opt.active { border-color: #1890ff; color: #1890ff; background: #e6f7ff; }
.dev-tip { font-size: 22rpx; text-align: center; display: block; margin-top: 12rpx; }
.user-card { padding: 24rpx; }
.avatar { width: 80rpx; height: 80rpx; border-radius: 50%; }
.user-name { font-size: 32rpx; font-weight: 600; display: block; }
.user-role { font-size: 24rpx; color: #999; }
.ml-16 { margin-left: 16rpx; }
.card-title { font-size: 28rpx; font-weight: 600; display: block; margin-bottom: 16rpx; }
.hour-item { margin-bottom: 16rpx; }
.hour-course { font-size: 26rpx; }
.hour-remain { font-size: 26rpx; font-weight: 600; }
.progress-bar { height: 8rpx; background: #f0f0f0; border-radius: 4rpx; margin-top: 8rpx; overflow: hidden; }
.progress-fill { height: 100%; background: #1890ff; border-radius: 4rpx; transition: width 0.3s; }
.empty-hours { text-align: center; padding: 30rpx 0; }
.view-detail { font-size: 24rpx; display: block; text-align: right; margin-top: 12rpx; }
.menu-list { padding: 0; }
.menu-item { display: flex; align-items: center; padding: 28rpx 24rpx; border-bottom: 1px solid #f5f5f5; }
.menu-item:last-child { border-bottom: none; }
.menu-icon { font-size: 36rpx; margin-right: 16rpx; }
.menu-text { flex: 1; font-size: 28rpx; }
.menu-arrow { font-size: 32rpx; color: #ccc; }
.logout-btn { text-align: center; padding: 24rpx; color: #ff4d4f; font-size: 28rpx; margin-top: 32rpx; }
</style>
