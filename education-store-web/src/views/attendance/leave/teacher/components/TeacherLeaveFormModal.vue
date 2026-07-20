<!-- 教师请假表单弹窗 -->
<template>
  <el-dialog
    :model-value="visible" @update:model-value="$emit('update:visible', $event)"
    :title="title" width="500px" destroy-on-close append-to-body @open="onOpen"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
      <el-form-item label="教师" prop="teacherId">
        <el-select v-model="form.teacherId" filterable remote :remote-method="searchTeachers" :loading="teacherLoading" placeholder="请搜索教师姓名" clearable style="width:100%">
          <el-option v-for="t in teacherOptions" :key="t.id" :label="t.name" :value="t.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="请假日期" prop="dateRange">
        <el-date-picker
          v-model="form.dateRange" type="daterange" range-separator="至"
          start-placeholder="开始日期" end-placeholder="结束日期"
          value-format="YYYY-MM-DD" style="width:100%" :disabled-date="disabledDate"
        />
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
      <el-button type="primary" :loading="submitting" @click="handleSubmit">提交申请</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, nextTick } from 'vue';
import { ElMessage } from 'element-plus';
import { addTeacherLeave } from '@/api/attendance/leaveTeacher';
import { listTeacher as listTeacherApi } from '@/api/teaching/teachers';

const props = defineProps({
  visible: { type: Boolean, default: false },
  title: { type: String, default: '教师请假申请' }
});

const emit = defineEmits(['update:visible', 'submit-success']);

const formRef = ref(null);
const submitting = ref(false);
const teacherOptions = ref([]);
const teacherLoading = ref(false);

const form = ref({
  teacherId: null,
  dateRange: [],
  leaveType: 0,
  reason: ''
});

const rules = {
  teacherId: [{ required: true, message: '请选择教师', trigger: 'change' }],
  dateRange: [{ required: true, message: '请选择日期范围', trigger: 'change' }],
  leaveType: [{ required: true, message: '请选择类型', trigger: 'change' }],
  reason: [{ required: true, message: '请填写原因', trigger: 'blur' }]
};

function disabledDate(time) {
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  return time.getTime() < today.getTime() - 86400000;
}

function onOpen() { reset(); }

function reset() {
  form.value = { teacherId: null, dateRange: [], leaveType: 0, reason: '' };
  teacherOptions.value = [];
  nextTick(() => formRef.value?.clearValidate());
}

let sTimer = null; let sSeq = 0;
function searchTeachers(keyword) {
  clearTimeout(sTimer);
  if (!keyword) { teacherOptions.value = []; return; }
  teacherLoading.value = true;
  sTimer = setTimeout(() => { sSeq++; const seq = sSeq;
    listTeacherApi({ page: 1, pageSize: 50, keyword }).then(res => {
      if (seq !== sSeq) return;
      if (res.success) teacherOptions.value = res.data?.records || res.rows || [];
    }).finally(() => { if (seq === sSeq) teacherLoading.value = false; });
  }, 300);
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false);
  if (!valid) return;

  submitting.value = true;
  try {
    const [startDate, endDate] = form.value.dateRange;
    const data = {
      teacherId: form.value.teacherId,
      leaveStartDate: startDate,
      leaveEndDate: endDate || startDate,
      leaveType: form.value.leaveType,
      reason: form.value.reason,
      applicantType: 'ADMIN',
      status: 0
    };
    const res = await addTeacherLeave(data);
    if (res.success) {
      ElMessage.success('请假申请已提交');
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
