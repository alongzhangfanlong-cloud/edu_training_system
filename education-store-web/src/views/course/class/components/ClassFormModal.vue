<!-- 班级新增/编辑弹窗 -->
<template>
  <el-dialog :model-value="visible" :title="title" width="600px" destroy-on-close append-to-body @open="onOpen" @update:model-value="(val) => emit('update:visible', val)">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="班级名称" prop="name">
            <el-input v-model="form.name" placeholder="请输入班级名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="课程" prop="courseId">
            <el-select v-model="form.courseId" placeholder="请选择课程" style="width:100%">
              <el-option v-for="c in courseOptions" :key="c.id" :label="c.name" :value="c.id" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="教师" prop="teacherId">
            <el-select v-model="form.teacherId" placeholder="请选择教师" filterable remote :remote-method="searchTeachers" :loading="teacherLoading" style="width:100%" clearable>
              <el-option v-for="t in teacherOptions" :key="t.id" :label="t.realName" :value="t.id" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="默认教室">
            <el-select v-model="form.classroomId" placeholder="请选择教室" style="width:100%" clearable>
              <el-option v-for="r in roomOptions" :key="r.id" :label="r.name" :value="r.id" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="最多人数">
            <el-input-number v-model="form.maxStudents" :min="1" style="width:100%" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="状态" prop="status">
            <el-select v-model="form.status" style="width:100%">
              <el-option v-for="d in dicts.class_status" :key="d.value" :label="d.label" :value="d.value" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="开课日期">
            <el-date-picker v-model="form.startDate" type="date" style="width:100%" value-format="YYYY-MM-DD" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="结课日期">
            <el-date-picker v-model="form.endDate" type="date" style="width:100%" value-format="YYYY-MM-DD" />
          </el-form-item>
        </el-col>
      </el-row>
      <!-- 上课规则：每周上课日 + 时间 -->
      <el-divider content-position="left">上课规则</el-divider>
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="每周上课日">
            <el-checkbox-group v-model="form.weekDays">
              <el-checkbox v-for="d in weekDayOptions" :key="d.value" :value="d.value" :label="d.label" />
            </el-checkbox-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="上课时间">
            <el-time-picker v-model="form.startTime" style="width:100%" value-format="HH:mm:ss" placeholder="开始时间" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="下课时间">
            <el-time-picker v-model="form.endTime" style="width:100%" value-format="HH:mm:ss" placeholder="结束时间" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="班级说明">
        <el-input v-model="form.description" type="textarea" :rows="3" />
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
import { getClass, addClass, updateClass } from '@/api/class/classes';
import { listCourse } from '@/api/course/courses';
import { listTeacher } from '@/api/teaching/teachers';
import { listClassroom } from '@/api/course/classrooms';
import { useDictsStore } from '@/stores/dicts';

const props = defineProps({
  visible: { type: Boolean, default: false },
  title: { type: String, default: '' },
  classId: { type: [Number, String], default: null },
});
const emit = defineEmits(['update:visible', 'submit-success']);

const { dicts, loadDict } = useDictsStore();
const formRef = ref(null); const submitting = ref(false);
const courseOptions = ref([]);
const teacherOptions = ref([]); const teacherLoading = ref(false);
const roomOptions = ref([]);
let searchTimer = null; let searchSeq = 0;

const form = ref({ name: '', courseId: null, teacherId: null, classroomId: null, maxStudents: 20, status: 'RECRUITING', startDate: '', endDate: '', weekDays: [], startTime: '', endTime: '', description: '' });
const weekDayOptions = [
  { value: 1, label: '周一' }, { value: 2, label: '周二' }, { value: 3, label: '周三' },
  { value: 4, label: '周四' }, { value: 5, label: '周五' }, { value: 6, label: '周六' }, { value: 7, label: '周日' },
];
const rules = {
  name: [{ required: true, message: '请输入班级名称', trigger: 'blur' }],
  courseId: [{ required: true, message: '请选择课程', trigger: 'change' }],
  teacherId: [{ required: true, message: '请选择教师', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }],
};

async function onOpen() {
  reset();
  await loadDict('class_status');
  await Promise.all([loadCourses(), loadRooms()]);
  if (props.classId) {
    const res = await getClass(props.classId);
    if (res.success && res.data) {
      const c = res.data.data || res.data;
      form.value = { name: c.name || '', courseId: c.courseId || null, teacherId: c.teacherId || null, classroomId: c.classroomId || null, maxStudents: c.maxStudents || 20, status: c.status || 'RECRUITING', startDate: c.startDate || '', endDate: c.endDate || '', weekDays: parseWeekDays(c.weekDays), startTime: c.startTime || '', endTime: c.endTime || '', description: c.description || '' };
      if (c.teacherId && c.teacherName) teacherOptions.value = [{ id: c.teacherId, realName: c.teacherName }];
    }
  }
}

function loadCourses() { listCourse({ page: 1, size: 999 }).then(res => { if (res.success) courseOptions.value = res.data?.records || res.rows || []; }); }
function loadRooms() { listClassroom({ page: 1, size: 999 }).then(res => { if (res.success) roomOptions.value = res.data?.records || res.rows || []; }); }

function searchTeachers(keyword) {
  clearTimeout(searchTimer);
  if (!keyword) { teacherOptions.value = []; return; }
  teacherLoading.value = true;
  searchTimer = setTimeout(() => { searchSeq++; const seq = searchSeq;
    listTeacher({ page: 1, size: 100, keyword }).then(res => {
      if (seq !== searchSeq) return;
      if (res.success) teacherOptions.value = res.data?.records || res.rows || [];
    }).finally(() => { if (seq === searchSeq) teacherLoading.value = false; });
  }, 300);
}

function reset() {
  form.value = { name: '', courseId: null, teacherId: null, classroomId: null, maxStudents: 20, status: 'RECRUITING', startDate: '', endDate: '', weekDays: [], startTime: '', endTime: '', description: '' };
  teacherOptions.value = []; nextTick(() => formRef.value?.clearValidate());
}

function parseWeekDays(val) {
  if (!val) return [];
  if (Array.isArray(val)) return val;
  if (typeof val === 'string') {
    try { return JSON.parse(val); } catch { return val.split(',').map(Number).filter(Boolean); }
  }
  return [];
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false); if (!valid) return;
  submitting.value = true;
  try {
    const data = { ...form.value };
    // 转换 weekDays 为逗号分隔存储
    data.weekDays = Array.isArray(data.weekDays) ? data.weekDays.join(',') : data.weekDays;
    let res;
    if (props.classId) { data.id = props.classId; res = await updateClass(data); }
    else { res = await addClass(data); }
    if (res.success) { ElMessage.success(props.classId ? '修改成功' : '新增成功'); emit('update:visible', false); emit('submit-success'); }
    else { ElMessage.error(res.msg || '操作失败'); }
  } finally { submitting.value = false; }
}
</script>
