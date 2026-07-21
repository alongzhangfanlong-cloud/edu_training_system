<template>
  <view class="submit-page">
    <view class="form-card">
      <view class="form-item"><text class="form-label">请假日期</text>
        <picker mode="date" :value="form.leaveDate" @change="onDateChange"><view class="form-picker">{{ form.leaveDate || '请选择日期' }}</view></picker>
      </view>
      <view class="form-item"><text class="form-label">请假类型</text>
        <picker mode="selector" :range="leaveTypes" :value="typeIndex" @change="onTypeChange"><view class="form-picker">{{ leaveTypes[typeIndex] }}</view></picker>
      </view>
      <view class="form-item"><text class="form-label">请假原因</text>
        <textarea v-model="form.reason" class="form-textarea" placeholder="请输入请假原因（选填）" maxlength="200" />
      </view>
    </view>
    <view class="submit-btn-wrap"><button class="submit-btn" type="primary" :loading="submitting" @click="handleSubmit">提交请假申请</button></view>
  </view>
</template>
<script setup>
import { ref, reactive } from 'vue'; import { onLoad } from '@dcloudio/uni-app'; import { submitLeave } from '@/api/leave';
let studentId = null; const submitting = ref(false); const leaveTypes = ['事假', '病假', '其他']; const typeIndex = ref(0);
const form = reactive({ leaveDate: '', leaveType: 1, reason: '' });
onLoad((options) => { studentId = options.studentId; });
function onDateChange(e) { form.leaveDate = e.detail.value; }
function onTypeChange(e) { typeIndex.value = e.detail.value; form.leaveType = e.detail.value + 1; }
async function handleSubmit() {
  if (!form.leaveDate) { uni.showToast({ title: '请选择请假日期', icon: 'none' }); return; }
  if (!studentId) { uni.showToast({ title: '参数错误', icon: 'none' }); return; }
  submitting.value = true;
  try { const res = await submitLeave(studentId, { leaveDate: form.leaveDate, leaveType: form.leaveType, reason: form.reason });
    if (res.code === 200) { uni.showToast({ title: '提交成功', icon: 'success' }); setTimeout(() => uni.navigateBack(), 1500); }
    else { uni.showToast({ title: res.msg || '提交失败', icon: 'none' }); }
  } catch { uni.showToast({ title: '提交失败，请检查网络', icon: 'none' }); }
  finally { submitting.value = false; }
}
</script>
<style scoped lang="scss">
.submit-page { padding: 24rpx; min-height: 100vh; } .form-card { background: #fff; border-radius: 12rpx; padding: 24rpx; }
.form-item { margin-bottom: 32rpx; } .form-label { font-size: 28rpx; color: #303133; margin-bottom: 12rpx; display: block; }
.form-picker { padding: 20rpx 16rpx; background: #f5f7fa; border-radius: 8rpx; font-size: 28rpx; color: #303133; }
.form-textarea { width: 100%; height: 200rpx; padding: 20rpx 16rpx; background: #f5f7fa; border-radius: 8rpx; font-size: 28rpx; box-sizing: border-box; }
.submit-btn-wrap { margin-top: 48rpx; } .submit-btn { width: 100%; height: 88rpx; line-height: 88rpx; background: #409eff; border-radius: 44rpx; font-size: 32rpx; color: #fff; } .submit-btn::after { border: none; }
</style>
