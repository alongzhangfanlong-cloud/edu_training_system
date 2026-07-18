<!-- 录入/编辑成绩 Drawer -->
<template>
  <el-drawer :model-value="visible" :title="title" size="700px" destroy-on-close append-to-body direction="rtl" @open="onOpen" @update:model-value="(val) => emit('update:visible', val)">
    <div v-loading="loading" class="exam-body">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="考试名称" prop="examName">
              <el-input v-model="form.examName" placeholder="如：期中考试、单元测验" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="考试类型" prop="examType">
              <el-select v-model="form.examType" style="width:100%">
                <el-option label="月考" value="MONTHLY" />
                <el-option label="期中" value="MIDTERM" />
                <el-option label="期末" value="FINAL" />
                <el-option label="测验" value="QUIZ" />
                <el-option label="自定义" value="CUSTOM" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="班级" prop="classId">
              <el-select v-model="form.classId" placeholder="选择班级" filterable remote :remote-method="remoteSearchClass" :loading="classLoading" style="width:100%">
                <el-option v-for="c in classOptions" :key="c.id" :label="c.name" :value="c.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="科目" prop="subjectId">
              <el-select v-model="form.subjectId" placeholder="选择科目" filterable remote :remote-method="remoteSearchSubject" :loading="subjectLoading" style="width:100%">
                <el-option v-for="s in subjectOptions" :key="s.id" :label="s.name" :value="s.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="满分" prop="totalScore">
              <el-input-number v-model="form.totalScore" :min="1" :precision="1" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="考试日期" prop="examDate">
          <el-date-picker v-model="form.examDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width:200px" />
        </el-form-item>
      </el-form>

      <!-- 学生成绩列表 -->
      <h4 class="section-title">学生成绩</h4>
      <el-table :data="students" size="small" border max-height="400">
        <el-table-column label="序号" type="index" width="50" align="center" />
        <el-table-column label="学生姓名" prop="studentName" width="120" />
        <el-table-column label="分数" width="150">
          <template #default="{ row }">
            <el-input-number v-model="row.score" :min="0" :max="form.totalScore || 999" :precision="1" size="small" :disabled="row.isAbsent === 1" style="width:120px" />
          </template>
        </el-table-column>
        <el-table-column label="缺考" width="70" align="center">
          <template #default="{ row }">
            <el-checkbox v-model="row.isAbsent" :true-value="1" :false-value="0" @change="onAbsentChange(row)" />
          </template>
        </el-table-column>
        <el-table-column label="备注" min-width="120">
          <template #default="{ row }">
            <el-input v-model="row.remark" size="small" placeholder="选填" maxlength="100" />
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="students.length === 0" description="该班级暂无学生" :image-size="60" />
    </div>

    <template #footer>
      <div class="drawer-footer">
        <el-button @click="emit('update:visible', false)">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">保存</el-button>
      </div>
    </template>
  </el-drawer>
</template>

<script setup>
import { ref, nextTick } from 'vue';
import { ElMessage } from 'element-plus';
import { addExam, updateExam, getExam, getExamStudents } from '@/api/teaching/exam';
import { listClass } from '@/api/class/classes';
import { listSubject } from '@/api/course/subjects';

const props = defineProps({ visible: Boolean, title: String, examId: { type: [Number, String], default: null } });
const emit = defineEmits(['update:visible', 'submit-success']);

const loading = ref(false);
const formRef = ref(null); const submitting = ref(false);
const form = ref({ examName: '', examType: 'MONTHLY', classId: '', subjectId: '', totalScore: 100, examDate: '' });
const rules = {
  examName: [{ required: true, message: '请输入考试名称', trigger: 'blur' }],
  examType: [{ required: true, message: '请选择考试类型', trigger: 'change' }],
  classId: [{ required: true, message: '请选择班级', trigger: 'change' }],
  subjectId: [{ required: true, message: '请选择科目', trigger: 'change' }],
  totalScore: [{ required: true, message: '请输入满分', trigger: 'blur' }],
  examDate: [{ required: true, message: '请选择考试日期', trigger: 'change' }],
};
const students = ref([]);
const classOptions = ref([]); const classLoading = ref(false);
const subjectOptions = ref([]); const subjectLoading = ref(false);
let classSeq = 0; let subjectSeq = 0;

