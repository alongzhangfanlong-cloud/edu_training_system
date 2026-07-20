<!-- 学生请假表单弹窗（家长申请 / 教师代申请） -->
<template>
  <el-dialog
    :model-value="visible" @update:model-value="$emit('update:visible', $event)"
    :title="title" width="480px" destroy-on-close append-to-body @open="onOpen"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
      <el-form-item label="学生" prop="studentId">
        <el-select v-model="form.studentId" filterable remote :remote-method="searchStudents" :loading="studentLoading" placeholder="请搜索学生姓名" clearable style="width:100%">
          <el-option v-for="s in studentOptions" :key="s.id" :label="s.name" :value="s.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="请假日期" prop="leaveDate">
        <el-date-picker v-model="form.leaveDate" type="date" placeholder="选择日期" style="width:100%" value-format="YYYY-MM-DD" :disabled-date="disabledDate" />
      </el-form-item>
      <el-form-item label="请假类型" prop="leaveType">
        <el-radio-group v-model="form.leaveType">
          <el-radio :value="0">事假</el-radio>
          <el-radio :value="1">病假</el-radio>
          <el-radio :value="2">其他</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="请假原因" prop="reason">
        <el-input v-model="form.reason" type="textarea" :rows="4" placeholder="请填写请假原因" maxlength="500" show-word-limit />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="$emit('update:visible', false)">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">
        {{ proxyMode ? '提交（直接通过）' : '提交申请' }}
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, nextTick } from 'vue';
import { ElMessage } from 'element-plus';
import { addStudentLeave } from '@/api/attendance/leaveStudent';
import { listStudent } from '@/api/student/students';

const props = defineProps({
  visible: { type: Boolean, default: false },
  title: { type: String, default: '学生请假' },
  proxyMode: { type: Boolean, default: false }  // 教师代请假模式：提交后直接通过
});

const emit = defineEmits(['update:visible', 'submit-success']);

const formRef = ref(null);
const submitting = ref(false);
const studentOptions = ref([]);
const studentLoading = ref(false);

const form = ref({
  studentId: null,
  leaveDate: '',
  leaveType: 0,
  reason: ''
});

const rules = {
  studentId: [{ required: true, message: '请选择学生', trigger: 'change' }],
  leaveDate: [{ required: true, message: '请选择日期', trigger: 'change' }],
  leaveType: [{ required: true, message: '请选择类型', trigger: 'change' }],
  reason: [{ required: true, message: '请填写原因', trigger: 'blur' }]
};

function disabledDate(time) {
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  return time.getTime() < today.getTime() - 86400000;  // 不允许选择过去日期
}

function onOpen() {
  reset();
}

function reset() {
  form.value = { studentId: null, leaveDate: '', leaveType: 0, reason: '' };
  studentOptions.value = [];
  nextTick(() => formRef.value?.clearValidate());
}

let sTimer = null; let sSeq = 0;
function searchStudents(keyword) {
  clearTimeout(sTimer);
  if (!keyword) { studentOptions.value = []; return; }
  studentLoading.value = true;
  sTimer = setTimeout(() => { sSeq++; const seq = sSeq;
    listStudent({ page: 1, pageSize: 50, keyword }).then(res => {
      if (seq !== sSeq) return;
      if (res.success) studentOptions.value = res.data?.records || res.rows || [];
    }).finally(() => { if (seq === sSeq) studentLoading.value = false; });
  }, 300);
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false);
  if (!valid) return;

  submitting.value = true;
  try {
    const data = {
      studentId: form.value.studentId,
      leaveDate: form.value.leaveDate,
      leaveType: form.value.leaveType,
      reason: form.value.reason,
      applicantType: props.proxyMode ? 'TEACHER' : 'PARENT',
      status: props.proxyMode ? 1 : 0  // 教师代请假直接通过
    };
    const res = await addStudentLeave(data);
    if (res.success) {
      ElMessage.success(props.proxyMode ? '请假已提交并通过' : '请假申请已提交');
      emit('update:visible', false);
      emit('submit-success');
    } else {
      ElMessage.error(res.msg || '提交失败');
    }
  } catch {
    ElMessage.error('网络异常，请稍后重试');
  } finally {
    submitting.value = false;
  }
}
</script>
