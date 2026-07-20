<!-- 教师工作台 — 欢迎区 -->
<template>
  <div class="welcome-bar">
    <div class="welcome-info">
      <h2 class="welcome-greeting">👋 欢迎回来，{{ userName }}老师</h2>
      <p class="welcome-date">{{ serverDateFormatted || '--' }}</p>
      <p v-if="lastLoginTime" class="welcome-login">上次登录：{{ lastLoginTime }}</p>
      <p v-else-if="isFirstLogin" class="welcome-login welcome-first">🎉 欢迎首次登录！</p>
    </div>
    <el-button @click="$emit('refresh')" :loading="refreshing" :icon="Refresh" circle />
  </div>
</template>

<script setup>
import { Refresh } from '@element-plus/icons-vue';

defineProps({
  userName: { type: String, default: '--' },
  serverDateFormatted: { type: String, default: '' },
  lastLoginTime: { type: String, default: '' },
  isFirstLogin: { type: Boolean, default: false },
  refreshing: { type: Boolean, default: false },
});

defineEmits(['refresh']);
</script>

<style scoped lang="less">
.welcome-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  margin-bottom: 16px;
  background: linear-gradient(135deg, #409eff 0%, #337ecc 100%);
  border-radius: 8px;
  color: #fff;

  .welcome-greeting { margin: 0 0 6px; font-size: 22px; font-weight: 600; }
  .welcome-date { margin: 0 0 2px; font-size: 14px; opacity: 0.9; }
  .welcome-login { margin: 0; font-size: 12px; opacity: 0.75; }
  .welcome-first { opacity: 1; font-size: 13px; }
}
</style>
