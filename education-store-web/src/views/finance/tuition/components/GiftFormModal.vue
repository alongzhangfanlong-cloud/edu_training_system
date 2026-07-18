<!-- 赠课表单弹窗 -->
<template>
  <el-dialog :model-value="visible" :title="title" width="550px" destroy-on-close append-to-body @open="onOpen" @update:model-value="(val) => emit('update:visible', val)">
    <el-form ref="formRef" :model="form" :rules="getDynamicRules()" label-width="100px">
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
            <el-select v-model="form.courseId" filterable remote :remote-method="searchCourses" :loading="courseLoading" placeholder="请搜索课程" clearable style="width:100%">
              <el-option v-for="c in courseOptions" :key="c.id" :label="c.name" :value="c.id" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="赠送课时" prop="hours">
            <el-input-number v-model="form.hours" :min="1" :max="200" style="width:100%" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="经手人" prop="operatorId">
            <el-select v-model="form.operatorId" filterable remote :remote-method="searchUsers" :loading="userLoading" placeholder="请搜索经手人" clearable style="width:100%">
              <el-option v-for="u in userOptions" :key="u.id" :label="u.userName || u.name" :value="u.id" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="赠送原因" prop="reason">
        <el-select v-model="form.reason" placeholder="选择赠送原因" style="width:100%" @change="onReasonChange">
          <el-option label="促销活动" value="PROMOTION" />
          <el-option label="补偿" value="COMPENSATION" />
          <el-option label="其他" value="OTHER" />
        </el-select>
      </el-form-item>
      <el-form-item v-if="form.reason === 'OTHER'" label="自定义原因" prop="reasonCustom">
        <el-input v-model="form.reasonCustom" placeholder="请输入赠送原因" maxlength="200" show-word-limit />
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="备注信息" maxlength="500" show-word-limit />
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
import { addGift } from '@/api/finance/tuitionGifts';
import { listStudent } from '@/api/student/students';
import { listCourse } from '@/api/course/courses';

const props = defineProps({
  visible: { type: Boolean, default: false },
  title: { type: String, default: '' },
});
const emit = defineEmits(['update:visible', 'submit-success']);

const formRef = ref(null);
const submitting = ref(false);
const studentOptions = ref([]);
const studentLoading = ref(false);
const courseOptions = ref([]);
const courseLoading = ref(false);
const userOptions = ref([]);
const userLoading = ref(false);

const form = ref({ studentId: null, courseId: null, hours: null, reason: '', reasonCustom: '', remark: '', operatorId: null });

const rules = {
  studentId: [{ required: true, message: '请选择学员', trigger: 'change' }],
  courseId: [{ required: true, message: '请选择课程', trigger: 'change' }],
  hours: [{ required: true, message: '请输入赠送课时数', trigger: 'blur' }],
  reason: [{ required: true, message: '请选择赠送原因', trigger: 'change' }],
  operatorId: [{ required: true, message: '请选择经手人', trigger: 'change' }],
};

function getDynamicRules() {
  const base = { ...rules };
  if (form.value.reason === 'OTHER') {
    base.reasonCustom = [{ required: true, message: '请输入自定义原因', trigger: 'blur' }];
  }
  return base;
}

function onReasonChange(val) { if (val !== 'OTHER') form.value.reasonCustom = ''; }

// 远程搜索 - 学生
let sTimer = null; let sSeq = 0;
function searchStudents(keyword) {
  clearTimeout(sTimer);
  if (!keyword) { studentOptions.value = []; return; }
  studentLoading.value = true;
  sTimer = setTimeout(() => { sSeq++; const seq = sSeq;
    listStudent({ page: 1, pageSize: 100, keyword }).then(res => {
      if (seq !== sSeq) return;
      if (res.success) studentOptions.value = res.data?.records || res.rows || [];
    }).finally(() => { if (seq === sSeq) studentLoading.value = false; });
  }, 300);
}

// 远程搜索 - 课程
let cTimer = null; let cSeq = 0;
function searchCourses(keyword) {
  clearTimeout(cTimer);
  if (!keyword) { courseOptions.value = []; return; }
  courseLoading.value = true;
  cTimer = setTimeout(() => { cSeq++; const seq = cSeq;
    listCourse({ page: 1, size: 100, keyword }).then(res => {
      if (seq !== cSeq) return;
      if (res.success) courseOptions.value = res.data?.records || res.rows || [];
    }).finally(() => { if (seq === cSeq) courseLoading.value = false; });
  }, 300);
}

// 远程搜索 - 经手人（用户）
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

function onOpen() { reset(); }

function reset() {
  form.value = { studentId: null, courseId: null, hours: null, reason: '', reasonCustom: '', remark: '', operatorId: null };
  studentOptions.value = []; courseOptions.value = []; userOptions.value = [];
  nextTick(() => formRef.value?.clearValidate());
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false);
  if (!valid) return;
  submitting.value = true;
  try {
    const res = await addGift({ ...form.value });
    if (res.success) { ElMessage.success('赠送成功'); emit('update:visible', false); emit('submit-success'); }
    else { ElMessage.error(res.msg || '操作失败'); }
  } catch {
    ElMessage.error('网络异常，请稍后重试');
  } finally { submitting.value = false; }
}
</script>