function onAbsentChange(row) {
  if (row.isAbsent === 1) { row.score = null; }
}

function remoteSearchClass(query) {
  if (!query) { classOptions.value = []; return; }
  classLoading.value = true; classSeq++; const seq = classSeq;
  listClass({ page: 1, size: 20, keyword: query }).then(res => {
    if (seq !== classSeq) return;
    classOptions.value = res.success ? (res.data?.records || res.rows || []) : [];
  }).finally(() => { if (seq === classSeq) classLoading.value = false; });
}

function remoteSearchSubject(query) {
  if (!query) { subjectOptions.value = []; return; }
  subjectLoading.value = true; subjectSeq++; const seq = subjectSeq;
  listSubject({ page: 1, size: 20, keyword: query }).then(res => {
    if (seq !== subjectSeq) return;
    subjectOptions.value = res.success ? (res.data?.records || res.rows || []) : [];
  }).finally(() => { if (seq === subjectSeq) subjectLoading.value = false; });
}

async function onOpen() {
  reset();
  if (props.examId) {
    loading.value = true;
    try {
      const res = await getExam(props.examId);
      if (res.success && res.data) {
        const e = res.data.data || res.data;
        form.value = {
          examName: e.examName || '',
          examType: e.examType || 'MONTHLY',
          classId: e.classId || '',
          subjectId: e.subjectId || '',
          totalScore: e.totalScore || 100,
          examDate: e.examDate || '',
        };
        if (e.classId) classOptions.value = [{ id: e.classId, name: e.className || '—' }];
        if (e.subjectId) subjectOptions.value = [{ id: e.subjectId, name: e.subjectName || '—' }];
        // load student scores
        const sRes = await getExamStudents(props.examId);
        if (sRes.success && sRes.data) {
          const list = sRes.data.data || sRes.data || [];
          students.value = list.map(s => ({
            studentId: s.studentId,
            studentName: s.studentName || '',
            score: s.score,
            isAbsent: s.isAbsent || 0,
            remark: s.remark || '',
          }));
        }
      }
    } catch (e) { /* ignore */ }
    finally { loading.value = false; }
  }
}

function reset() {
  form.value = { examName: '', examType: 'MONTHLY', classId: '', subjectId: '', totalScore: 100, examDate: '' };
  students.value = []; classOptions.value = []; subjectOptions.value = [];
  nextTick(() => formRef.value?.clearValidate());
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false); if (!valid) return;

  // validate scores
  for (const s of students.value) {
    if (s.isAbsent !== 1 && (s.score === null || s.score === undefined)) { ElMessage.warning(`${s.studentName} 的分数未填写`); return; }
    if (s.score > form.value.totalScore) { ElMessage.warning(`${s.studentName} 的分数超过满分 ${form.value.totalScore}`); return; }
  }

  submitting.value = true;
  try {
    const data = { ...form.value };
    if (props.examId) data.id = props.examId;

    const studentsData = students.value.map(s => ({
      studentId: s.studentId,
      score: s.isAbsent === 1 ? null : s.score,
      isAbsent: s.isAbsent || 0,
      remark: s.remark,
    }));

    let res;
    if (props.examId) { res = await updateExam(props.examId, { ...data, students: studentsData }); }
    else { res = await addExam({ ...data, students: studentsData }); }

    if (res.success) { ElMessage.success(props.examId ? '编辑成功' : '录入成功'); emit('update:visible', false); emit('submit-success'); }
    else { ElMessage.error(res.msg || '操作失败'); }
  } finally { submitting.value = false; }
}
</script>

<style scoped lang="less">
.exam-body { padding: 0 16px; }
.section-title { margin: 20px 0 12px; font-size: 15px; font-weight: 600; color: #303133; }
.drawer-footer { display: flex; justify-content: flex-end; gap: 8px; }
</style>
