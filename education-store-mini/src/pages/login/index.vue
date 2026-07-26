<template>
  <view class="login-page">
    <view class="login-card">
      <image class="login-logo" src="/static/logo.png" mode="aspectFit" />
      <text class="login-title">教育管理平台</text>
      <text class="login-desc">家校互通 · 课程管理 · 成长记录</text>

      <view class="form-group">
        <input
          v-model="form.username"
          class="form-input"
          placeholder="请输入用户名"
          placeholder-class="input-placeholder"
        />
      </view>
      <view class="form-group">
        <input
          v-model="form.password"
          class="form-input"
          type="password"
          placeholder="请输入密码"
          placeholder-class="input-placeholder"
        />
      </view>
      <view
        class="login-btn"
        :class="{ 'login-btn-disabled': loading }"
        @click="handlePasswordLogin"
        @tap="handlePasswordLogin"
      >
        <text v-if="!loading">登录</text>
        <text v-else>登录中...</text>
      </view>
      <view class="demo-accounts">
        <text class="demo-title">演示账号：</text>
        <text class="demo-item" @click="fillAccount('teacher01','123456')">教师 teacher01/123456</text>
        <text class="demo-item" @click="fillAccount('parent01','123456')">家长 parent01/123456</text>
        <text class="demo-item" @click="fillAccount('admin','123456')">管理员 admin/123456</text>
      </view>

      <text class="login-tip">点击登录即表示同意用户协议和隐私政策</text>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { useChildrenStore } from '@/stores/children';
import { useTeacherStore } from '@/stores/teacher';

const loading = ref(false);
const form = reactive({ username: '', password: '' });
const authStore = useAuthStore();
const childrenStore = useChildrenStore();
const teacherStore = useTeacherStore();

function fillAccount(username, password) {
  form.username = username;
  form.password = password;
}

async function navigateAfterLogin(role) {
  if (role === 'TEACHER') {
    await teacherStore.fetchDashboard();
    uni.reLaunch({ url: '/pages/teacher/schedule/index' });
  } else {
    await childrenStore.fetchChildren();
    uni.reLaunch({ url: '/pages/index/index' });
  }
}

async function handlePasswordLogin() {
  if (!form.username || !form.password) {
    uni.showToast({ title: '请输入用户名和密码', icon: 'none' });
    return;
  }
  if (loading.value) return;
  loading.value = true;
  try {
    const data = await authStore.loginWithPassword(form.username, form.password);
    await navigateAfterLogin(data.role);
  } catch (e) {
    console.error('登录失败:', e);
    uni.showToast({ title: e.message || '登录失败，请检查账号密码', icon: 'none' });
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped lang="scss">
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40rpx;
}
.login-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 80rpx 64rpx;
  width: 100%;
  max-width: 600rpx;
  background: #fff;
  border-radius: 24rpx;
  box-shadow: 0 16rpx 48rpx rgba(0, 0, 0, 0.12);
}
.login-logo { width: 160rpx; height: 160rpx; margin-bottom: 32rpx; }
.login-title { font-size: 40rpx; font-weight: 700; color: #303133; margin-bottom: 8rpx; }
.login-desc { font-size: 26rpx; color: #909399; margin-bottom: 48rpx; }

.form-group {
  width: 100%;
  margin-bottom: 24rpx;
}
.form-input {
  width: 100%;
  height: 44px;
  padding: 0 12px;
  background: #f5f7fa;
  border-radius: 6px;
  font-size: 15px;
  box-sizing: border-box;
  border: none;
  outline: none;
}
.input-placeholder {
  color: #c0c4cc;
  font-size: 15px;
}
.login-btn {
  width: 100%;
  height: 44px;
  line-height: 44px;
  border-radius: 22px;
  background: linear-gradient(135deg, #667eea, #409eff);
  font-size: 16px;
  color: #fff;
  margin-top: 8px;
  text-align: center;
  cursor: pointer;
  user-select: none;
}
.login-btn-disabled {
  opacity: 0.6;
  pointer-events: none;
}

.demo-accounts {
  width: 100%;
  margin-top: 32rpx;
  display: flex;
  flex-direction: column;
  gap: 12rpx;
}
.demo-title {
  font-size: 24rpx;
  color: #909399;
}
.demo-item {
  font-size: 24rpx;
  color: #409eff;
  padding: 8rpx 0;
}

.login-tip { font-size: 22rpx; color: #c0c4cc; margin-top: 32rpx; text-align: center; }
</style>
