<!-- 收费表单弹窗 -->
<template>
  <el-dialog :model-value="visible" :title="title" width="600px" destroy-on-close append-to-body @open="onOpen" @update:model-value="(val) => emit('update:visible', val)">
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
            <el-select v-model="form.courseId" filterable remote :remote-method="searchCourses" :loading="courseLoading" placeholder="请搜索课程" clearable style="width:100%">
              <el-option v-for="c in courseOptions" :key="c.id" :label="c.name" :value="c.id" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="班级">
            <el-select v-model="form.classId" placeholder="请选择班级（可选）" clearable style="width:100%">
              <el-option v-for="c in classOptions" :key="c.id" :label="c.name" :value="c.id" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="费用类型" prop="feeType">
            <el-select v-model="form.feeType" placeholder="选择费用类型" style="width:100%">
              <el-option label="报名费" value="REGISTRATION" />
              <el-option label="课时费" value="TUITION" />
              <el-option label="教材费" value="MATERIAL" />
              <el-option label="其他费用" value="OTHER" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="应缴金额" prop="amountReceivable">
            <el-input-number v-model="form.amountReceivable" :min="0.01" :precision="2" style="width:100%" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="实缴金额" prop="amountPaid">
            <el-input-number v-model="form.amountPaid" :min="0" :precision="2" style="width:100%" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="支付方式" prop="paymentMethod">
            <el-select v-model="form.paymentMethod" placeholder="选择支付方式" style="width:100%">
              <el-option label="现金" value="CASH" />
              <el-option label="微信" value="WECHAT" />
              <el-option label="支付宝" value="ALIPAY" />
              <el-option label="银行转账" value="BANK_TRANSFER" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="收费时间" prop="payTime">
            <el-date-picker v-model="form.payTime" type="datetime" placeholder="选择时间" value-format="YYYY-MM-DD HH:mm:ss" style="width:100%" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="关联报名">
            <el-input-number v-model="form.enrollmentId" :min="1" placeholder="报名ID" style="width:100%" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="状态">
            <el-select v-model="form.status" clearable style="width:100%">
              <el-option label="已收费" value="PAID" />
              <el-option label="已退费" value="REFUNDED" />
              <el-option label="部分退费" value="PARTIAL_REFUNDED" />
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
import { getFee, addFee, updateFee } from '@/api/finance/tuitionFees';
import { listStudent } from '@/api/student/students';
import { listCourse } from '@/api/course/courses';
import { listClass } from '@/api/class/classes';

const props = defineProps({
  visible: { type: Boolean, default: false },
  title: { type: String, default: '' },
  feeId: { type: [Number, String], default: null },
});
const emit = defineEmits(['update:visible', 'submit-success']);

const formRef = ref(null);
const submitting = ref(false);
const studentOptions = ref([]);
const studentLoading = ref(false);
const courseOptions = ref([]);
const courseLoading = ref(false);
const classOptions = ref([]);

const form = ref({
  studentId: null, courseId: null, classId: null, enrollmentId: null,
  feeType: 'TUITION', amountReceivable: null, amountPaid: null,
  paymentMethod: 'CASH', status: 'PAID', payTime: '', remark: '',
});

const rules = {
  studentId: [{ required: true, message: '请选择学员', trigger: 'change' }],
  courseId: [{ required: true, message: '请选择课程', trigger: 'change' }],
  feeType: [{ required: true, message: '请选择费用类型', trigger: 'change' }],
  amountReceivable: [{ required: true, message: '请输入应缴金额', trigger: 'blur' }],
  amountPaid: [{ required: true, message: '请输入实缴金额', trigger: 'blur' }],
  paymentMethod: [{ required: true, message: '请选择支付方式', trigger: 'change' }],
  payTime: [{ required: true, message: '请选择收费时间', trigger: 'change' }],
};

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

async function onOpen() {
  reset();
  // 加载班级列表
  listClass({ page: 1, pageSize: 200 }).then(res => {
    if (res.success) classOptions.value = res.data?.records || res.rows || [];
  });
  if (props.feeId) {
    try {
      const res = await getFee(props.feeId);
      if (res.success && res.data) {
        const f = res.data;
        form.value = {
          studentId: f.studentId || null, courseId: f.courseId || null,
          classId: f.classId || null, enrollmentId: f.enrollmentId || null,
          feeType: f.feeType || 'TUITION', amountReceivable: f.amountReceivable || null,
          amountPaid: f.amountPaid || null, paymentMethod: f.paymentMethod || 'CASH',
          status: f.status || 'PAID', payTime: f.payTime || '', remark: f.remark || '',
        };
      }
    } catch {
      ElMessage.error('加载收费详情失败');
      emit('update:visible', false);
    }
  }
}

function reset() {
  form.value = { studentId: null, courseId: null, classId: null, enrollmentId: null, feeType: 'TUITION', amountReceivable: null, amountPaid: null, paymentMethod: 'CASH', status: 'PAID', payTime: '', remark: '' };
  studentOptions.value = []; courseOptions.value = []; classOptions.value = [];
  nextTick(() => formRef.value?.clearValidate());
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false);
  if (!valid) return;
  submitting.value = true;
  try {
    const data = { ...form.value }; let res;
    if (props.feeId) { data.id = props.feeId; res = await updateFee(data); }
    else { res = await addFee(data); }
    if (res.success) { ElMessage.success(props.feeId ? '修改成功' : '新增成功'); emit('update:visible', false); emit('submit-success'); }
    else { ElMessage.error(res.msg || '操作失败'); }
  } finally { submitting.value = false; }
}
</script>
