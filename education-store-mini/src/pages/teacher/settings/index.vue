<template>
  <view class="settings-page">
    <view class="section">
      <view class="section-title">通知偏好</view>
      <view v-for="item in settings" :key="item.key" class="setting-item">
        <view class="si-info">
          <text class="si-label">{{ item.label }}</text>
          <text class="si-desc">{{ item.desc }}</text>
        </view>
        <switch :checked="item.value" :disabled="saving" @change="(e) => onToggle(item.key, e.detail.value)" color="#409eff" />
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { getNotificationSettings, updateNotificationSettings } from '@/api/notification';

const saving = ref(false);

const settingDefs = [
  { key: 'SCHEDULE_REMINDER', label: '课表提醒', desc: '上课前推送提醒' },
  { key: 'CHECKIN_REMINDER', label: '签到提醒', desc: '需要签到时推送' },
  { key: 'LEAVE_RESULT', label: '请假审批结果', desc: '审批结果推送' },
  { key: 'EVALUATION_REMINDER', label: '评价提醒', desc: '课后评价提醒' },
  { key: 'SYSTEM_NOTICE', label: '系统通知', desc: '机构公告和系统通知' },
];

const settings = reactive(settingDefs.map(d => ({ ...d, value: true })));

async function loadSettings() {
  try {
    const res = await getNotificationSettings();
    if (res.code === 200 && res.data) {
      for (const item of settings) {
        if (res.data[item.key] !== undefined) item.value = res.data[item.key];
      }
    }
  } catch { /* ignore */ }
}

async function onToggle(key, value) {
  saving.value = true;
  try {
    const res = await updateNotificationSettings({ [key]: value });
    if (res.code === 200) {
      const item = settings.find(s => s.key === key);
      if (item) item.value = value;
    }
  } catch { /* ignore */ }
  saving.value = false;
}

onMounted(() => loadSettings());
</script>

<style scoped lang="scss">
.settings-page { padding: 24rpx; }
.section { background: #fff; border-radius: 12rpx; overflow: hidden; }
.section-title { font-size: 30rpx; font-weight: 600; color: #303133; padding: 24rpx 24rpx 0; margin-bottom: 16rpx; }
.setting-item { display: flex; justify-content: space-between; align-items: center; padding: 24rpx; border-bottom: 1rpx solid #f0f2f5; }
.setting-item:last-child { border-bottom: none; }
.si-label { font-size: 28rpx; color: #303133; display: block; }
.si-desc { font-size: 24rpx; color: #909399; margin-top: 4rpx; display: block; }
</style>
