<!-- 意向学员新增/编辑弹窗 -->
<template>
  <el-dialog :model-value="visible" :title="title" width="700px" destroy-on-close append-to-body @open="onOpen" @update:model-value="(val) => emit('update:visible', val)">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="学员姓名" prop="name">
            <el-input v-model="form.name" placeholder="请输入学员姓名" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="联系电话" prop="mobile">
            <el-input v-model="form.mobile" placeholder="请输入联系电话" maxlength="11" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="家长姓名" prop="parentName">
            <el-input v-model="form.parentName" placeholder="请输入家长姓名" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="家长手机号">
            <el-input v-model="form.parentMobile" placeholder="请输入家长手机号" maxlength="11" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="亲属关系">
            <el-select v-model="form.parentRelation" placeholder="请选择亲属关系" style="width:100%" clearable>
              <el-option v-for="d in dicts.parent_relation" :key="d.value" :label="d.label" :value="d.value" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="微信昵称">
            <el-input v-model="form.wechatNickname" placeholder="请输入微信昵称" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="性别" prop="gender">
            <el-select v-model="form.gender" placeholder="请选择性别" style="width:100%">
              <el-option v-for="d in dicts.sys_user_sex" :key="d.value" :label="d.label" :value="d.value" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="出生日期">
            <el-date-picker v-model="form.birthDate" type="date" placeholder="请选择出生日期" style="width:100%" value-format="YYYY-MM-DD" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="年级">
            <el-input v-model="form.grade" placeholder="请输入年级" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="就读学校">
            <el-input v-model="form.school" placeholder="请输入就读学校" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="顾问" prop="counselorId">
            <el-select v-model="form.counselorId" placeholder="请选择顾问" filterable remote :remote-method="searchCounselors" :loading="counselorLoading" style="width:100%" clearable>
              <el-option v-for="u in counselorOptions" :key="u.id" :label="u.realName" :value="u.id" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="意向课程">
            <el-input v-model="form.intendedCourseName" placeholder="请输入意向课程名称" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="来源渠道">
            <el-select v-model="form.source" placeholder="请选择来源渠道" style="width:100%" clearable>
              <el-option v-for="d in dicts.student_source" :key="d.value" :label="d.label" :value="d.value" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="意向等级">
            <el-select v-model="form.interestLevel" placeholder="请选择意向等级" style="width:100%" clearable>
              <el-option v-for="d in dicts.interest_level" :key="d.value" :label="d.label" :value="d.value" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="跟进状态" prop="followStatus">
            <el-select v-model="form.followStatus" placeholder="请选择跟进状态" style="width:100%">
              <el-option v-for="d in dicts.prospect_follow_status" :key="d.value" :label="d.label" :value="d.value" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="备注">
        <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
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
import { getProspect, addProspect, updateProspect } from '@/api/student/prospects';
import { listUser } from '@/api/system/user';
import { useDictsStore } from '@/stores/dicts';

const props = defineProps({
  visible: { type: Boolean, default: false },
  title: { type: String, default: '' },
  prospectId: { type: [Number, String], default: null },
});
const emit = defineEmits(['update:visible', 'submit-success']);

const { dicts, loadDicts } = useDictsStore();
const formRef = ref(null);
const submitting = ref(false);

const counselorOptions = ref([]);
const counselorLoading = ref(false);

let counselorTimer = null;
let counselorSeq = 0;

const form = ref({
  name: '', mobile: '', parentName: '', parentMobile: '', parentRelation: '',
  wechatNickname: '', gender: '', birthDate: '', grade: '', school: '',
  counselorId: null, intendedCourseName: '', source: '', interestLevel: '',
  followStatus: 'NEW', remark: '',
});

const rules = {
  name: [{ required: true, message: '请输入学员姓名', trigger: 'blur' }],
  mobile: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  followStatus: [{ required: true, message: '请选择跟进状态', trigger: 'change' }],
};

// 远程搜索顾问
function searchCounselors(keyword) {
  clearTimeout(counselorTimer);
  if (!keyword) { counselorOptions.value = []; return; }
  counselorLoading.value = true;
  counselorTimer = setTimeout(() => {
    counselorSeq++;
    const seq = counselorSeq;
    listUser({ page: 1, size: 100, keyword }).then(res => {
      if (seq !== counselorSeq) return;
      if (res.success) counselorOptions.value = res.data?.records || res.rows || [];
    }).finally(() => {
      if (seq === counselorSeq) counselorLoading.value = false;
    });
  }, 300);
}

async function onOpen() {
  reset();
  await loadDicts(['sys_user_sex', 'student_source', 'prospect_follow_status', 'interest_level', 'parent_relation']);
  if (props.prospectId) {
    const res = await getProspect(props.prospectId);
    if (res.success && res.data) {
      const s = res.data;
      form.value = {
        name: s.name || '', mobile: s.mobile || '', parentName: s.parentName || '',
        parentMobile: s.parentMobile || '', parentRelation: s.parentRelation || '',
        wechatNickname: s.wechatNickname || '', gender: s.gender || '',
        birthDate: s.birthDate || '', grade: s.grade || '', school: s.school || '',
        counselorId: s.counselorId || null, intendedCourseName: s.intendedCourseName || '',
        source: s.source || '', interestLevel: s.interestLevel || '',
        followStatus: s.followStatus || 'NEW', remark: s.remark || '',
      };
      if (s.counselorId && s.counselorName) {
        counselorOptions.value = [{ id: s.counselorId, realName: s.counselorName }];
      }
    }
  }
}

function reset() {
  form.value = {
    name: '', mobile: '', parentName: '', parentMobile: '', parentRelation: '',
    wechatNickname: '', gender: '', birthDate: '', grade: '', school: '',
    counselorId: null, intendedCourseName: '', source: '', interestLevel: '',
    followStatus: 'NEW', remark: '',
  };
  counselorOptions.value = [];
  nextTick(() => formRef.value?.clearValidate());
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false);
  if (!valid) return;
  submitting.value = true;
  try {
    const data = { ...form.value };
    let res;
    if (props.prospectId) {
      data.id = props.prospectId;
      res = await updateProspect(data);
    } else {
      res = await addProspect(data);
    }
    if (res.success) {
      ElMessage.success(props.prospectId ? '修改成功' : '新增成功');
      emit('update:visible', false);
      emit('submit-success');
    } else {
      ElMessage.error(res.msg || '操作失败');
    }
  } finally { submitting.value = false; }
}
</script>
