<!-- 学员新增/编辑弹窗 -->
<template>
  <el-dialog :model-value="visible" :title="title" width="700px" destroy-on-close append-to-body @open="onOpen" @update:model-value="(val) => emit('update:visible', val)">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="学员姓名" prop="name">
            <el-input v-model="form.name" placeholder="请输入学员姓名" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="性别" prop="gender">
            <el-select v-model="form.gender" placeholder="请选择性别" style="width:100%">
              <el-option v-for="d in dicts.sys_user_sex" :key="d.value" :label="d.label" :value="d.value" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="手机号" prop="mobile">
            <el-input v-model="form.mobile" placeholder="请输入手机号" maxlength="11" @input="form.mobile = form.mobile?.replace(/\D/g, '')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="家长手机号" prop="parentMobile">
            <el-input v-model="form.parentMobile" placeholder="请输入家长手机号" maxlength="11" @input="form.parentMobile = form.parentMobile?.replace(/\D/g, '')" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="家长姓名">
            <el-input v-model="form.parentName" placeholder="请输入家长姓名" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="亲属关系">
            <el-select v-model="form.parentRelation" placeholder="请选择亲属关系" style="width:100%" clearable>
              <el-option v-for="d in dicts.parent_relation" :key="d.value" :label="d.label" :value="d.value" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="出生日期" prop="birthDate">
            <el-date-picker v-model="form.birthDate" type="date" placeholder="请选择出生日期" style="width:100%" value-format="YYYY-MM-DD" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="年级" prop="grade">
            <el-input v-model="form.grade" placeholder="请输入年级" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="就读学校" prop="school">
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
          <el-form-item label="报名科目">
            <el-select v-model="form.intendedCourseId" placeholder="请选择科目" filterable style="width:100%" clearable>
              <el-option v-for="c in courseOptions" :key="c.id" :label="c.name" :value="c.id" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="来源渠道" prop="source">
            <el-select v-model="form.source" placeholder="请选择来源渠道" style="width:100%" clearable>
              <el-option v-for="d in dicts.student_source" :key="d.value" :label="d.label" :value="d.value" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="状态" prop="status">
            <el-select v-model="form.status" placeholder="请选择状态" style="width:100%" :disabled="!props.studentId">
              <el-option v-for="d in dicts.student_status" :key="d.value" :label="d.label" :value="d.value" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="标签">
            <el-input v-model="form.tags" placeholder="多个标签用逗号分隔" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="微信昵称">
            <el-input v-model="form.wechatNickname" placeholder="请输入微信昵称" />
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
import { getStudent, addStudent, updateStudent } from '@/api/student/students';
import { listUser } from '@/api/system/user';
import { listSubject } from '@/api/course/subjects';
import { useDictsStore } from '@/stores/dicts';

const props = defineProps({
  visible: { type: Boolean, default: false },
  title: { type: String, default: '' },
  studentId: { type: [Number, String], default: null },
});
const emit = defineEmits(['update:visible', 'submit-success']);

const { dicts, loadDicts } = useDictsStore();
const formRef = ref(null);
const submitting = ref(false);
const counselorOptions = ref([]);
const counselorLoading = ref(false);

let searchTimer = null;
let searchSeq = 0;

const courseOptions = ref([]);
const courseLoading = ref(false);
let courseTimer = null;
let courseSeq = 0;

const form = ref({
  name: '', mobile: '', parentMobile: '', gender: '', birthDate: '',
  grade: '', school: '', parentName: '', parentRelation: '',
  counselorId: null, intendedCourseId: null, intendedCourseName: '', source: '',
  status: 'ENROLLED', tags: '', wechatNickname: '', remark: '',
});

const validateMobileOptional = (rule, value, callback) => {
  if (value && !/^1[3-9]\d{9}$/.test(value)) {
    callback(new Error('请输入正确的手机号格式'));
  } else {
    callback();
  }
};

