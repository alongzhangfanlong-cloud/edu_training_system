<!-- 课酬表单弹窗 -->
<template>
  <el-dialog :model-value="visible" :title="title" width="550px" destroy-on-close append-to-body @open="onOpen" @update:model-value="(val) => emit('update:visible', val)">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="教师" prop="teacherId">
            <el-select v-model="form.teacherId" filterable remote :remote-method="searchTeachers" :loading="teacherLoading" placeholder="请搜索教师" clearable style="width:100%">
              <el-option v-for="t in teacherOptions" :key="t.id" :label="t.name" :value="t.id" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="月份" prop="month">
            <el-date-picker v-model="form.month" type="month" placeholder="选择月份" value-format="YYYY-MM" style="width:100%" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="课程" prop="courseId">
            <el-select v-model="form.courseId" filterable remote :remote-method="searchCourses" :loading="courseLoading" placeholder="请搜索课程" clearable style="width:100%">
              <el-option v-for="c in courseOptions" :key="c.id" :label="c.name" :value="c.id" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="消课数" prop="lessonCount">
            <el-input-number v-model="form.lessonCount" :min="1" style="width:100%" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="单节课酬" prop="payPerLesson">
            <el-input-number v-model="form.payPerLesson" :min="0" :precision="2" style="width:100%" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="小计">
            <el-input-number :model-value="totalPay" :precision="2" disabled style="width:100%" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="状态">
        <el-select v-model="form.status" style="width:200px">
          <el-option label="草稿" value="DRAFT" />
          <el-option label="已确认" value="CONFIRMED" />
          <el-option label="已发放" value="PAID" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="emit('update:visible', false)">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, nextTick, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { addPayroll } from '@/api/finance/payrolls';
import { listTeacherUsers } from '@/api/teaching/teachers';
import { listCourse, listAllCourses } from '@/api/course/courses';

const props = defineProps({
  visible: { type: Boolean, default: false },
  title: { type: String, default: '' },
});
const emit = defineEmits(['update:visible', 'submit-success']);

const formRef = ref(null);
const submitting = ref(false);
const teacherOptions = ref([]);
const teacherLoading = ref(false);
const courseOptions = ref([]);
const courseLoading = ref(false);

const form = ref({
  teacherId: null, month: '', courseId: null, lessonCount: 1,
  payPerLesson: null, totalPay: 0, status: 'DRAFT',
});

const rules = {
  teacherId: [{ required: true, message: '请选择教师', trigger: 'change' }],
  month: [{ required: true, message: '请选择月份', trigger: 'change' }],
  courseId: [{ required: true, message: '请选择课程', trigger: 'change' }],
  lessonCount: [{ required: true, message: '请输入消课数', trigger: 'blur' }],
  payPerLesson: [{ required: true, message: '请输入单节课酬', trigger: 'blur' }],
};

const totalPay = computed(() => {
  const count = Number(form.value.lessonCount) || 0;
  const price = Number(form.value.payPerLesson) || 0;
  return Math.round(count * price * 100) / 100;
});

let tTimer = null; let tSeq = 0;
function searchTeachers(keyword) {
  clearTimeout(tTimer);
  teacherLoading.value = true;
  tTimer = setTimeout(() => { tSeq++; const seq = tSeq;
    listTeacherUsers(keyword || '').then(res => {
      if (seq !== tSeq) return;
      if (res.success) {
        teacherOptions.value = (res.data || []).map(u => ({ id: u.userId, name: u.realName }));
      }
    }).finally(() => { if (seq === tSeq) teacherLoading.value = false; });
  }, 300);
}

let cTimer = null; let cSeq = 0;
function searchCourses(keyword) {
  clearTimeout(cTimer);
  courseLoading.value = true;
  cTimer = setTimeout(() => { cSeq++; const seq = cSeq;
    // 如果没有关键词，加载所有课程；有关键词时分页搜索
    const apiCall = !keyword 
      ? listAllCourses() 
      : listCourse({ page: 1, pageSize: 100, name: keyword });
    apiCall.then(res => {
      if (seq !== cSeq) return;
      if (res.success) {
        const courses = Array.isArray(res.data) ? res.data : (res.data?.records || res.rows || []);
        courseOptions.value = courses.map(c => ({ id: c.id, name: c.name }));
      }
    }).finally(() => { if (seq === cSeq) courseLoading.value = false; });
  }, 300);
}

function onOpen() { reset(); }

function reset() {
  form.value = { teacherId: null, month: '', courseId: null, lessonCount: 1, payPerLesson: null, totalPay: 0, status: 'DRAFT' };
  teacherOptions.value = []; courseOptions.value = [];
  // 默认加载教师和课程列表
  searchTeachers('');
  searchCourses('');
  nextTick(() => formRef.value?.clearValidate());
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false);
  if (!valid) return;
  submitting.value = true;
  try {
    const data = { ...form.value, totalPay: Number(totalPay.value) };
    const res = await addPayroll(data);
    if (res.success) { ElMessage.success('新增成功'); emit('update:visible', false); emit('submit-success'); }
    else { ElMessage.error(res.msg || '操作失败'); }
  } finally { submitting.value = false; }
}
</script>
