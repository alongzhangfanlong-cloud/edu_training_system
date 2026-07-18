<!-- 教评学表单 Drawer：排课信息 + 课堂总结 + 学生评价列表 + 积分奖励 -->
<template>
  <el-drawer :model-value="visible" :title="title" size="800px" destroy-on-close append-to-body direction="rtl" @open="onOpen" @update:model-value="(val) => emit('update:visible', val)">
    <div v-loading="loading" class="eval-body">
      <div v-if="error" class="error-box">
        <el-result icon="error" title="加载失败" :sub-title="error">
          <template #extra><el-button type="primary" @click="onOpen">重试</el-button></template>
        </el-result>
      </div>

      <template v-if="!loading && !error">
        <!-- 排课信息卡片 -->
        <el-card v-if="scheduleInfo" shadow="never" class="info-card">
          <template #header>📋 排课信息</template>
          <el-descriptions :column="2" size="small">
            <el-descriptions-item label="上课日期">{{ scheduleInfo.scheduleDate || '—' }}</el-descriptions-item>
            <el-descriptions-item label="课程">{{ scheduleInfo.courseName || '—' }}</el-descriptions-item>
            <el-descriptions-item label="班级">{{ scheduleInfo.className || '—' }}</el-descriptions-item>
            <el-descriptions-item label="教师">{{ scheduleInfo.teacherName || '—' }}</el-descriptions-item>
            <el-descriptions-item label="时段">{{ (scheduleInfo.startTime || '') + ' - ' + (scheduleInfo.endTime || '') }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <!-- 选择排课（新建时） -->
        <el-form v-if="!evaluationId && !scheduleInfo" :inline="true" size="small" class="schedule-picker">
          <el-form-item label="关联排课">
            <el-select v-model="selectedScheduleId" placeholder="选择已上课的排课记录" filterable remote :remote-method="remoteSearchSchedule" :loading="scheduleLoading" style="width:400px" @change="onScheduleChange">
              <el-option v-for="s in scheduleOptions" :key="s.id" :label="`${s.scheduleDate} ${s.courseName || ''} ${s.className || ''} ${s.startTime || ''}-${s.endTime || ''}`" :value="s.id" />
            </el-select>
          </el-form-item>
        </el-form>

        <!-- 课堂总结 -->
        <el-form ref="formRef" :model="form" :rules="submitRules" label-width="100px" class="eval-form">
          <el-form-item label="课堂总结" prop="summary">
            <el-input v-model="form.summary" type="textarea" :rows="5" placeholder="请总结本次课堂的教学内容完成情况、学生整体表现、课堂氛围等" maxlength="2000" show-word-limit :disabled="readonly" />
          </el-form-item>
        </el-form>

        <!-- 学生个人评价 -->
        <h4 class="section-title">学生个人评价 <span class="eval-count">已评 {{ evaluatedCount }} / 共 {{ students.length }} 人</span></h4>
        <div class="student-list">
          <div v-for="(student, idx) in students" :key="student.studentId" class="student-row">
            <div class="student-info">
              <span class="student-name">{{ student.studentName || '学生' + (idx + 1) }}</span>
            </div>
            <div class="student-rate">
              <span class="rate-label">星级评分</span>
              <el-rate v-model="student.starRating" :max="5" :disabled="readonly" show-score score-template="{value}分" />
            </div>
            <div class="student-comment">
              <el-input v-model="student.comment" placeholder="文字评语（选填，≤500字）" maxlength="500" show-word-limit size="small" :disabled="readonly" />
            </div>
            <div class="student-points">
              <el-button v-if="!readonly" link type="primary" size="small" @click="togglePoints(student)">🎁 {{ student.rewardPoints ? '积分: +' + student.rewardPoints : '添加积分奖励' }}</el-button>
              <span v-else-if="student.rewardPoints" class="points-badge">+{{ student.rewardPoints }} 积分</span>
              <div v-if="student.showPointsInput" class="points-input-row">
                <el-input-number v-model="student.rewardPoints" :min="1" :max="100" size="small" style="width:100px" placeholder="积分" />
                <el-input v-model="student.rewardReason" size="small" style="width:160px;margin-left:8px" placeholder="奖励原因（选填）" maxlength="100" />
                <el-button link type="danger" size="small" @click="student.rewardPoints = null; student.rewardReason = ''; student.showPointsInput = false">取消</el-button>
              </div>
            </div>
          </div>
        </div>
        <el-empty v-if="students.length === 0" description="该班级暂无学生" :image-size="60" />
      </template>
    </div>

    <template #footer>
      <div class="drawer-footer">
        <el-button @click="emit('update:visible', false)">取消</el-button>
        <el-button v-if="!readonly" @click="handleDraft" :loading="saving">暂存草稿</el-button>
        <el-button v-if="!readonly" type="primary" @click="handleSubmit" :loading="submitting" :disabled="!selectedScheduleId && !scheduleInfo">提交</el-button>
      </div>
    </template>
  </el-drawer>
</template>

<script setup>
import { ref, reactive, computed, nextTick } from 'vue';
import { ElMessage } from 'element-plus';
import { getEvaluation, saveDraft, submitEvaluation } from '@/api/teaching/evaluation';
import { listSchedule } from '@/api/schedule/schedules';

const props = defineProps({ visible: Boolean, title: String, evaluationId: { type: [Number, String], default: null }, scheduleId: { type: [Number, String], default: null }, readonly: Boolean });
const emit = defineEmits(['update:visible', 'submit-success']);

const loading = ref(false); const error = ref('');
const formRef = ref(null);
const form = ref({ summary: '' });
const submitRules = { summary: [{ required: true, message: '请输入课堂总结', trigger: 'blur' }, { min: 30, message: '课堂总结至少30字', trigger: 'blur' }] };
const students = ref([]);
const scheduleInfo = ref(null);
const selectedScheduleId = ref('');
const scheduleOptions = ref([]); const scheduleLoading = ref(false);
const saving = ref(false); const submitting = ref(false);
let scheduleSeq = 0;

const evaluatedCount = computed(() => students.value.filter(s => s.starRating > 0).length);

function togglePoints(student) {
  student.showPointsInput = !student.showPointsInput;
  if (student.showPointsInput && !student.rewardPoints) student.rewardPoints = null;
}

function remoteSearchSchedule(query) {
  if (!query) { scheduleOptions.value = []; return; }
  scheduleLoading.value = true; scheduleSeq++; const seq = scheduleSeq;
  listSchedule({ page: 1, size: 20, keyword: query }).then(res => {
    if (seq !== scheduleSeq) return;
    scheduleOptions.value = res.success ? (res.data?.records || res.rows || []) : [];
  }).finally(() => { if (seq === scheduleSeq) scheduleLoading.value = false; });
}

function onScheduleChange(id) {
  const s = scheduleOptions.value.find(s => s.id === id);
  if (s) scheduleInfo.value = s;
}

async function onOpen() {
  loading.value = false; error.value = '';
  form.value = { summary: '' }; students.value = [];
  scheduleInfo.value = null; selectedScheduleId.value = '';
  scheduleOptions.value = [];

  if (!props.evaluationId && !props.scheduleId) return;

  loading.value = true;
  try {
    const id = props.evaluationId || props.scheduleId;
    const res = await getEvaluation(id);
    if (res.success && res.data) {
      const evalData = res.data.data || res.data;
      form.value.summary = evalData.summary || '';
      scheduleInfo.value = {
        scheduleDate: evalData.scheduleDate,
        courseName: evalData.courseName,
        className: evalData.className,
        teacherName: evalData.teacherName,
        startTime: evalData.startTime,
        endTime: evalData.endTime,
        scheduleId: evalData.scheduleId,
      };
      const evalStudents = evalData.students || evalData.evaluationStudents || [];
      students.value = evalStudents.map(s => ({
        studentId: s.studentId,
        studentName: s.studentName || '',
        starRating: s.starRating || 0,
        comment: s.comment || '',
        rewardPoints: s.rewardPoints || null,
        rewardReason: s.rewardReason || '',
        showPointsInput: !!(s.rewardPoints),
      }));
    } else { error.value = res.msg || '加载失败'; }
  } catch (e) { error.value = '网络错误，请检查网络后重试'; }
  finally { loading.value = false; }
}

async function handleDraft() {
  saving.value = true;
  try {
    const params = { summary: form.value.summary };
    if (scheduleInfo.value?.scheduleId) params.scheduleId = scheduleInfo.value.scheduleId;
    else if (selectedScheduleId.value) params.scheduleId = selectedScheduleId.value;
    if (scheduleInfo.value?.classId) params.classId = scheduleInfo.value.classId;
    if (scheduleInfo.value?.teacherId) params.teacherId = scheduleInfo.value.teacherId;

    const data = students.value.map(s => ({
      studentId: s.studentId,
      starRating: s.starRating || 0,
      comment: s.comment,
      rewardPoints: s.rewardPoints,
      rewardReason: s.rewardReason,
    }));

    const res = await saveDraft(params, data);
    if (res.success) { ElMessage.success('草稿已保存'); emit('update:visible', false); emit('submit-success'); }
    else { ElMessage.error(res.msg || '保存失败'); }
  } finally { saving.value = false; }
}

async function handleSubmit() {
  // validate class summary
  if (!form.value.summary || form.value.summary.length < 30) { ElMessage.warning('课堂总结至少需要30字'); return; }

  // validate all students have rating
  const unrated = students.value.filter(s => !s.starRating || s.starRating === 0);
  if (unrated.length > 0) { ElMessage.warning(`还有 ${unrated.length} 名学生未评分，请完成所有学生评分后再提交`); return; }

  submitting.value = true;
  try {
    const params = { summary: form.value.summary };
    if (scheduleInfo.value?.teacherId) params.teacherId = scheduleInfo.value.teacherId;

    const data = students.value.map(s => ({
      studentId: s.studentId,
      starRating: s.starRating,
      comment: s.comment,
      rewardPoints: s.rewardPoints,
      rewardReason: s.rewardReason,
    }));

    const evalId = props.evaluationId;
    if (!evalId) { ElMessage.warning('请先保存草稿后再提交'); submitting.value = false; return; }

    const res = await submitEvaluation(evalId, params, data);
    if (res.success) { ElMessage.success('评价已提交，积分已发放'); emit('update:visible', false); emit('submit-success'); }
    else { ElMessage.error(res.msg || '提交失败'); }
  } finally { submitting.value = false; }
}
</script>

<style scoped lang="less">
.eval-body { padding: 0 16px; }
.info-card { margin-bottom: 16px; }
.schedule-picker { margin-bottom: 16px; }
.eval-form { margin-bottom: 16px; }
.section-title { margin: 20px 0 12px; font-size: 15px; font-weight: 600; color: #303133; }
.eval-count { font-size: 13px; font-weight: 400; color: #909399; margin-left: 8px; }
.student-list { display: flex; flex-direction: column; gap: 12px; }
.student-row { padding: 12px; background: #fafafa; border-radius: 6px; border: 1px solid #ebeef5; }
.student-info { margin-bottom: 8px; }
.student-name { font-weight: 600; font-size: 14px; }
.student-rate { display: flex; align-items: center; gap: 8px; margin-bottom: 8px; }
.rate-label { font-size: 13px; color: #606266; }
.student-comment { margin-bottom: 4px; }
.student-points { min-height: 24px; }
.points-input-row { display: flex; align-items: center; margin-top: 6px; gap: 8px; }
.points-badge { color: #e6a23c; font-size: 12px; }
.drawer-footer { display: flex; justify-content: flex-end; gap: 8px; }
.error-box { padding: 40px 0; }
</style>
