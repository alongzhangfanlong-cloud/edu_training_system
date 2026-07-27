<template>
  <view class="hw-form-page">
    <view class="form-card">
      <view class="form-item">
        <text class="form-label">作业内容</text>
        <textarea v-model="form.content" class="form-textarea" placeholder="请输入作业内容（必填）" maxlength="5000" />
        <text class="form-count">{{ form.content.length }}/5000</text>
      </view>
    </view>

    <view class="submit-wrap">
      <button class="submit-btn" type="primary" :loading="submitting" @click="handleSubmit">发布作业</button>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { createHomework } from '@/api/teacher-evaluation';
import { useAuthStore } from '@/stores/auth';

const authStore = useAuthStore();
const submitting = ref(false);

const form = reactive({ content: '' });

async function handleSubmit() {
  if (!form.content.trim()) { uni.showToast({ title: '请输入作业内容', icon: 'none' }); return; }
  submitting.value = true;
  try {
    const res = await createHomework({ content: form.content, teacherId: authStore.userInfo?.userId });
    if (res.code === 200) {
      uni.showToast({ title: '发布成功', icon: 'success' });
      setTimeout(() => uni.navigateBack(), 1500);
    } else {
      uni.showToast({ title: res.msg || '发布失败', icon: 'none' });
    }
  } catch { uni.showToast({ title: '网络错误', icon: 'none' }); }
  finally { submitting.value = false; }
}
</script>

<style scoped lang="scss">
.hw-form-page { padding: 24rpx; min-height: 100vh; }
.form-card { background: #fff; border-radius: 12rpx; padding: 24rpx; margin-bottom: 48rpx; }
.form-item { }
.form-label { font-size: 28rpx; color: #303133; margin-bottom: 12rpx; display: block; }
.form-textarea { width: 100%; height: 300rpx; padding: 16rpx; background: #f5f7fa; border-radius: 8rpx; font-size: 28rpx; box-sizing: border-box; }
.form-count { text-align: right; font-size: 22rpx; color: #c0c4cc; margin-top: 4rpx; display: block; }
.submit-btn { width: 100%; height: 88rpx; line-height: 88rpx; background: #409eff; border-radius: 44rpx; font-size: 32rpx; border: none; }
.submit-btn::after { border: none; }
</style>
