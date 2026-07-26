<template>
  <view class="eval-form-page">
    <view v-if="loading"><loading-skeleton :rows="4" /></view>
    <template v-else>
      <view class="form-section">
        <text class="section-label">课堂总结</text>
        <textarea v-model="form.summary" class="form-textarea" placeholder="请输入课堂整体总结（必填）" maxlength="500" />
        <text class="form-count">{{ form.summary.length }}/500</text>
      </view>

      <view class="form-section">
        <text class="section-label">学生评价</text>
        <view v-for="(s, i) in form.students" :key="s.studentId" class="student-eval">
          <text class="se-name">{{ s.studentName }}</text>
          <view class="se-dimensions">
            <view v-for="dim in dimensions" :key="dim.key" class="se-dim">
              <text class="se-dim-label">{{ dim.label }}</text>
              <view class="se-stars">
                <text
                  v-for="star in 5"
                  :key="star"
                  class="se-star"
                  :class="{ filled: star <= (s[dim.key] || 0) }"
                  @click="s[dim.key] = star"
                >{{ star <= (s[dim.key] || 0) ? '★' : '☆' }}</text>
              </view>
            </view>
          </view>
          <input v-model="s.comment" class="se-comment" placeholder="评语（选填，200字）" maxlength="200" />
        </view>
      </view>

      <view class="submit-bar">
        <button class="draft-btn" @click="saveDraft">暂存草稿</button>
        <button class="submit-btn" type="primary" :loading="submitting" @click="handleSubmit">提交评价</button>
      </view>
    </template>
  </view>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { getEvaluationDetail, saveEvaluationDraft, submitEvaluation } from '@/api/teacher-evaluation';
import LoadingSkeleton from '@/components/loading-skeleton.vue';

const dimensions = [
  { key: 'focus', label: '专注度' },
  { key: 'participation', label: '参与度' },
  { key: 'completion', label: '完成度' },
  { key: 'overall', label: '综合表现' },
];

let evalId = null;
const loading = ref(true);
const submitting = ref(false);
const form = reactive({ summary: '', students: [] });

onLoad((options) => {
  evalId = options.id;
  loadDetail();
});

async function loadDetail() {
  loading.value = true;
  try {
    const res = await getEvaluationDetail(evalId);
    if (res.code === 200) {
      const d = res.data;
      form.summary = d.summary || '';
      form.students = (d.students || []).map(s => ({
        studentId: s.studentId,
        studentName: s.studentName,
        focus: s.focus || 0,
        participation: s.participation || 0,
        completion: s.completion || 0,
        overall: s.overall || 0,
        comment: s.comment || '',
      }));
    }
  } catch { /* ignore */ }
  finally { loading.value = false; }
}

async function saveDraft() {
  submitting.value = true;
  try {
    const res = await saveEvaluationDraft({ id: evalId, summary: form.summary, students: form.students });
    if (res.code === 200) uni.showToast({ title: '已保存草稿', icon: 'success' });
    else uni.showToast({ title: res.msg || '保存失败', icon: 'none' });
  } catch { uni.showToast({ title: '保存失败', icon: 'none' }); }
  finally { submitting.value = false; }
}

async function handleSubmit() {
  if (!form.summary.trim()) { uni.showToast({ title: '请输入课堂总结', icon: 'none' }); return; }
  for (let i = 0; i < form.students.length; i++) {
    const s = form.students[i];
    if (!s.overall || s.overall === 0) {
      uni.showToast({ title: `请为${s.studentName}打分（综合表现）`, icon: 'none' });
      return;
    }
  }
  submitting.value = true;
  try {
    const res = await submitEvaluation({ id: evalId, summary: form.summary, students: form.students });
    if (res.code === 200) {
      uni.showToast({ title: '提交成功', icon: 'success' });
      setTimeout(() => uni.navigateBack(), 1500);
    } else {
      uni.showToast({ title: res.msg || '提交失败', icon: 'none' });
    }
  } catch { uni.showToast({ title: '网络错误', icon: 'none' }); }
  finally { submitting.value = false; }
}
</script>

<style scoped lang="scss">
.eval-form-page { min-height: 100vh; padding: 24rpx; padding-bottom: 160rpx; }
.form-section { background: #fff; border-radius: 12rpx; padding: 24rpx; margin-bottom: 24rpx; }
.section-label { font-size: 30rpx; font-weight: 600; color: #303133; margin-bottom: 16rpx; display: block; }
.form-textarea { width: 100%; height: 200rpx; padding: 16rpx; background: #f5f7fa; border-radius: 8rpx; font-size: 28rpx; box-sizing: border-box; }
.form-count { text-align: right; font-size: 22rpx; color: #c0c4cc; margin-top: 4rpx; display: block; }

.student-eval { border-top: 1rpx solid #ebeef5; padding-top: 20rpx; margin-top: 20rpx; }
.se-name { font-size: 28rpx; font-weight: 600; color: #303133; display: block; margin-bottom: 12rpx; }
.se-dim { display: flex; align-items: center; margin-bottom: 8rpx; }
.se-dim-label { width: 120rpx; font-size: 24rpx; color: #909399; }
.se-stars { display: flex; gap: 4rpx; }
.se-star { font-size: 36rpx; color: #dcdfe6; }
.se-star.filled { color: #f7ba2a; }
.se-comment { margin-top: 8rpx; padding: 12rpx 16rpx; background: #f5f7fa; border-radius: 8rpx; font-size: 26rpx; width: 100%; box-sizing: border-box; }

.submit-bar { position: fixed; bottom: 0; left: 0; right: 0; display: flex; gap: 16rpx; padding: 16rpx 24rpx; padding-bottom: calc(120rpx + env(safe-area-inset-bottom)); background: #fff; border-top: 1rpx solid #ebeef5; }
.draft-btn { flex: 1; height: 88rpx; line-height: 88rpx; border: 1rpx solid #409eff; color: #409eff; border-radius: 44rpx; font-size: 30rpx; background: #fff; }
.submit-btn { flex: 1; height: 88rpx; line-height: 88rpx; background: #409eff; border-radius: 44rpx; font-size: 30rpx; border: none; }
.draft-btn::after, .submit-btn::after { border: none; }
</style>
