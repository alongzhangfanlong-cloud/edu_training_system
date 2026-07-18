<!-- 报名表单弹窗 -->
<template>
  <el-dialog :model-value="visible" :title="title" width="650px" destroy-on-close append-to-body @open="onOpen" @update:model-value="(val) => emit('update:visible', val)">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="学员" prop="studentId">
            <el-select v-model="form.studentId" filterable remote :remote-method="searchStudents" :loading="studentLoading" placeholder="请搜索学员" clearable style="width:100%">
              <el-option v-for="s in studentOptions" :key="s.id" :label="s.name" :value="s.id" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="课程" prop="courseId">
            <el-select v-model="form.courseId" filterable remote :remote-method="searchCourses" :loading="courseLoading" placeholder="请搜索课程" clearable style="width:100%" @change="onCourseChange">
              <el-option v-for="c in courseOptions" :key="c.id" :label="c.name" :value="c.id" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="班级" prop="classId">
            <el-select v-model="form.classId" placeholder="请选择班级（可选）" clearable style="width:100%">
              <el-option v-for="c in classOptions" :key="c.id" :label="c.name" :value="c.id" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="应缴金额" prop="amount">
            <el-input-number v-model="form.amount" :min="0.01" :precision="2" style="width:100%" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="经手人" prop="handlerId">
            <el-select v-model="form.handlerId" filterable remote :remote-method="searchUsers" :loading="userLoading" placeholder="请搜索经手人" clearable style="width:100%">
              <el-option v-for="u in userOptions" :key="u.id" :label="u.userName || u.name" :value="u.id" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="报名日期" prop="enrollmentDate">
            <el-date-picker v-model="form.enrollmentDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width:100%" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="收费状态">
        <el-select v-model="form.paymentStatus" clearable style="width:200px">
          <el-option label="待收费" value="PENDING" />
          <el-option label="已收费" value="PAID" />
          <el-option label="部分收费" value="PARTIAL" />
          <el-option label="已退费" value="REFUNDED" />
          <el-option label="已取消" value="CANCELLED" />
        </el-select>
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="备注信息（最多200字）" maxlength="200" show-word-limit />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="emit('update:visible', false)">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, nextTick } from 'vue';
import { ElMessage } from 'element-plus';
import { getEnrollment, addEnrollment, updateEnrollment } from '@/api/finance/enrollments';
import { listStudent } from '@/api/student/students';
import { listCourse } from '@/api/course/courses';
import { listClass } from '@/api/class/classes';

const props = defineProps({
  visible: { type: Boolean, default: false },
  title: { type: String, default: '' },
  enrollmentId: { type: [Number, String], default: null },
});
const emit = defineEmits(['update:visible', 'submit-success']);

const formRef = ref(null);
const submitting = ref(false);
const studentOptions = ref([]);
const studentLoading = ref(false);
const courseOptions = ref([]);
const courseLoading = ref(false);
const classOptions = ref([]);
const userOptions = ref([]);
const userLoading = ref(false);

const form = ref({
  studentId: null, courseId: null, classId: null, amount: null,
  handlerId: null, enrollmentDate: '', paymentStatus: 'PENDING', remark: '',
});

const rules = {
  studentId: [{ required: true, message: '请选择学员', trigger: 'change' }],
  courseId: [{ required: true, message: '请选择课程', trigger: 'change' }],
  amount: [{ required: true, message: '请输入应缴金额', trigger: 'blur' }],
  handlerId: [{ required: true, message: '请选择经手人', trigger: 'change' }],
  enrollmentDate: [{ required: true, message: '请选择报名日期', trigger: 'change' }],
};

// 远程搜索 - 学生
let studentTimer = null; let studentSeq = 0;
function searchStudents(keyword) {
  clearTimeout(studentTimer);
  if (!keyword) { studentOptions.value = []; return; }
  studentLoading.value = true;
  studentTimer = setTimeout(() => {
    studentSeq++; const seq = studentSeq;
    listStudent({ page: 1, pageSize: 100, keyword }).then(res => {
      if (seq !== studentSeq) return;
      if (res.success) studentOptions.value = res.data?.records || res.rows || [];
    }).finally(() => { if (seq === studentSeq) studentLoading.value = false; });
  }, 300);
}

// 远程搜索 - 课程
let cTimer = null; let cSeq = 0;
function searchCourses(keyword) {
  clearTimeout(cTimer);
  if (!keyword) { courseOptions.value = []; return; }
  courseLoading.value = true;
  cTimer = setTimeout(() => {
    cSeq++; const seq = cSeq;
    listCourse({ page: 1, size: 100, keyword }).then(res => {
      if (seq !== cSeq) return;
      if (res.success) courseOptions.value = res.data?.records || res.rows || [];
    }).finally(() => { if (seq === cSeq) courseLoading.value = false; });
  }, 300);
}

function onCourseChange(courseId) {
  form.value.classId = null;
  if (courseId && courseOptions.value.length) {
    const course = courseOptions.value.find(c => c.id === courseId);
    if (course && course.price && !form.value.amount) form.value.amount = course.price;
  }
  // 加载班级列表（可选下拉）
  listClass({ page: 1, pageSize: 200 }).then(res => {
    if (res.success) classOptions.value = res.data?.records || res.rows || [];
  });
}

// 远程搜索 - 用户
let uTimer = null; let uSeq = 0;
function searchUsers(keyword) {
  clearTimeout(uTimer);
  if (!keyword) { userOptions.value = []; return; }
  userLoading.value = true;
  uTimer = setTimeout(() => {
    uSeq++; const seq = uSeq;
    import('@/api/system/user').then(({ listUser }) => {
      listUser({ page: 1, pageSize: 100, keyword }).then(res => {
        if (seq !== uSeq) return;
        if (res.success) userOptions.value = res.data?.records || res.rows || [];
      }).finally(() => { if (seq === uSeq) userLoading.value = false; });
    });
  }, 300);
}

async function onOpen() {
  reset();
  if (props.enrollmentId) {
    try {
      const res = await getEnrollment(props.enrollmentId);
      if (res.success && res.data) {
        const e = res.data;
        form.value = {
          studentId: e.studentId || null, courseId: e.courseId || null,
          classId: e.classId || null, amount: e.amount || null,
          handlerId: e.handlerId || null, enrollmentDate: e.enrollmentDate || '',
          paymentStatus: e.paymentStatus || 'PENDING', remark: e.remark || '',
        };
      }
    } catch {
      ElMessage.error('加载报名详情失败');
      emit('update:visible', false);
    }
  }
}

function reset() {
  form.value = { studentId: null, courseId: null, classId: null, amount: null, handlerId: null, enrollmentDate: '', paymentStatus: 'PENDING', remark: '' };
  studentOptions.value = []; courseOptions.value = []; classOptions.value = []; userOptions.value = [];
  nextTick(() => formRef.value?.clearValidate());
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false);
  if (!valid) return;
  submitting.value = true;
  try {
    const data = { ...form.value };
    let res;
    if (props.enrollmentId) { data.id = props.enrollmentId; res = await updateEnrollment(data); }
    else { res = await addEnrollment(data); }
    if (res.success) { ElMessage.success(props.enrollmentId ? '修改成功' : '新增成功'); emit('update:visible', false); emit('submit-success'); }
    else { ElMessage.error(res.msg || '操作失败'); }
  } finally { submitting.value = false; }
}
</script>
