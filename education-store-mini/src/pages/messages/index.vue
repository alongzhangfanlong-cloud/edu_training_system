<template>
  <view class="messages-page">
    <!-- notification preferences -->
    <view class="section">
      <view class="section-title">通知偏好设置</view>
      <view class="setting-list">
        <view v-for="item in settings" :key="item.key" class="setting-item">
          <view class="setting-info">
            <text class="setting-label">{{ item.label }}</text>
            <text class="setting-desc">{{ item.desc }}</text>
          </view>
          <switch
            :checked="item.value"
            :disabled="saving"
            @change="(e) => onToggle(item.key, e.detail.value)"
            color="#409eff"
          />
          <text class="subscribe-btn" @click.stop="handleSubscribe(item.key)">订阅</text>
        </view>
      </view>
    </view>

  </view>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { getNotificationSettings, updateNotificationSettings } from '@/api/notification';
import { subscribeOne } from '@/utils/subscribe';

const saving = ref(false);

const settingDefs = [
  { key: 'SIGN_IN', label: '签到通知', desc: '孩子上课签到后推送' },
  { key: 'EVALUATION', label: '评价通知', desc: '收到课堂评价时推送' },
  { key: 'HOMEWORK', label: '作业通知', desc: '新作业发布时推送' },
  { key: 'EXAM', label: '成绩通知', desc: '新成绩录入时推送' },
  { key: 'LEAVE', label: '请假通知', desc: '请假审批结果推送' },
  { key: 'REDEMPTION', label: '兑换通知', desc: '积分兑换状态推送' },
];

const settings = reactive(settingDefs.map((d) => ({ ...d, value: true })));

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
  const patch = { [key]: value };
  try {
    const res = await updateNotificationSettings(patch);
    if (res.code === 200) {
      const item = settings.find((s) => s.key === key);
      if (item) item.value = value;
    }
  } catch { /* ignore */ }
  saving.value = false;
}

async function handleSubscribe(key) {
  const ok = await subscribeOne(key);
  if (ok) {
    uni.showToast({ title: '订阅成功', icon: 'success' });
  } else {
    uni.showToast({ title: '已跳过', icon: 'none' });
  }
}

onMounted(() => { loadSettings(); });
</script>

<style scoped lang="scss">
.messages-page { padding: 24rpx; min-height: 100vh; }
.section { background: #fff; border-radius: 12rpx; margin-bottom: 24rpx; overflow: hidden; }
.section-title { font-size: 30rpx; font-weight: 600; color: #303133; padding: 24rpx 24rpx 0; margin-bottom: 16rpx; }
.setting-item { display: flex; justify-content: space-between; align-items: center; padding: 24rpx; border-bottom: 1rpx solid #f0f2f5; }
.setting-item:last-child { border-bottom: none; }
.setting-info { flex: 1; }
.setting-label { font-size: 28rpx; color: #303133; }
.setting-desc { font-size: 24rpx; color: #909399; margin-top: 4rpx; display: block; }
.subscribe-btn {
  font-size: 24rpx; color: #409eff; padding: 8rpx 16rpx;
  border: 1rpx solid #409eff; border-radius: 8rpx; margin-left: 16rpx; flex-shrink: 0;
}
</style>
