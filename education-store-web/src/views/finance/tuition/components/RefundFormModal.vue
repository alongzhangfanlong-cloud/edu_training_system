<!-- 退费表单弹窗 -->
<template>
  <el-dialog :model-value="visible" :title="title" width="600px" destroy-on-close append-to-body @open="onOpen" @update:model-value="(val) => emit('update:visible', val)">
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
          <el-form-item label="退费金额" prop="amount">
            <el-input-number v-model="form.amount" :min="0.01" :precision="2" style="width:100%" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="退费方式" prop="refundMethod">
            <el-select v-model="form.refundMethod" placeholder="选择退费方式" style="width:100%">
              <el-option label="原路退回" value="ORIGINAL" />
              <el-option label="现金" value="CASH" />
              <el-option label="微信" value="WECHAT" />
              <el-option label="支付宝" value="ALIPAY" />
              <el-option label="银行转账" value="BANK_TRANSFER" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="退费原因" prop="reason">
            <el-select v-model="form.reason" placeholder="选择退费原因" style="width:100%" @change="onReasonChange">
              <el-option label="学生退课" value="STUDENT_WITHDRAW" />
              <el-option label="课程调整" value="COURSE_ADJUST" />
              <el-option label="服务质量问题" value="SERVICE_ISSUE" />
              <el-option label="重复收费" value="DUPLICATE_CHARGE" />
              <el-option label="其他" value="OTHER" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="扣回课时" prop="hoursDeducted">
            <el-input-number v-model="form.hoursDeducted" :min="0" style="width:100%" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item v-if="form.reason === 'OTHER'" label="自定义原因" prop="reasonCustom">
        <el-input v-model="form.reasonCustom" placeholder="请输入退费原因" maxlength="200" show-word-limit />
      </el-form-item>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="关联收费ID" prop="feeId">
            <el-input-number v-model="form.feeId" :min="1" placeholder="收费记录ID" style="width:100%" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="状态">
            <el-select v-model="form.status" disabled style="width:100%">
              <el-option label="已完成" value="COMPLETED" />
              <el-option label="待审批" value="PENDING_APPROVAL" />
              <el-option label="已通过" value="APPROVED" />
              <el-option label="已驳回" value="REJECTED" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
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
import { addRefund } from '@/api/finance/tuitionRefunds';
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

const form = ref({
  studentId: null, courseId: null, feeId: null, amount: null,
  reason: '', reasonCustom: '', refundMethod: 'ORIGINAL',
  hoursDeducted: 0, status: 'PENDING', remark: '',
});

const rules = {
  studentId: [{ required: true, message: '请选择学员', trigger: 'change' }],
  courseId: [{ required: true, message: '请选择课程', trigger: 'change' }],
  amount: [{ required: true, message: '请输入退费金额', trigger: 'blur' }],
  reason: [{ required: true, message: '请选择退费原因', trigger: 'change' }],
  refundMethod: [{ required: true, message: '请选择退费方式', trigger: 'change' }],
};

function getDynamicRules() {
  const base = { ...rules };
  if (form.value.reason === 'OTHER') {
    base.reasonCustom = [{ required: true, message: '请输入自定义原因', trigger: 'blur' }];
  }
  return base;
}

function onReasonChange(val) { if (val !== 'OTHER') form.value.reasonCustom = ''; }

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

function onOpen() {
  reset();
}

function reset() {
  form.value = { studentId: null, courseId: null, feeId: null, amount: null, reason: '', reasonCustom: '', refundMethod: 'ORIGINAL', hoursDeducted: 0, status: 'PENDING', remark: '' };
  studentOptions.value = []; courseOptions.value = [];
  nextTick(() => formRef.value?.clearValidate());
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false);
  if (!valid) return;
  submitting.value = true;
  try {
    const data = { ...form.value };
    const res = await addRefund(data);
    if (res.success) { ElMessage.success('退费申请成功'); emit('update:visible', false); emit('submit-success'); }
    else { ElMessage.error(res.msg || '操作失败'); }
  } finally { submitting.value = false; }
}
</script>
