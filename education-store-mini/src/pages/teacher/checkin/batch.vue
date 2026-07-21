<template>
  <view class="batch-page">
    <view class="batch-header">
      <text class="bh-course">{{ courseInfo.courseName }}</text>
      <text class="bh-class">{{ courseInfo.className }}</text>
      <text class="bh-count">已签 {{ signedCount }} / 应到 {{ students.length }}</text>
    </view>

    <view v-if="loading"><loading-skeleton :rows="4" /></view>
    <view v-else-if="error"><empty-state icon="⚠️" text="加载失败" :show-retry="true" @retry="loadStudents" /></view>
    <view v-else-if="!students.length"><empty-state icon="👥" text="暂无学生名单" /></view>

    <view v-else class="student-list">
      <view v-for="s in students" :key="s.studentId" class="student-row" @click="cycleStatus(s)">
        <view class="sr-info">
          <text class="sr-name">{{ s.studentName }}</text>
        </view>
        <text class="sr-tag" :class="statusClass(s)">{{ statusText(s) }}</text>
      </view>
    </view>

    <view v-if="students.length" class="submit-bar">
      <button class="submit-btn" :loading="submitting" @click="handleSubmit">确认签到</button>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, computed } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { getScheduleStudents } from '@/api/teacher';
import { batchSignin } from '@/api/checkin';
import EmptyState from '@/components/empty-state.vue';
import LoadingSkeleton from '@/components/loading-skeleton.vue';

const STATUSES = ['PRESENT', 'LATE', 'ABSENT'];
const STATUS_LABELS = { PRESENT: '到校', LATE: '迟到', ABSENT: '缺勤' };
const STATUS_COLORS = { PRESENT: 'tag-green', LATE: 'tag-orange', ABSENT: 'tag-red' };

let scheduleId = null;
const courseInfo = reactive({ courseName: '', className: '' });
const students = ref([]);
const loading = ref(true);
const error = ref(false);
const submitting = ref(false);

onLoad((options) => {
  scheduleId = options.scheduleId;
  loadStudents();
});

async function loadStudents() {
  loading.value = true; error.value = false;
  try {
    const res = await getScheduleStudents(scheduleId);
    if (res.code === 200) {
      const data = res.data || [];
      students.value = data.map(s => ({ ...s, _status: s.attendanceStatus || 'PRESENT' }));
      if (data.length > 0) {
        courseInfo.courseName = data[0].courseName || '';
        courseInfo.className = data[0].className || '';
      }
    }
  } catch { error.value = true; }
  finally { loading.value = false; }
}

const signedCount = computed(() => students.value.length);

function cycleStatus(s) {
  const idx = STATUSES.indexOf(s._status);
  s._status = STATUSES[(idx + 1) % 3];
}

function statusText(s) { return STATUS_LABELS[s._status] || '--'; }
function statusClass(s) { return STATUS_COLORS[s._status] || ''; }

async function handleSubmit() {
  const lines = students.value.map(s => `${s.studentName}: ${statusText(s)}`);
  const confirmed = await new Promise(resolve => {
    uni.showModal({ title: '确认签到', content: lines.join('\n'), success: r => resolve(r.confirm) });
  });
  if (!confirmed) return;

  submitting.value = true;
  try {
    const data = students.value.map(s => ({
      scheduleId: Number(scheduleId),
      studentId: s.studentId,
      status: s._status,
    }));
    const res = await batchSignin(data);
    if (res.code === 200) {
      uni.showToast({ title: '签到成功', icon: 'success' });
      setTimeout(() => uni.navigateBack(), 1500);
    } else {
      uni.showToast({ title: res.msg || '签到失败', icon: 'none' });
    }
  } catch { uni.showToast({ title: '网络错误', icon: 'none' }); }
  finally { submitting.value = false; }
}
</script>

<style scoped lang="scss">
.batch-page { min-height: 100vh; padding-bottom: 120rpx; }
.batch-header { padding: 24rpx; background: #fff; margin-bottom: 16rpx; }
.bh-course { font-size: 34rpx; font-weight: 700; color: #303133; display: block; }
.bh-class { font-size: 26rpx; color: #606266; margin-top: 4rpx; display: block; }
.bh-count { font-size: 24rpx; color: #409eff; margin-top: 8rpx; display: block; }

.student-list { padding: 0 24rpx; }
.student-row { display: flex; justify-content: space-between; align-items: center; padding: 24rpx; background: #fff; border-radius: 12rpx; margin-bottom: 8rpx; }
.sr-name { font-size: 28rpx; color: #303133; font-weight: 500; }
.sr-tag { font-size: 24rpx; padding: 6rpx 20rpx; border-radius: 20rpx; border: 1rpx solid #dcdfe6; }
.tag-green { background: #f0f9eb; color: #67c23a; border-color: #c2e7b0; }
.tag-orange { background: #fdf6ec; color: #e6a23c; border-color: #f5dab1; }
.tag-red { background: #fef0f0; color: #f56c6c; border-color: #fbc4c4; }

.submit-bar { position: fixed; bottom: 0; left: 0; right: 0; padding: 16rpx 24rpx; padding-bottom: calc(120rpx + env(safe-area-inset-bottom)); background: #fff; border-top: 1rpx solid #ebeef5; }
.submit-btn { width: 100%; height: 88rpx; line-height: 88rpx; background: #409eff; color: #fff; border-radius: 44rpx; font-size: 32rpx; border: none; }
.submit-btn::after { border: none; }
</style>