const rules = {
  name: [{ required: true, message: '请输入学员姓名', trigger: 'blur' }],
  mobile: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' },
  ],
  parentMobile: [
    { validator: validateMobileOptional, trigger: 'blur' }
  ],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
};

// 加载顾问列表（不传 keyword 则加载全部）
async function loadCounselors(keyword) {
  counselorLoading.value = true;
  try {
    const res = await listUser({ page: 1, size: 100, keyword: keyword || '' });
    if (res.success) counselorOptions.value = res.data?.records || res.rows || [];
  } finally {
    counselorLoading.value = false;
  }
}

// 远程搜索顾问（防抖 + 竞态，用户输入时使用）
function searchCounselors(keyword) {
  clearTimeout(searchTimer);
  if (!keyword) { counselorOptions.value = []; return; }
  counselorLoading.value = true;
  searchTimer = setTimeout(() => {
    searchSeq++;
    const seq = searchSeq;
    listUser({ page: 1, size: 100, keyword }).then(res => {
      if (seq !== searchSeq) return;
      if (res.success) counselorOptions.value = res.data?.records || res.rows || [];
    }).finally(() => {
      if (seq === searchSeq) counselorLoading.value = false;
    });
  }, 300);
}

// 加载科目列表
async function loadSubjects() {
  try {
    const res = await listSubject({ page: 1, size: 200 });
    if (res.success) courseOptions.value = res.data?.records || res.rows || [];
  } catch { /* ignore */ }
}

async function onOpen() {
  reset();
  await loadDicts(['sys_user_sex', 'student_status', 'student_source', 'parent_relation']);
  await loadSubjects();
  // 预加载顾问列表，确保编辑时下拉能匹配到名称
  await loadCounselors('');
  if (props.studentId) {
    const res = await getStudent(props.studentId);
    if (res.success && res.data) {
      const s = res.data;
      form.value = {
        name: s.name || '',
        mobile: s.mobile || '',
        parentMobile: s.parentMobile || '',
        gender: s.gender || '',
        birthDate: s.birthDate || '',
        grade: s.grade || '',
        school: s.school || '',
        parentName: s.parentName || '',
        parentRelation: s.parentRelation || '',
        counselorId: s.counselorId || null,
        intendedCourseId: s.intendedCourseId || null,
        intendedCourseName: s.intendedCourseName || '',
        source: s.source || '',
        status: s.status || 'ENROLLED',
        tags: s.tags || '',
        wechatNickname: s.wechatNickname || '',
        remark: s.remark || '',
      };
      // 回填课程选项
      if (s.intendedCourseId && s.intendedCourseName) {
        courseOptions.value = [{ id: s.intendedCourseId, name: s.intendedCourseName }];
      }
    }
  }
}

function reset() {
  form.value = {
    name: '', mobile: '', parentMobile: '', gender: '', birthDate: '',
    grade: '', school: '', parentName: '', parentRelation: '',
    counselorId: null, intendedCourseId: null, intendedCourseName: '', source: '',
    status: 'ENROLLED', tags: '', wechatNickname: '', remark: '',
  };
  counselorOptions.value = [];
  courseOptions.value = [];
  nextTick(() => formRef.value?.clearValidate());
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false);
  if (!valid) return;
  submitting.value = true;
  try {
    const data = { ...form.value };
    // 带上课程名称（用于持久化存储）
    if (data.intendedCourseId) {
      const selected = courseOptions.value.find(c => c.id === data.intendedCourseId);
      if (selected) data.intendedCourseName = selected.name;
    }
    let res;
    if (props.studentId) {
      data.id = props.studentId;
      res = await updateStudent(data);
    } else {
      res = await addStudent(data);
    }
    if (res.success) {
      ElMessage.success(props.studentId ? '修改成功' : '新增成功');
      emit('update:visible', false);
      emit('submit-success');
    } else {
      ElMessage.error(res.msg || '操作失败');
    }
  } finally { submitting.value = false; }
}
</script>
